package me.zach.DesertMC.GameMechanics;


import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.reflect.StructureModifier;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTEntity;
import de.tr7zw.nbtapi.NBTItem;
import me.ench.main.RefineryInventory;
import me.ench.main.RefineryUtils;
import me.zach.DesertMC.ClassManager.CoruManager.EventsForCorruptor;
import me.zach.DesertMC.ClassManager.ScoutManager.EventsForScout;
import me.zach.DesertMC.ClassManager.TankManager.EventsForTank;
import me.zach.DesertMC.ClassManager.TravellerEvents;
import me.zach.DesertMC.ClassManager.WizardManager.EventsForWizard;
import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.GameMechanics.EXPMilesstones.MilestonesUtil;
import me.zach.DesertMC.GameMechanics.hitbox.Hitbox;
import me.zach.DesertMC.GameMechanics.hitbox.HitboxListener;
import me.zach.DesertMC.GameMechanics.hitbox.HitboxManager;
import me.zach.DesertMC.GameMechanics.npcs.StreakPolice;
import me.zach.DesertMC.Prefix;
import me.zach.DesertMC.ScoreboardManager.FScoreboardManager;
import me.zach.DesertMC.Utils.Config.ConfigUtils;
import me.zach.DesertMC.Utils.MiscUtils;
import me.zach.DesertMC.Utils.Particle.ParticleEffect;
import me.zach.DesertMC.Utils.PlayerUtils;
import me.zach.DesertMC.Utils.RankUtils.Rank;
import me.zach.DesertMC.Utils.ench.CustomEnch;
import me.zach.DesertMC.Utils.nbt.NBTUtil;
import me.zach.DesertMC.Utils.packet.wrappers.WrapperPlayClientUseEntity;
import me.zach.DesertMC.Utils.packet.wrappers.WrapperPlayServerWindowItems;
import me.zach.DesertMC.Utils.structs.Pair;
import me.zach.DesertMC.anvil.FallenAnvilInventory;
import me.zach.DesertMC.cosmetics.Cosmetic;
import me.zach.DesertMC.events.FallenDeathEvent;
import me.zach.artifacts.events.ArtifactEvents;
import me.zach.artifacts.gui.inv.items.CreeperTrove;
import me.zach.databank.saver.Key;
import me.zach.databank.saver.PlayerData;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.CachedServerIcon;
import org.bukkit.util.Vector;
import xyz.fallenmc.risenboss.main.RisenBoss;
import xyz.fallenmc.risenboss.main.RisenMain;
import xyz.fallenmc.risenboss.main.utils.RisenUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

import static com.comphenix.protocol.PacketType.Play.Server.SET_SLOT;
import static com.comphenix.protocol.PacketType.Play.Server.WINDOW_ITEMS;
import static me.zach.DesertMC.DesertMain.lastdmgers;

public class Events implements Listener{
	public static Set<UUID> invincible = new HashSet<>();
	Plugin main = DesertMain.getInstance;
	public static HashMap<UUID,Integer> ks = new HashMap<>();
	public static HashMap<Integer, ItemStack> arrowArray = new HashMap<>();
	static final HashMap<UUID, Float> blocking = new HashMap<>();
	static final DecimalFormat wphFormat = new DecimalFormat();
	static{
		wphFormat.setMaximumFractionDigits(4);

	}
	private CachedServerIcon nft = null;
	public Events(){
		File serverDir = new File(".").getAbsoluteFile();
		File nftFile = new File(serverDir, "nft.png");
		if(nftFile.exists()){
			try{
				this.nft = Bukkit.loadServerIcon(nftFile);
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		PacketListener tooltips = new PacketListener() {
			final ListeningWhitelist whitelist = ListeningWhitelist.newBuilder().types(WINDOW_ITEMS, SET_SLOT).build();
			public void onPacketSending(PacketEvent event){
				if(event.getPlayer().getGameMode() == GameMode.CREATIVE) return; //some weird inconsistencies with the creative inventory that I don't want to deal with
				if(event.getPacketType() == WINDOW_ITEMS){
					StructureModifier<ItemStack[]> modifier = event.getPacket().getItemArrayModifier();
					ItemStack[] items = modifier.read(0);
					boolean changed = false;
					for(int i = 0; i < items.length; i++){
						ItemStack item = items[i];
						ItemStack newItem = applyChanges(item);
						if(newItem != item){
							items[i] = newItem;
							changed = true;
						}
					}
					if(changed) modifier.write(0, items);
				}else if(event.getPacketType() == SET_SLOT){
					StructureModifier<ItemStack> modifier = event.getPacket().getItemModifier();
					ItemStack item = modifier.read(0);
					ItemStack newItem = applyChanges(item);
					if(newItem != item){
						modifier.write(0, newItem);
					}
				}
			}

			public void onPacketReceiving(PacketEvent event){

			}

			public ListeningWhitelist getSendingWhitelist(){
				return whitelist;
			}

			public ListeningWhitelist getReceivingWhitelist(){
				return ListeningWhitelist.EMPTY_WHITELIST;
			}

			public Plugin getPlugin(){
				return DesertMain.getInstance;
			}

			private ItemStack applyChanges(ItemStack start){
				if(start == null || start.getType() == Material.AIR || !start.hasItemMeta() || !start.getItemMeta().hasLore()) return start;
				else start = start.clone();
				NBTItem nbt = new NBTItem(start);
				Double wph = NBTUtil.getCustomAttr(nbt, "WEIGHT_ADD", Double.class);
				Double weight = NBTUtil.getCustomAttr(nbt, "WEIGHT", Double.class);
				if(wph != null && weight != null){
					ItemMeta meta = start.getItemMeta();
					List<String> lore = meta.getLore();
					lore.add("");
					lore.add(ChatColor.DARK_GRAY + "Current weight: " + ChatColor.GRAY + wphFormat.format(weight) + "%");
					lore.add(ChatColor.DARK_GRAY + "Weight add per hit: " + ChatColor.GRAY + wphFormat.format(wph) + "%");
					meta.setLore(lore);
					start.setItemMeta(meta);
					return start;
				}else return start;
			}
		};
		ProtocolLibrary.getProtocolManager().addPacketListener(tooltips);
	}

	public static ItemStack getItemUsed(Entity damager){
		Player playerDmgr = damager instanceof Player ? (Player) damager : null;
		return damager instanceof Arrow ? arrowArray.get(damager.getEntityId()) : (playerDmgr != null ? playerDmgr.getItemInHand() : null); //arrow? get from arrow array. player? get item in hand. neither? null.
	}

	public static void checkItemLives(Player player){
		Inventory inventory = player.getInventory();
		boolean modified = false;
		ItemStack[] contents = inventory.getContents();
		for(int i = 0; i<contents.length; i++){
			ItemStack item = contents[i];
			if(item != null && item.getType() != Material.AIR){
				NBTItem nbt = new NBTItem(item);
				if(NBTUtil.hasCustomKey(nbt, "LIVES")){
					int lives = NBTUtil.getCustomAttr(nbt, "LIVES", int.class);
					if(lives - 1 <= 0){
						contents[i] = null;
						player.sendMessage(ChatColor.RED + "Your " + item.getItemMeta().getDisplayName() + ChatColor.RED + " ran out of lives!");
					}else contents[i] = NBTUtil.setLives(item, lives - 1);
					modified = true;
				}
			}
		}
		if(modified) inventory.setContents(contents);
	}

	@EventHandler
	public void iceMelt(BlockFadeEvent event){
		if(event.getBlock().getType() != Material.FIRE) event.setCancelled(true);
	}

	@EventHandler
	public void weatherChange(WeatherChangeEvent event){
		World world = event.getWorld();
		if(world.hasStorm()) world.setStorm(false);
		event.setCancelled(true);
	}

	@EventHandler
	public void projectileLand(ProjectileHitEvent event){
		Entity entity = event.getEntity();
		if(entity instanceof Arrow){
			Arrow arrow = (Arrow) entity;
			if(arrow.isDead() || arrow.isOnGround() || new NBTEntity(arrow).getBoolean("inGround")){
				arrowArray.remove(arrow.getEntityId());
				Bukkit.getScheduler().runTaskLater(DesertMain.getInstance, arrow::remove, 50);
			}
		}
	}

	@EventHandler
	public void nft(ServerListPingEvent event){
		if(nft != null && Math.random() < 0.002){
			event.setServerIcon(nft);
		}
	}

	public void attackMod(EntityDamageByEntityEvent event){
		ItemStack itemUsed = getItemUsed(event.getDamager());
		if(event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK){
			double damage = event.getDamage();
			if(itemUsed != null && itemUsed.getType() != Material.AIR){
				NBTItem nbt = new NBTItem(itemUsed);
				float dmgMod = NBTUtil.getCustomAttrFloat(nbt, "ATTACK", 1);
				if(dmgMod != 1) event.setDamage(damage * dmgMod);
			}
		}
	}

	public void defenseMod(EntityDamageEvent event){
		Entity damaged = event.getEntity();
		if(damaged instanceof LivingEntity){
			double damage = event.getDamage();
			ItemStack[] armor = ((LivingEntity) damaged).getEquipment().getArmorContents();
			float damagePercent = 100;
			for(ItemStack item : armor){
				if(item != null){
					float defenseMod = NBTUtil.getCustomAttrFloat(item, "DEFENSE", 0);
					damagePercent -= defenseMod;
				}
			}
			if(damagePercent != 100){
				event.setDamage(damage * (damagePercent / 100));
			}
		}
	}

	@EventHandler
	public void cancelPlayerChangeBlock(EntityChangeBlockEvent event){
		event.setCancelled(true);
	}

	@EventHandler
	public void cancelBlockFlow(BlockFromToEvent event){
		event.setCancelled(true);
	}

	@EventHandler
	public void nonOwnerPickup(PlayerPickupItemEvent event){
		Item item = event.getItem();
		String uuid = event.getPlayer().getUniqueId().toString();
		NBTEntity nbt = new NBTEntity(item);
		String owner = nbt.getString("Owner");
		System.out.println("cancelled before: " + event.isCancelled());
		if(owner != null && !owner.isEmpty() && !owner.equals(uuid)){
			event.setCancelled(item.getTicksLived() < 99999999);
		}
	}

	@EventHandler
	public void arrowShoot(ProjectileLaunchEvent event){
		for(CustomEnch ce : CustomEnch.values()){
			ce.onShoot(event);
		}
		if(event.getEntity().getShooter() instanceof Player && event.getEntity() instanceof Arrow){
			Arrow arrow = (Arrow) event.getEntity();
			Player player = (Player) event.getEntity().getShooter();
			arrowArray.put(arrow.getEntityId(), player.getInventory().getItemInHand());
			ArtifactEvents.shootEvent(event);
			Cosmetic cosmetic = Cosmetic.getSelected(player, Cosmetic.CosmeticType.ARROW_TRAIL);
			if(cosmetic != null) cosmetic.activateArrow(arrow, ConfigUtils.getData(player).getArtifactData().bowS());
		}
	}

	@EventHandler
	public void illegalCommandSend(PlayerCommandPreprocessEvent event){
			boolean plugins = event.getMessage().startsWith("/plugins");
			boolean pl = event.getMessage().equalsIgnoreCase("/pl");
			boolean pl2 = event.getMessage().startsWith("/pl ");
			boolean gc = event.getMessage().equalsIgnoreCase("/gc");
			boolean icanhasbukkit = event.getMessage().startsWith("/icanhasbukkit");
			boolean unknown = event.getMessage().startsWith("/?");
			boolean version = event.getMessage().startsWith("/version");
			boolean ver = event.getMessage().startsWith("/ver");
			boolean bukkitplugin = event.getMessage().startsWith("/bukkit:plugins");
			boolean bukkitpl = event.getMessage().startsWith("/bukkit:pl");
			boolean bukkitunknown = event.getMessage().startsWith("/bukkit:?");
			boolean about = event.getMessage().startsWith("/about");
			boolean a = event.getMessage().equalsIgnoreCase("/a");
			boolean bukkitabout = event.getMessage().startsWith("/bukkit:about");
			boolean bukkita = event.getMessage().startsWith("/bukkit:a");
			boolean bukkitversion = event.getMessage().startsWith("/bukkit:version");
			boolean bukkitver = event.getMessage().startsWith("/bukkit:ver");
			boolean bukkithelp = event.getMessage().startsWith("/bukkit:help");
			boolean help = event.getMessage().startsWith("/minecraft:help");


		if(plugins || pl || pl2 || gc || icanhasbukkit || unknown || version || ver || bukkitplugin || bukkitpl || bukkitunknown || about || a || bukkitabout || bukkita || help || bukkithelp || bukkitver || bukkitversion){
			if(MiscUtils.isAdmin(event.getPlayer())){
				return;
			}
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "You do not have permission to use this command.");
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void craft(CraftItemEvent event){
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void use(PlayerInteractEvent event){
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		Action action = event.getAction();
		boolean isCreative = player.getGameMode() == GameMode.CREATIVE;
		boolean placeBlock = event.isBlockInHand() && action == Action.RIGHT_CLICK_BLOCK;
		boolean destroyBlock = action == Action.LEFT_CLICK_BLOCK;
		if(destroyBlock && isCreative){
			event.setUseInteractedBlock(Event.Result.DEFAULT);
			event.setUseItemInHand(Event.Result.DEFAULT);
		}else if(placeBlock && isCreative){
			event.setUseItemInHand(Event.Result.DEFAULT);
			event.setUseInteractedBlock(Event.Result.DEFAULT);
		}else if(item == null){
			event.setUseItemInHand(Event.Result.DENY);
			event.setUseInteractedBlock(Event.Result.DENY);
		}else if((action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) && ((NBTUtil.getCustomAttrBoolean(item, "USABLE") && !HitboxListener.isInSafeZone(player.getLocation())) || item.getType().name().endsWith("SWORD"))){
			event.setUseItemInHand(Event.Result.DEFAULT);
			event.setUseInteractedBlock(Event.Result.DENY);
			player.setFoodLevel(19);
		}else{
			event.setUseInteractedBlock(Event.Result.DENY);
			event.setUseItemInHand(Event.Result.DENY);
			player.updateInventory();
		}
		if(action == Action.RIGHT_CLICK_BLOCK){
			Block block = event.getClickedBlock();
			if(block.getType() == Material.ANVIL){
				player.openInventory(new FallenAnvilInventory().getInventory());
			}else if(block.getType() == Material.ENCHANTMENT_TABLE){
				RefineryInventory inv = RefineryUtils.instance.get(player.getUniqueId());
				if(inv == null) RefineryUtils.instance.put(player.getUniqueId(), inv = new RefineryInventory());
				inv.openRefineryInventory(player, false);
			}else if(block.getType() == Material.ENDER_CHEST){
				event.setUseInteractedBlock(Event.Result.ALLOW);
			}
		}
	}

	@EventHandler
	public void inventoryOpen(InventoryOpenEvent event){
		Inventory inv = event.getInventory();
		if(inv instanceof AnvilInventory || inv instanceof EnchantingInventory){
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void cancelFish(PlayerFishEvent event){
		if(event.getState() == PlayerFishEvent.State.CAUGHT_FISH){
			event.setCancelled(true);
		}
	}

	public void travellerTank(EntityDamageByEntityEvent e){
		if(e.getEntity() instanceof Player) {
			UUID uuid = e.getEntity().getUniqueId();
			if(ConfigUtils.getLevel("tank", uuid) > 5 && ConfigUtils.findClass(uuid).equals("tank")){
				Set<Block> blockSet = TravellerEvents.travelled.get(uuid);
				if (blockSet != null) {
					double multiplier = 1 - Math.floorDiv(blockSet.size(), 100) * 0.01;
					e.setDamage(e.getDamage() * multiplier);
				}
			}
		}
	}

	public void travellerCoru(EntityDamageByEntityEvent e){
		UUID uuid = e.getDamager().getUniqueId();
		if(ConfigUtils.findClass(uuid).equals("corrupter") && ConfigUtils.getLevel("corrupter", uuid) > 5){
			Set<Block> blockSet = TravellerEvents.travelled.get(uuid);
			if(blockSet != null){
				double multiplier = 1 + (Math.floorDiv(blockSet.size(), 100) * 0.01);
				e.setDamage(e.getDamage() * multiplier);
			}
		}
	}

	public void removeFallDMG(EntityDamageEvent event){
		if(event.getCause().equals(EntityDamageEvent.DamageCause.FALL)){
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onDamage(EntityDamageEvent event){
		if(!(event.getEntity() instanceof Damageable) || event.getEntity() instanceof Item || MiscUtils.canDamage(event.getEntity()) != null){
			if(!event.isCancelled() && event.getDamage() > 0){
				boolean killed = false;
				try{
					EventsForCorruptor.INSTANCE.fort4(event);
					defenseMod(event);
					removeFallDMG(event);
					if(event.getCause() == EntityDamageEvent.DamageCause.VOID){
						if(event.getEntity() instanceof Player){
							executeKill(event);
							killed = true;
						}
					}else if(event instanceof EntityDamageByEntityEvent){
						onHit((EntityDamageByEntityEvent) event);
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}finally{
					if(!killed) executeKillCheck(event);
				}
			}
		}else{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void setPlayerFighting(EntityDamageEvent event){
		Entity entity = event.getEntity();
		if(!event.isCancelled() && entity instanceof Player){
			PlayerUtils.setFighting((Player) entity);
		}
	}

	public static float getBlockingTime(Player player){
		return getBlockingTime(player.getUniqueId());
	}

	public static float getBlockingTime(UUID uuid){
		return blocking.getOrDefault(uuid, 0f);
	}

	Location coruPortalSpawn = ConfigUtils.getSpawn("corrupterPortalEntry");
	public void check(DesertMain main){
		new BukkitRunnable(){
			int count = 0;
			@Override
			public void run() {
				if(count % 20 == 0){
					count = 0;
					for(Player p : Bukkit.getOnlinePlayers()){
						UUID uuid = p.getUniqueId();
						p.setFoodLevel(20);
						Location location = p.getLocation();
						if(!PlayerUtils.fighting.containsKey(uuid))
							PlayerUtils.fighting.put(uuid, 0);
						int incombat = PlayerUtils.fighting.get(uuid);
						if(incombat > 0) PlayerUtils.fighting.put(uuid, incombat - 1);
						PlayerData data = ConfigUtils.getData(p);
						if(location.getBlock().getType().equals(Material.LAVA) || location.getBlock().getType().equals(Material.STATIONARY_LAVA)){
							if(data.getCurrentClass().equals("corrupter") && data.getCorL() > 7){
								p.heal(0.5f);
								ParticleEffect.VILLAGER_HAPPY.display(1, 1, 1, 0, 50, location, 10);
							}
						}
					}
				}
				if(count % 10 == 0){
					for(Player p : Bukkit.getOnlinePlayers()){
						PlayerData data = ConfigUtils.getData(p);
						if(data.getCurrentClass().equals("tank") && data.getTankL() > 5){
							ParticleEffect.SPELL_WITCH.display(0.7f, 0.7f, 0.7f, 0, 1, p.getLocation(), 75);
						}
					}
				}
				Hitbox coruPortal = HitboxManager.get("corrupterPortal");
				for(Player p : Bukkit.getOnlinePlayers()){
					UUID uuid = p.getUniqueId();
					if(p.isBlocking()) blocking.put(uuid, getBlockingTime(uuid) + 0.05f);
					else blocking.remove(uuid);
					if(coruPortal != null){
						if(coruPortal.isInside(p.getLocation())){
							if(coruPortalSpawn == null){
								coruPortalSpawn = ConfigUtils.getSpawn("corrupterPortalEntry");
								if(coruPortalSpawn == null)
									p.sendMessage(ChatColor.RED + "We couldn't teleport you because this portal's spawn is not yet set! Please tell an admin to run /setspawn corrupterPortalEntry");
							}else{
								p.teleport(coruPortalSpawn);
								p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 10, 1);
							}
						}
					}
					PlayerData data = ConfigUtils.getData(p);
					if(p.getHealth() / p.getMaxHealth() < 0.25 && data.getArtifactData().magS()){
						p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,10,0), true);
					}
				}
				count++;
			}
		}.runTaskTimer(main,0,1);
	}

	public static Player getPlayer(Entity arrowOrPlayer) {
		if (arrowOrPlayer instanceof Player) return (Player) arrowOrPlayer;
		else if(arrowOrPlayer instanceof Projectile) return ((Arrow) arrowOrPlayer).getShooter() instanceof Player ? ((Player) ((Arrow) arrowOrPlayer).getShooter()) : null;
		else return null;
	}

	// ---------------------------------------------------------------------------




	public void onHit(EntityDamageByEntityEvent event) {
		try{
			if(event.isCancelled()) return;
			if(event.getDamager().getUniqueId().equals(event.getEntity().getUniqueId())) return;
			if(event.getFinalDamage() <= 0) return;

			for(CustomEnch ce : CustomEnch.values()){
				ce.onHit(event);
			}
			if(event.getEntity() instanceof Player) lastdmgers.put(event.getEntity().getUniqueId(), getPlayer(event.getDamager()).getUniqueId());
			if (event.getDamager() instanceof Player) {
					if (NBTUtil.getCustomAttrString(((Player) event.getDamager()).getItemInHand(), "ID").equals("TOKEN")){
						event.setCancelled(true);
						return;
					}
					ArtifactEvents.hitEvent(event);
				EventsForScout.getInstance().daggerHit(event);
					if (event.getEntity() instanceof Player) {
						attackMod(event);
						if (DesertMain.ct1players.contains(event.getDamager().getUniqueId())) {
							event.setDamage(event.getDamage() * 1.1);
						}
						StreakPolice.onHit(event);

						EventsForCorruptor.INSTANCE.corruptedSword(event);

						EventsForCorruptor.INSTANCE.t8Event(event);
						EventsForCorruptor.INSTANCE.noMercy(event);

						EventsForWizard.INSTANCE.wizardt8(event);
						EventsForWizard.INSTANCE.magicWandHit((Player)event.getEntity(),(Player)event.getDamager());
						EventsForCorruptor.INSTANCE.corruptedSword(event);
						EventsForTank.getInstance().t8Event(event);
						EventsForScout.getInstance().alert(event);
						EventsForTank.getInstance().bludgeon(event);
						if(!event.isCancelled()) EventsForScout.getInstance().scoutBlade(event);
						EventsForScout.getInstance().t8Event(event);
					}
				travellerCoru(event);
				travellerTank(event);
				snackHit(event);
			}
		}catch(NullPointerException ex){
			ex.printStackTrace();
		}
		if(event.getDamager() instanceof Arrow)
			arrowArray.remove(event.getDamager().getEntityId());
	}

	@EventHandler
	public void explosionCancel(ExplosionPrimeEvent event){
		event.setCancelled(true);
	}

	@EventHandler
	public void advancementCancel(PlayerAchievementAwardedEvent event){
		event.setCancelled(true);
	}

	final Map<UUID, Pair<Integer, Integer>> portal = new HashMap<>(); //first: server tick when player entered portal, second: server tick when player last stepped in portal
	Location wizardPortalSpawn = ConfigUtils.getSpawn("wizardPortalEntry");
	private static final int PORTAL_TIME = 60;
	private static final double PORTAL_CIRCLE_FACTOR = 9.0;
	private static final int PORTAL_CIRCLE_DENSITY = 15;
	private static final int PARTICLE_CHANGE_POINT = PORTAL_TIME - PORTAL_TIME / 4; //when the particles switch from the spiral effect to the line shoot effect
	@EventHandler
	public void portal(PlayerPortalEvent event){
		PlayerTeleportEvent.TeleportCause cause = event.getCause();
		event.setCancelled(true);
		if(cause == PlayerTeleportEvent.TeleportCause.END_PORTAL){
			Player player = event.getPlayer();
			UUID uuid = player.getUniqueId();
			int tick = MiscUtils.getCurrentTick();
			Pair<Integer, Integer> playerTick = portal.get(uuid);
			if(playerTick == null){
				portal.put(uuid, playerTick = new Pair<>(tick, tick));
				new BukkitRunnable() {
					public void run(){
						if(portal.containsKey(uuid)){
							int portalTicks = portal.get(uuid).second;
							if(portalTicks == -2){
								portal.remove(player.getUniqueId()); //scrappy
								cancel();
							}else{
								if(MiscUtils.getCurrentTick() - portalTicks > 3){
									if(portal.remove(uuid) != null)
										player.playSound(player.getLocation(), Sound.NOTE_BASS, 10, 0.9f);
									cancel();
								}
							}
						}else{
							cancel();
						}
					}
				}.runTaskTimer(DesertMain.getInstance, 2, 3);
			}else if(playerTick.second == -2){
				portal.remove(player.getUniqueId());
				return;
			}else playerTick.second = tick;
			int difference = playerTick.second - playerTick.first;
			Location playerLoc = player.getLocation().add(0, 1.1, 0);
			if(difference >= PORTAL_TIME){
				playerTick.second = -2;
				if(wizardPortalSpawn != null){
					player.teleport(wizardPortalSpawn);
					player.playSound(playerLoc, Sound.ENDERMAN_TELEPORT, 10, 1);
					ParticleEffect.CLOUD.display(1, 1, 1, 0, 20, playerLoc, 75);
				}else{
					player.sendMessage(ChatColor.RED + "We couldn't teleport you as the portal spawn has not been set yet! Please tell an admin to run /setspawn wizardPortalEntry.");
					player.playSound(playerLoc, Sound.VILLAGER_NO, 10, 1);
					wizardPortalSpawn = ConfigUtils.getSpawn("wizardPortalEntry");
				}
			}else{
				if(difference > PARTICLE_CHANGE_POINT){
					if(wizardPortalSpawn != null){ //shoot a line of flames from the player to the corrupter biome spawn
						Location loc = player.getEyeLocation();
						double x = wizardPortalSpawn.getX() - loc.getX();
						double y = wizardPortalSpawn.getY() - loc.getY();
						double z = wizardPortalSpawn.getZ() - loc.getZ();
						int points = (int) (loc.distance(wizardPortalSpawn) * 1.3);
						int step = (PORTAL_TIME - PARTICLE_CHANGE_POINT) - (PORTAL_TIME - difference) + 2;
						x = x / points * step;
						y = y / points * step;
						z = z / points * step;
						loc = loc.add(x, y, z);
						ParticleEffect.FLAME.display(0, 0, 0, 0, 1, loc, 75);
					}
				}else{
					double radius = (PORTAL_TIME - difference) / PORTAL_CIRCLE_FACTOR;
					if(radius > 0){
						List<Location> circle = MiscUtils.getCircle(playerLoc, radius, (int) (radius * PORTAL_CIRCLE_DENSITY));
						ParticleEffect.FLAME.display(0, 0, 0, 0, 1, circle.get(difference % circle.size()), 50);
						if(difference % 20 == 0)
							player.playSound(playerLoc, Sound.NOTE_BASS, 10, 1 + (difference / 20f / 8));
					}
				}
			}
		}
	}

//	public void executePreKill(EntityDamageByEntityEvent event) {
//		Player damager = getPlayer(event.getDamager());
//		if (event.getEntity() instanceof Player && damager != null) {
//			Player player = (Player) event.getEntity();
//			Player killer = getPlayer(event.getDamager());
//			if(event.getFinalDamage() >= player.getHealth()){
//				event.setCancelled(true);
//				executeKill(player, killer);
//			}
//		}
//	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void exp(PlayerExpChangeEvent event){
		event.setAmount(0);
	}

	public static void executeKill(Player player, Player killer){
		executeKill(player, killer, EntityDamageEvent.DamageCause.CUSTOM);
	}

	public static void executeKill(Player player, Player killer, EntityDamageEvent.DamageCause cause) {
		boolean cancelled = false;
		try {
			FallenDeathEvent event = new FallenDeathEvent(player, killer, getItemUsed(killer), cause);
			try{
				Bukkit.getPluginManager().callEvent(event);
			}finally{
				cancelled = event.isCancelled();
			}
			if(!cancelled){
				String message = killer != null ? ChatColor.GRAY + "You were killed by " + killer.getDisplayName() + ChatColor.GRAY + "." : ChatColor.YELLOW + "You died.";
				if(ks.get(player.getUniqueId()) > 5)
					message += ChatColor.RED + "\nYour streak of " + ChatColor.AQUA + ks.get(player.getUniqueId()) + ChatColor.RED + " was lost!";
				player.sendMessage(message);
				callOnKill(player, killer);
				DesertMain.snack.remove(player.getUniqueId());
				player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 0.5f);
				if(RisenUtils.isBoss(player.getUniqueId()))
					RisenMain.currentBoss.endBoss(RisenBoss.EndReason.BOSS_VANQUISHED);
				if(killer != null){
					ThreadLocalRandom random = ThreadLocalRandom.current();
					int soulsgained = 0;
					if(random.nextDouble() <= 0.2){
						try{
							if(NBTUtil.getCustomAttrString(killer.getInventory().getChestplate(), "ID").equals("LUCKY_CHESTPLATE"))
								soulsgained = 2;
							else{
								soulsgained = 1;
							}
						}catch(Exception ex){
							soulsgained = 1;
							if(!(ex instanceof NullPointerException)){
								Bukkit.getConsoleSender().sendMessage("Error managing souls. Error:\n" + ex);
							}
						}
					}
					int leveldiff = Math.max((ConfigUtils.getLevel(ConfigUtils.findClass(player), player) - ConfigUtils.getLevel(ConfigUtils.findClass(killer), killer)), 1);
					int xpgained = (leveldiff * 80) + (ks.get(player.getUniqueId()) * 15) + ((random.nextInt(1, 3) * 25) * ks.get(killer.getUniqueId()) + 15);
					xpgained = Math.min(xpgained, 6500 + random.nextInt(500));
					int gemsgained = (leveldiff * 60) + (ks.get(player.getUniqueId()) * 10) + ((random.nextInt(2, 4) * 40) * ks.get(killer.getUniqueId()) + 20);
					gemsgained = Math.min(gemsgained, Math.min(gemsgained, 2500 + random.nextInt(500)));
					killer.sendMessage(ChatColor.GREEN + "You killed " + ChatColor.YELLOW + player.getName() + ChatColor.DARK_GRAY + " (" + ChatColor.DARK_GRAY + "+" + ChatColor.BLUE + xpgained + " EXP" + ChatColor.DARK_GRAY + ", +" + ChatColor.GREEN + gemsgained + " Gems" + ChatColor.DARK_GRAY + ", +" + ChatColor.LIGHT_PURPLE + soulsgained + " Souls" + ChatColor.DARK_GRAY + ")");
					ks.put(killer.getUniqueId(), ks.getOrDefault(killer.getUniqueId(), 0) + 1);
					killer.playSound(killer.getLocation(), Sound.LEVEL_UP, 1, 4);
					killer.playSound(killer.getLocation(), Sound.BURP, 6, 1.3f);
					killer.playSound(killer.getLocation(), Sound.SHOOT_ARROW, 7, 1.1f);
					ConfigUtils.addGems(killer, gemsgained);
					ConfigUtils.addXP(killer, ConfigUtils.findClass(killer), xpgained);
					ConfigUtils.addSouls(killer, soulsgained);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(!cancelled) respawn(player);
		}
	}

	public static void respawn(Player player){
		Location spawn = ConfigUtils.getSpawn("lobby");
		player.setHealth(player.getMaxHealth());
		ks.put(player.getUniqueId(), 0);
		player.teleport(spawn);
		player.setVelocity(new Vector(0, 0, 0));
		PlayerInventory inv = player.getInventory();
		if(inv.getHelmet() == null) inv.setHelmet(MiscUtils.generateItem(Material.CHAINMAIL_HELMET, ChatColor.WHITE + "Chainmail Helmet", Collections.emptyList(), (byte) -1, 1, "CHAINMAIL_HELMET"));
		if(inv.getChestplate() == null) inv.setChestplate(MiscUtils.generateItem(Material.CHAINMAIL_CHESTPLATE, ChatColor.WHITE + "Chainmail Chestplate", Collections.emptyList(), (byte) -1, 1, "CHAINMAIL_CHESTPLATE"));
		if(inv.getLeggings() == null) inv.setLeggings(MiscUtils.generateItem(Material.CHAINMAIL_LEGGINGS, ChatColor.WHITE + "Chainmail Leggings", Collections.emptyList(), (byte) -1, 1, "CHAINMAIL_LEGGINGS"));
		if(inv.getBoots() == null) inv.setBoots(MiscUtils.generateItem(Material.CHAINMAIL_BOOTS, ChatColor.WHITE + "Chainmail Boots", Collections.emptyList(), (byte) -1, 1, "CHAINMAIL_BOOTS"));
		if(!inv.contains(Material.IRON_SWORD)) inv.addItem(MiscUtils.generateItem(Material.IRON_SWORD, ChatColor.WHITE + "Iron Sword", Collections.emptyList(), (byte) -1, 1, "IRON_SWORD"));
		Bukkit.getScheduler().runTask(DesertMain.getInstance, () -> player.setFireTicks(0)); //idk, just don't ask
		PlayerUtils.setAbsorption(player, 0);
		TravellerEvents.resetTraveller(player);
		for(Player other : Bukkit.getOnlinePlayers()){
			if(!other.getUniqueId().equals(player.getUniqueId())){
				if(!other.canSee(player)) other.showPlayer(player);
			}
		}
		for(PotionEffect potionEffect : player.getActivePotionEffects()){
			player.removePotionEffect(potionEffect.getType());
		}
		me.gabriel.Traits.Events.reapply(player);
	}

	private static void executeKillCheck(EntityDamageEvent event) throws NullPointerException {
		if(!event.isCancelled()){
			if(event.getEntity() instanceof Player){
				Player player = (Player) event.getEntity();
				if(event.getFinalDamage() >= PlayerUtils.getTotalHealth(player)){
					executeKill(event);
				}
			}
		}
	}

	public static void executeKill(EntityDamageEvent event){
		Entity entity = event.getEntity();
		if(entity instanceof Player){
			event.setCancelled(true);
			executeKill((Player) entity, event.getCause());
		}else if(entity instanceof Damageable) ((Damageable) entity).setHealth(0);
	}

	public static void executeKill(Player dead){
		executeKill(dead, EntityDamageByEntityEvent.DamageCause.CUSTOM);
	}

	public static void executeKill(Player dead, EntityDamageEvent.DamageCause cause){
		UUID uuid = lastdmgers.get(dead.getUniqueId());
		Player killer = uuid == null ? null : Bukkit.getPlayer(uuid);
		executeKill(dead, killer, cause);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void showMarker(EntityDamageEvent event){
		if(!event.isCancelled() && event.getFinalDamage() > 0)
			MiscUtils.damageIndicator(event.getFinalDamage(), event instanceof EntityDamageByEntityEvent ? ((EntityDamageByEntityEvent) event).getDamager() : null, event.getEntity());
	}

	@EventHandler
	public void gapple(PlayerItemConsumeEvent event){
		ItemStack item = event.getItem();
		if(NBTUtil.getCustomAttrString(item, "ID").equals("GOLDEN_APPLE")){
			event.setCancelled(true);
			Player player = event.getPlayer();
			player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 1), false);
			if(player.getAbsorption() < 4) player.setAbsorption(4);
			player.heal(7);
			Inventory inv = player.getInventory();
			int index = MiscUtils.indexOf(inv.getContents(), item);
			inv.setItem(index, MiscUtils.decrementItem(item));
		}
	}

	private static void callOnKill(Player player, Player killer) {
		checkItemLives(player);
		PlayerUtils.setIdle(player);
		for(int i = 0; i<36; i++){
			ItemStack item = player.getInventory().getItem(i);
			try{
				NBTItem nbt = new NBTItem(item);
				NBTCompound compound = nbt.getCompound("CustomAttributes");
				if(compound.getString("ID").equals("WIZARD_BLADE")){
					compound.setInteger("CHARGE", 0);
					player.getInventory().setItem(i, nbt.getItem());
				}
			}catch(NullPointerException ignored){}
		}
		if(TravellerEvents.travelled.containsKey(player.getUniqueId())) TravellerEvents.travelled.get(player.getUniqueId()).clear();
		if(killer == null) return;
		// ---------------------------------------------------------------------
		PlayerUtils.setFighting(killer);
		EventsForWizard.INSTANCE.wizardt1(killer);
		try{
			if(NBTUtil.getCustomAttrString(killer.getItemInHand(), "ID").equals("WIZARD_BLADE")) EventsForWizard.addBladeCharge(killer);
		}catch(Exception ex){
			if (!(ex instanceof NullPointerException)){
				Bukkit.getLogger().log( Level.WARNING, "Error adding charge to wizard blade", ex);
			}
		}
		try {
			if(new NBTItem(killer.getInventory().getLeggings()).getString("ID").equals("CORRUPTER_LEGGINGS")) EventsForCorruptor.INSTANCE.corrupterLeggings(killer, player);
		}catch(Exception ex){
			if(!(ex instanceof NullPointerException)){
				Bukkit.getConsoleSender().sendMessage("Error checking for corrupter leggings: " + ex);
			}
		}


		StreakPolice.onKill(killer);

		if(ks.get(killer.getUniqueId()) == 50 && !RisenMain.alreadyUsed.contains(killer.getUniqueId())){
			String[] classes = new String[]{Key.SCOUT, Key.CORRUPTER, Key.TANK, Key.WIZARD};
			boolean allClassesMaxed = true;
			for(String clazz : classes){
				 if(!(ConfigUtils.getLevel(clazz, killer.getUniqueId()) > 9)){
					 allClassesMaxed = false;
					 break;
				 }
			}
			if(allClassesMaxed) RisenUtils.activateBossReady(killer);
		}
	}

	@EventHandler
	public void enchant(FallenDeathEvent event){
		for(CustomEnch ce : CustomEnch.values()){
			ce.onKill(event);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void cosmetic(FallenDeathEvent event){
		if(!event.isCancelled()){
			Cosmetic kSelected = Cosmetic.getSelected(event.getKiller(), Cosmetic.CosmeticType.KILL_EFFECT);
			if(kSelected != null) kSelected.activateKill(event.getPlayer());
		}
	}

	@EventHandler
	public void dd(FallenDeathEvent event){
		if(!(event.getDamageCause() == EntityDamageEvent.DamageCause.VOID)){
			Player player = event.getPlayer();
			for(ItemStack item : player.getInventory().getContents()){
				if(NBTUtil.getCustomAttrString(item, "ID").equals("DEATH_DEFIANCE")){
					event.setCancelled(true);
					Location location = player.getLocation();
					player.getInventory().remove(item);
					player.playSound(location, Sound.DIG_STONE, 10, 1);
					player.playSound(location, Sound.PISTON_EXTEND, 10, 0.9f);
					new BukkitRunnable() {
						float tone = 0;

						@Override
						public void run(){
							if(tone >= 2){
								cancel();
							}else{
								location.getWorld().playSound(player.getLocation(), Sound.ORB_PICKUP, 10, tone);
								tone += 0.1f;
							}
						}
					}.runTaskTimer(DesertMain.getInstance, 0, 2);
					player.sendMessage(ChatColor.YELLOW + "You rise from the ashes!\n" + ChatColor.DARK_GRAY + "Consumed 1 Death Defiance");
					player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 50, 1));
					ParticleEffect.FLAME.display(1, 10, 1, 0.1f, 200, player.getLocation(), 15);
					player.setHealth(player.getMaxHealth() * 0.4);
					invincible.add(player.getUniqueId());
					new BukkitRunnable() {
						public void run(){
							invincible.remove(player.getUniqueId());
						}
					}.runTaskLater(DesertMain.getInstance, 50);
					break;
				}
			}
		}
	}

	@EventHandler
	public void healthRegen(EntityRegainHealthEvent e){
		if(e.getEntity() instanceof Player && (e.getRegainReason() != EntityRegainHealthEvent.RegainReason.MAGIC_REGEN && e.getRegainReason() != EntityRegainHealthEvent.RegainReason.MAGIC && e.getRegainReason() != EntityRegainHealthEvent.RegainReason.CUSTOM)){
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void quit(PlayerQuitEvent event){
		ks.put(event.getPlayer().getUniqueId(), 0);
		MilestonesUtil.confirming.remove(event.getPlayer().getUniqueId());
		Rank rank = ConfigUtils.getRank(event.getPlayer());
		Prefix title = ConfigUtils.getSelectedTitle(event.getPlayer());
		if(rank != null)
			event.setQuitMessage((title == null ? "" : title + " ") + event.getPlayer().getDisplayName() + " just left. See you around!");
		else event.setQuitMessage("");
		portal.remove(event.getPlayer().getUniqueId());
		executeKill(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void lastDamagerRemove(FallenDeathEvent event){
		lastdmgers.remove(event.getPlayer().getUniqueId());
	}

	@EventHandler
	public void lastDamagerRemove(EntityDeathEvent event){
		lastdmgers.remove(event.getEntity().getUniqueId());
	}

	@EventHandler
	public void continual(PlayerJoinEvent e) {
		final Player p = e.getPlayer();
		new BukkitRunnable() {
			int count = 0;
			@Override
			public void run() {
				if(p.isOnline()){
					FScoreboardManager.initialize(p);
					if(count % 10 == 0 && (PlayerUtils.isIdle(p) || HitboxListener.isInSafeZone(p.getLocation()))){
						tryRegen(p);
					}
					count++;
				}else cancel();
			}
		}.runTaskTimer(DesertMain.getPlugin(DesertMain.class), 0, 5);
	}

	private static void tryRegen(Player p){
		double maxHealth = p.getMaxHealth();
		double health = p.getHealth();
		if(health < maxHealth)
			p.heal(0.5f);
	}

	@EventHandler
	public void forKs(PlayerJoinEvent event){
		ks.putIfAbsent(event.getPlayer().getUniqueId(), 0);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		try{
			UUID uuid = p.getUniqueId();
			String blockNotifPath = "players." + uuid + ".blocknotifications";
			if(!(main.getConfig().contains("players." + uuid))){
				main.getConfig().createSection("players." + uuid);
				//init block notifications
				main.getConfig().set(blockNotifPath, true);
				//save
				main.saveConfig();
				e.setJoinMessage(e.getPlayer().getName() + ChatColor.GOLD + " just joined for the first time, give them a warm welcome!");
			}else{
				Rank rank = ConfigUtils.getRank(e.getPlayer());
				if(rank != null){
					e.getPlayer().setDisplayName(rank.c + e.getPlayer().getName());
					e.setJoinMessage(rank.p.toString() + rank.c + " " + e.getPlayer().getName() + " just joined.");
				}else{
					e.getPlayer().setDisplayName(ChatColor.GRAY + e.getPlayer().getName());
					e.setJoinMessage("");
				}
				MiscUtils.refreshTablistName(e.getPlayer());
				e.getPlayer().sendMessage(DesertMain.getWelcome());
			}
			if(main.getConfig().getBoolean(blockNotifPath))
				TravellerEvents.blockNotifs.add(p.getUniqueId());
			MilestonesUtil.refreshExpBar(p);
		}finally{
			respawn(p);
		}
		p.updateInventory();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void dropTimer(PlayerDropItemEvent event){
		Bukkit.getScheduler().runTaskLater(DesertMain.getInstance, () -> {
			Item item = event.getItemDrop();
			if(!item.isDead()) item.remove();
		}, 10000);
	}

	@EventHandler
	public void snackEat(PlayerItemConsumeEvent event){
		Player player = event.getPlayer();
		try {
			if (NBTUtil.getCustomAttrString(event.getItem(), "ID").equals("LAVA_CAKE")){
				if(ConfigUtils.findClass(player).equals("corrupter") && ConfigUtils.getLevel("corrupter", player) > 2){
					if(!DesertMain.snack.containsKey(player.getUniqueId())) {
						player.sendMessage(ChatColor.GREEN + "Powered up your next shot for 3 extra damage!");
						player.playSound(player.getLocation(), Sound.FIRE_IGNITE, 12, 1);
						DesertMain.snack.put(player.getUniqueId(), "lava");
					}else{
						player.sendMessage(ChatColor.RED + "Your next shot was already powered up!");
						event.setCancelled(true);
					}
				}else{
					player.sendMessage(ChatColor.RED + "You must have the corrupter class equipped and level 2 to use this!");
					event.setCancelled(true);
				}
			}
		}catch(NullPointerException ignored){}
		try {
			if (NBTUtil.getCustomAttrString(event.getItem(), "ID").equals("PROTEIN_SNACK")){
				if(ConfigUtils.findClass(player).equals("tank") && ConfigUtils.getLevel("tank", player) > 2){
					if(!DesertMain.snack.containsKey(player.getUniqueId())) {
						player.sendMessage(ChatColor.GREEN + "Powered up your next shot to hoist your opponent upwards!");
						player.playSound(player.getLocation(), Sound.FIRE_IGNITE, 12, 1);
						DesertMain.snack.put(player.getUniqueId(), "protein");
					}else{
						player.sendMessage(ChatColor.RED + "Your next shot was already powered up!");
						event.setCancelled(true);
					}
				}else{
					player.sendMessage(ChatColor.RED + "You must have the tank class equipped and level 2 to use this!");
					event.setCancelled(true);
				}
			}
		}catch(NullPointerException ignored){}
		try {
			if (NBTUtil.getCustomAttrString(event.getItem(), "ID").equals("MAGIC_SNACK")){
				if(ConfigUtils.findClass(player).equals("wizard") && ConfigUtils.getLevel("wizard", player) > 2){
					if(!DesertMain.snack.containsKey(player.getUniqueId())) {
						player.sendMessage(ChatColor.GREEN + "Powered up your next shot to give your opponent speed 1 and weakness 1 for 2 seconds!");
						player.playSound(player.getLocation(), Sound.FIRE_IGNITE, 12, 1);
						DesertMain.snack.put(player.getUniqueId(), "magic");
					}else{
						player.sendMessage(ChatColor.RED + "Your next shot was already powered up!");
						event.setCancelled(true);
					}
				}else{
					player.sendMessage(ChatColor.RED + "You must have the wizard class equipped and level 2 to use this!");
					event.setCancelled(true);
				}
			}
		}catch(NullPointerException ignored){}
		try {
			if (NBTUtil.getCustomAttrString(event.getItem(), "ID").equals("ENERGY_SNACK")){
				if(ConfigUtils.findClass(player).equals("scout") && ConfigUtils.getLevel("scout", player) > 2){
					if(!DesertMain.snack.containsKey(player.getUniqueId())) {
						player.sendMessage(ChatColor.GREEN + "Powered up your next shot grant you you speed 2 for 2 seconds!");
						player.playSound(player.getLocation(), Sound.FIRE_IGNITE, 12, 1);
						DesertMain.snack.put(player.getUniqueId(), "energy");
					}else{
						player.sendMessage(ChatColor.RED + "Your next shot was already powered up!");
						event.setCancelled(true);
					}
				}else{
					player.sendMessage(ChatColor.RED + "You must have the scout class equipped and level 2 to use this!");
					event.setCancelled(true);
				}
			}
		}catch(NullPointerException ignored){}
	}

	public void snackHit(EntityDamageByEntityEvent e) {
		try {
			if (DesertMain.snack.get(e.getDamager().getUniqueId()).equals("lava")) {
				DesertMain.snack.remove(e.getDamager().getUniqueId());
				e.setDamage(e.getDamage() + 3);
				if (e.getDamager() instanceof Player) {
					((Player) e.getDamager()).playSound(e.getDamager().getLocation(), Sound.ANVIL_LAND, 10, 1.1f);
				}
			}else if(DesertMain.snack.get(e.getDamager().getUniqueId()).equals("magic")){
				DesertMain.snack.remove(e.getDamager().getUniqueId());
				if(e.getEntity() instanceof Player && e.getDamager() instanceof Player){
					((Player) e.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 0, false, false));
					((Player) e.getDamager()).playSound(e.getDamager().getLocation(), Sound.ANVIL_LAND, 10, 1.1f);
				}
			}else if(DesertMain.snack.get(e.getDamager().getUniqueId()).equals("energy")){
				DesertMain.snack.remove(e.getDamager().getUniqueId());
				if(e.getEntity() instanceof Player && e.getDamager() instanceof Player){
					((Player) e.getDamager()).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 0, false, false));
					((Player) e.getDamager()).playSound(e.getDamager().getLocation(), Sound.ANVIL_LAND, 10, 1.1f);
				}
			}else if(DesertMain.snack.get(e.getDamager().getUniqueId()).equals("protein")){
				DesertMain.snack.remove(e.getDamager().getUniqueId());
				if(e.getDamager() instanceof Player){
					Entity entity = e.getEntity();
					Vector velocity = entity.getVelocity();
					entity.setVelocity(velocity.setY(velocity.getY() + 1));
					((Player) e.getDamager()).playSound(e.getDamager().getLocation(), Sound.ANVIL_LAND, 10, 1.1f);
				}
			}
		}catch(NullPointerException ignored){}
	}
}
