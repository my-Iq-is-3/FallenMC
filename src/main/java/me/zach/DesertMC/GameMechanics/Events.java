package me.zach.DesertMC.GameMechanics;


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
import me.zach.DesertMC.GameMechanics.hitbox.HitboxListener;
import me.zach.DesertMC.GameMechanics.npcs.StreakPolice;
import me.zach.DesertMC.ScoreboardManager.FScoreboardManager;
import me.zach.DesertMC.Utils.Config.ConfigUtils;
import me.zach.DesertMC.Utils.MiscUtils;
import me.zach.DesertMC.Utils.Particle.ParticleEffect;
import me.zach.DesertMC.Utils.PlayerUtils;
import me.zach.DesertMC.Utils.RankUtils.Rank;
import me.zach.DesertMC.Utils.ench.CustomEnch;
import me.zach.DesertMC.Utils.nbt.NBTUtil;
import me.zach.DesertMC.anvil.FallenAnvilInventory;
import me.zach.DesertMC.cosmetics.Cosmetic;
import me.zach.DesertMC.events.FallenDeathEvent;
import me.zach.artifacts.events.ArtifactEvents;
import me.zach.artifacts.gui.inv.items.CreeperTrove;
import me.zach.databank.saver.Key;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.CachedServerIcon;
import xyz.fallenmc.risenboss.main.RisenBoss;
import xyz.fallenmc.risenboss.main.RisenMain;
import xyz.fallenmc.risenboss.main.utils.RisenUtils;

import java.io.File;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public class Events implements Listener{
	public static Set<UUID> invincible = new HashSet<>();
	Plugin main = DesertMain.getInstance;
	public static HashMap<UUID,Integer> ks = new HashMap<>();
	public static HashMap<Arrow, ItemStack> arrowArray = new HashMap<>();
	static final HashMap<UUID, Float> blocking = new HashMap<>();
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
	}


	public static ItemStack getItemUsed(Entity damager){
		Player playerDmgr = damager instanceof Player ? (Player) damager : null;
		return damager instanceof Arrow ? arrowArray.get(damager) : (playerDmgr != null ? playerDmgr.getItemInHand() : null); //arrow? get from arrow array. player? get item in hand. neither? null.
	}

	public static void checkItemLives(Player player){
		Inventory inventory = player.getInventory();
		for(int i = 0; i<inventory.getSize(); i++){
			ItemStack item = inventory.getItem(i);
			if(item != null && item.getType() != Material.AIR){
				NBTItem nbt = new NBTItem(item);
				if(NBTUtil.hasCustomKey(nbt, "LIVES")){
					Integer lives = NBTUtil.getCustomAttr(nbt, "LIVES", Integer.class);
					if(lives != null && lives - 1 <= 0){
						inventory.clear(i);
						player.sendMessage(ChatColor.RED + "Your " + item.getItemMeta().getDisplayName() + ChatColor.RED + " ran out of lives!");
					}
				}
			}
		}
	}

	@EventHandler
	public void iceMelt(BlockFadeEvent event){
		event.setCancelled(true);
	}

	@EventHandler
	public void nft(ServerListPingEvent event){
		if(nft != null && Math.random() < 0.02){
			event.setServerIcon(nft);
		}
	}

	public void attributeMod(EntityDamageByEntityEvent event){
		Entity damaged = event.getDamager();
		ItemStack itemUsed = getItemUsed(event.getDamager());
		if(event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK){
			double damage = event.getDamage();
			if(itemUsed != null){
				NBTItem nbt = new NBTItem(itemUsed);
				float dmgMod = NBTUtil.getCustomAttrFloat(nbt, "ATTACK", 1);
				if(dmgMod != 1) event.setDamage(damage * dmgMod);
			}
			if(damaged instanceof LivingEntity){
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
		String owner = NBTUtil.getCustomAttrString(nbt, "OWNER");
		if(!owner.equals("null") && !owner.equals(uuid)){
			event.setCancelled(item.getTicksLived() < 3000);
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
			arrowArray.put(arrow, player.getInventory().getItemInHand());
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

	@EventHandler
	public void cancelAnvil(InventoryOpenEvent event){
		if(event.getInventory() instanceof AnvilInventory){
			Player player = (Player) event.getPlayer();
			event.setCancelled(true);
			player.openInventory(new FallenAnvilInventory().getInventory());
		}
	}

	public void travellerTank(EntityDamageByEntityEvent e){
		if(e.getEntity() instanceof Player) {
			UUID uuid = e.getEntity().getUniqueId();
			if(ConfigUtils.getLevel("tank", uuid) > 5 && ConfigUtils.findClass(uuid).equals("tank")){
				Set<Block> blockSet = TravellerEvents.travelled.get(uuid);
				if (blockSet != null) {
					double multiplier = 1 - (blockSet.size() * 0.0002);
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
				double multiplier = 1 + blockSet.size() * 0.0002;
				e.setDamage(e.getDamage() * multiplier);
			}
		}
	}

	@EventHandler
	public void enchantRefinery(InventoryOpenEvent event){
		if(event.getInventory() instanceof EnchantingInventory){
			event.setCancelled(true);
			Player player = (Player) event.getPlayer();
			RefineryInventory inv = RefineryUtils.instance.get(player.getUniqueId());
			if(inv == null) RefineryUtils.instance.put(player.getUniqueId(), inv = new RefineryInventory());
			inv.openRefineryInventory(player, false);
		}
	}

	@EventHandler
	public void onHitInWhileInvincible(EntityDamageByEntityEvent event) {
		try {
			if(event.getEntity() instanceof Player) {
				Player p = (Player) event.getEntity();
				UUID uuid = p.getUniqueId();
				if(invincible.contains(uuid)) {
					event.setCancelled(true);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	public void removeFallDMG(EntityDamageEvent event){
		EventsForCorruptor.INSTANCE.fort4(event);
		if(event.getCause().equals(EntityDamageEvent.DamageCause.FALL)){
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void breakBlock(BlockBreakEvent event){
		Player player = event.getPlayer();
		if(!MiscUtils.isAdmin(player)){
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void placeBlock(BlockPlaceEvent event){
		if(!MiscUtils.isAdmin(event.getPlayer())) event.setCancelled(true);
	}

	@EventHandler
	public void till(PlayerInteractEvent event){
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(event.getItem().getType().name().endsWith("HOE")){
				event.setCancelled(true);
			}
		}
	}

	public static float getBlockingTime(Player player){
		return getBlockingTime(player.getUniqueId());
	}

	public static float getBlockingTime(UUID uuid){
		return blocking.getOrDefault(uuid, 0f);
	}

	public static void check(DesertMain main){
		new BukkitRunnable(){
			@Override
			public void run() {
				for(Player p : Bukkit.getOnlinePlayers()){
					UUID uuid = p.getUniqueId();
					if(!DesertMain.eating.contains(uuid))
					p.setFoodLevel(20);
					Location location = p.getLocation();
					Location locbefore = location.clone();

					if (!PlayerUtils.fighting.containsKey(uuid))
						PlayerUtils.fighting.put(uuid, 11);
					int incombat = PlayerUtils.fighting.get(uuid);
					if (incombat > 0) PlayerUtils.fighting.put(uuid, incombat - 1);


					if(locbefore.getBlock().getType().equals(Material.LAVA) || locbefore.getBlock().getType().equals(Material.STATIONARY_LAVA)){
						if(ConfigUtils.findClass(p).equals("corrupter") && ConfigUtils.getLevel("corrupter",p) > 7){
							if(p.getHealth() + 0.5 <= p.getMaxHealth()){
								p.setHealth(p.getHealth() + 0.5);
							}else if(p.getHealth() + 0.5  > p.getMaxHealth()){
								p.setHealth(p.getMaxHealth());
							}
							ParticleEffect.VILLAGER_HAPPY.display(1,1,1,0,50,locbefore,10);
						}
					}
					if(p.isBlocking()) blocking.put(uuid, getBlockingTime(uuid) + 0.05f);
					else blocking.remove(uuid);
				}
			}
		}.runTaskTimer(main,0,20);
	}

	@EventHandler
	public void onCrouchToggle(PlayerToggleSneakEvent event){
		if(DesertMain.crouchers.contains(event.getPlayer().getUniqueId())) {
			DesertMain.crouchers.remove(event.getPlayer().getUniqueId());
		}else{
			DesertMain.crouchers.add(event.getPlayer().getUniqueId());
		}
	}

	@EventHandler
	public void eating(PlayerInteractEvent e) {
		try {
			if (e.getPlayer().getItemInHand().getType().equals(Material.COOKIE)) {
				if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
					e.getPlayer().setFoodLevel(19);
					DesertMain.eating.add(e.getPlayer().getUniqueId());
					new BukkitRunnable() {
						public void run() {

							e.getPlayer().setFoodLevel(20);
							new BukkitRunnable() {
								public void run() {
									DesertMain.eating.remove(e.getPlayer().getUniqueId());
								}
							}.runTaskLater(Bukkit.getPluginManager().getPlugin("Fallen"), 10);
						}
					}.runTaskLater(Bukkit.getPluginManager().getPlugin("Fallen"), 60);
				}
			}
		} catch (NullPointerException ignored) {
		}

	}


	public static Player getPlayer(Entity arrowOrPlayer) {
		if (arrowOrPlayer instanceof Player) return (Player) arrowOrPlayer;
		else if(arrowOrPlayer instanceof Arrow) return ((Arrow) arrowOrPlayer).getShooter() instanceof Player ? ((Player) ((Arrow) arrowOrPlayer).getShooter()) : null;
		else return null;
	}

	// ---------------------------------------------------------------------------


	@EventHandler
	public void onKill(EntityDamageByEntityEvent event) {
		try{
			if(HitboxListener.isInCafe(event.getEntity().getLocation()) || HitboxListener.isInSpawn(event.getEntity().getLocation())) event.setCancelled(true);
			if(event.isCancelled()) return;
			CreeperTrove.executeTrove(event);
			if(event.getDamage() == 0) return;
			if(event.getDamager() instanceof Player && event.getEntity() instanceof Player){
				Player damager = (Player) event.getDamager();
				PlayerUtils.setFighting(damager);
				if (event.getEntity() instanceof Player)
					if (!event.getEntity().getUniqueId().equals(event.getDamager().getUniqueId()))
						DesertMain.lastdmgers.put(event.getEntity().getUniqueId(), damager.getUniqueId());
			}else if(event.getDamager() instanceof Arrow){
				Arrow damager = (Arrow) event.getDamager();
				if(damager.getShooter() instanceof Player){
					Player shooter = (Player) damager.getShooter();
					PlayerUtils.setFighting(shooter);
					if (event.getEntity() instanceof Player)
						if (!shooter.getUniqueId().equals(event.getEntity().getUniqueId()))
							DesertMain.lastdmgers.put(event.getEntity().getUniqueId(), shooter.getUniqueId());
				}
			}
			for(CustomEnch ce : CustomEnch.values()){
				ce.onHit(event);
			}
			if (event.getEntity() instanceof Player) {
				PlayerUtils.setFighting((Player) event.getEntity());
			}
			if (event.getDamager() instanceof Player) {
					if (NBTUtil.getCustomAttrString(((Player) event.getDamager()).getItemInHand(), "ID").equals("TOKEN")){
						event.setCancelled(true);
						return;
					}
					if (event.getEntity() instanceof Player) {
						attributeMod(event);
						if (DesertMain.ct1players.contains(event.getDamager().getUniqueId())) {
							event.setDamage(event.getDamage() * 1.1);
						}
						StreakPolice.onHit(event);
						ArtifactEvents.hitEvent(event);

						EventsForCorruptor.INSTANCE.corruptedSword(event);

						EventsForScout.getInstance().daggerHit(event);
						EventsForCorruptor.INSTANCE.t8Event(event);
						EventsForCorruptor.INSTANCE.noMercy(event);
						EventsForCorruptor.INSTANCE.t1Event(event);
						EventsForWizard.INSTANCE.wizardt4(event);

						EventsForWizard.INSTANCE.wizardt8(event);
						EventsForScout.getInstance().t1Event(event);
						EventsForScout.getInstance().t4Event(event);

						EventsForScout.getInstance().t8Event(event);
						EventsForCorruptor.INSTANCE.volcanicSword(event);
						EventsForWizard.INSTANCE.magicWandHit((Player)event.getEntity(),(Player)event.getDamager());
						EventsForCorruptor.INSTANCE.corruptedSword(event);
						EventsForTank.getInstance().t1Event(event);
						EventsForTank.getInstance().t5Event(event);
						EventsForTank.getInstance().t8Event(event);
						EventsForTank.getInstance().fortify(event);
						EventsForScout.getInstance().alert(event);
						EventsForTank.getInstance().bludgeon(event);
						EventsForScout.getInstance().scoutBlade((Player)event.getDamager(), (Player) event.getEntity());
					}
				travellerCoru(event);
				travellerTank(event);
				snackHit(event);
			}
		}catch(NullPointerException ex){
			ex.printStackTrace();
		}
		executePreKill(event);
		if(event.getDamager() instanceof Arrow) arrowArray.remove(event.getDamager());
	}


	public void executePreKill(EntityDamageByEntityEvent event) {
		Player damager = getPlayer(event.getDamager());
		if (event.getEntity() instanceof Player && damager != null) {
			Player player = (Player) event.getEntity();
			Player killer = getPlayer(event.getDamager());
			for(CustomEnch ce : CustomEnch.values()){
				ce.onKill(event);
			}
			if(player.getHealth() - event.getDamage() < 0.1){
				event.setCancelled(true);
				executeKill(player, killer);
			}
		}
	}

	public static void executeKill(Player player, Player killer) {
		try {
			FallenDeathEvent event = new FallenDeathEvent(player, killer, getItemUsed(killer));
			Bukkit.getPluginManager().callEvent(event);
			if(event.isCancelled()) return;
			Location spawn = ConfigUtils.getSpawn("lobby");
			player.setHealth(player.getMaxHealth());
			player.teleport(spawn);
			player.getInventory().addItem(new ItemStack(Material.IRON_SWORD));
			player.getInventory().addItem(new ItemStack(Material.CHAINMAIL_BOOTS));
			player.getInventory().addItem(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
			player.getInventory().addItem(new ItemStack(Material.CHAINMAIL_LEGGINGS));
			player.getInventory().addItem(new ItemStack(Material.CHAINMAIL_HELMET));

			player.setFireTicks(0);
			player.sendMessage(ChatColor.RED + "You were killed by " + (killer == null ? ChatColor.GRAY + "no one! (Seriously?)" : MiscUtils.getRankColor(killer) + killer.getName()) + ChatColor.RED + " and lost your streak of " + ChatColor.AQUA + ks.get(player.getUniqueId()));
			callOnKill(player, killer);
			if(RisenUtils.isBoss(player.getUniqueId()))
				RisenMain.currentBoss.endBoss(RisenBoss.EndReason.BOSS_VANQUISHED);
			DesertMain.snack.remove(player.getUniqueId());
			ks.put(player.getUniqueId(), 0);
			player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 0.5f);
			if(killer == null) return;
			Random random = ThreadLocalRandom.current();
			int soulsgained = 0;
			int randomCompare = 4;
			if (random.nextInt(randomCompare) == 0){
				try {
					if(NBTUtil.getCustomAttrString(killer.getInventory().getChestplate(), "ID").equals("LUCKY_CHESTPLATE"))
						soulsgained = 2;
					else{
						soulsgained = 1;
					}
				} catch (Exception ex) {
					soulsgained = 1;
					if (!(ex instanceof NullPointerException)) {
						Bukkit.getConsoleSender().sendMessage("Error managing souls. Error:\n" + ex);
					}
				}
			}
			int xpgained = (ConfigUtils.getLevel(ConfigUtils.findClass(player), player) * 10) + 5 + ks.get(player.getUniqueId()) * 5;

			int gemsgained = (ConfigUtils.getLevel(ConfigUtils.findClass(player), player) * 15) + ks.get(player.getUniqueId()) * 3;

			killer.sendMessage(ChatColor.GREEN + "You killed " + ChatColor.YELLOW + player.getName() + ChatColor.DARK_GRAY + " (" + ChatColor.DARK_GRAY + "+" + ChatColor.BLUE + xpgained + " EXP" + ChatColor.DARK_GRAY + ", +" + ChatColor.GREEN + gemsgained + " Gems" + ChatColor.DARK_GRAY + ", +" + ChatColor.LIGHT_PURPLE + soulsgained + " Souls" + ChatColor.DARK_GRAY + ")");
			ks.putIfAbsent(killer.getUniqueId(), 0);
			ks.put(killer.getUniqueId(), ks.get(killer.getUniqueId()) + 1);
			killer.playSound(killer.getLocation(), Sound.LEVEL_UP, 1, 4);
			killer.playSound(killer.getLocation(), Sound.BURP, 6, 1.3f);
			killer.playSound(killer.getLocation(), Sound.SHOOT_ARROW, 7, 1.1f);
			ConfigUtils.addGems(killer, gemsgained);
			ConfigUtils.addXP(killer, ConfigUtils.findClass(killer), xpgained);
			ConfigUtils.addSouls(killer, soulsgained);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void executeUnexpectedKill(EntityDamageEvent event) throws NullPointerException {
		if(!event.isCancelled()){
			UUID uuid = DesertMain.lastdmgers.get(event.getEntity().getUniqueId());
			Player killer = Bukkit.getPlayer(uuid);

			if(event.getEntity() instanceof Player){
				Player player = (Player) event.getEntity();
				if(player.getHealth() - event.getDamage() < 0.1){
					event.setCancelled(true);
					executeKill(player, killer);

				}
			}

		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event){
		if (event instanceof EntityDamageByEntityEvent) return;
		removeFallDMG(event);
		executeUnexpectedKill(event);
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
		Cosmetic dSelected = Cosmetic.getSelected(player, Cosmetic.CosmeticType.DEATH_EFFECT);
		if(dSelected != null) dSelected.activateDeath(player);

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
		Cosmetic kSelected = Cosmetic.getSelected(killer, Cosmetic.CosmeticType.KILL_EFFECT);
		if(kSelected != null) kSelected.activateKill(player);

		if(ks.get(killer.getUniqueId()) >= 50 && !RisenMain.alreadyUsed.contains(killer.getUniqueId())){
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
	public void dd(FallenDeathEvent event){
		Player player = event.getPlayer();
		for(ItemStack item : player.getInventory().getContents()){
			if(NBTUtil.getCustomAttrString(item, "ID").equals("DEATH_DEFIANCE")) {
				event.setCancelled(true);
				Location location = player.getLocation();
				player.getInventory().remove(item);
				player.playSound(location, Sound.DIG_STONE, 10, 1);
				player.playSound(location, Sound.PISTON_EXTEND, 10, 0.9f);
				new BukkitRunnable() {
					float tone = 0;
					@Override
					public void run() {
						if (tone >= 2) cancel();
						else {
							location.getWorld().playSound(player.getLocation(), Sound.ORB_PICKUP, 10, tone);
							tone += 0.1f;
						}
					}
				}.runTaskTimer(DesertMain.getInstance, 0, 2);
				player.sendMessage(ChatColor.YELLOW + "You rise from the ashes!\n" + ChatColor.DARK_GRAY + "Consumed 1 Death Defiance");
				ParticleEffect.FLAME.display(1, 10, 1, 0.2f, 200, player.getLocation(), 15);
				player.setHealth(player.getMaxHealth() * 0.2);
				invincible.add(player.getUniqueId());
				new BukkitRunnable() {
					public void run() {
						invincible.remove(player.getUniqueId());
					}
				}.runTaskLater(DesertMain.getInstance, 50);
				break;
			}
		}
	}

	@EventHandler
	public void healthRegen(EntityRegainHealthEvent e){
		if(e.getEntity() instanceof Player && (e.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED) || e.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.REGEN) || e.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.EATING))){
			if(!DesertMain.eating.contains(e.getEntity().getUniqueId())){
				DesertMain.eating.remove(e.getEntity().getUniqueId());
				e.setCancelled(true);
			}else {
				if(Math.random() >= 0.666) e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void quit(PlayerQuitEvent event){
		ks.put(event.getPlayer().getUniqueId(), 0);
		MilestonesUtil.confirming.remove(event.getPlayer().getUniqueId());
		Rank rank = ConfigUtils.getRank(event.getPlayer());
		if(rank != null)
			event.setQuitMessage(rank.p.toString() + rank.c + " " + event.getPlayer().getName() + " just left. See you around!");
		else event.setQuitMessage("");
	}

	@EventHandler
	public void forScoreboard(PlayerJoinEvent e) {
		final Player p = e.getPlayer();
		new BukkitRunnable() {

			@Override
			public void run() {
				FScoreboardManager.initialize(p);
			}

		}.runTaskTimer(DesertMain.getPlugin(DesertMain.class), 0, 5);
	}
	@EventHandler
	public void forKs(PlayerJoinEvent event){

		ks.putIfAbsent(event.getPlayer().getUniqueId(), 0);
	}

	@EventHandler
	public void onFirstJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		String blockNotifPath = "players." + uuid + ".blocknotifications";
		e.getPlayer().sendMessage("working");
		if(!(main.getConfig().contains("players." + uuid))) {
			main.getConfig().createSection("players." + uuid);
			//init block notifications
			main.getConfig().set(blockNotifPath, true);
			//save
			main.saveConfig();
			e.setJoinMessage(e.getPlayer().getName() + ChatColor.GOLD + " just joined for the first time, give them a warm welcome!");
		}else{
			Rank rank = ConfigUtils.getRank(e.getPlayer());
			if(rank != null)
				e.setJoinMessage(rank.p.toString() + rank + " " + e.getPlayer().getName() + " just joined.");
			else e.setJoinMessage("");
			e.getPlayer().sendMessage(DesertMain.getWelcome());
		}
		if(!main.getConfig().contains("players." + uuid + ".cosmetics")){
			Bukkit.getLogger().warning(ChatColor.RED + "Initializing cosmetics for player " + e.getPlayer().getName());
			//init cosmetics
			Cosmetic.init(e.getPlayer());
		}


		if(main.getConfig().getBoolean(blockNotifPath))
			TravellerEvents.blockNotifs.add(p.getUniqueId());
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
						player.sendMessage(ChatColor.GREEN + "Powered up your next shot for +20% knockback!");
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
					((Player) e.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 1, false, false));
					((Player) e.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 1, false, false));
					((Player) e.getDamager()).playSound(e.getDamager().getLocation(), Sound.ANVIL_LAND, 10, 1.1f);
				}
			}else if(DesertMain.snack.get(e.getDamager().getUniqueId()).equals("energy")){
				DesertMain.snack.remove(e.getDamager().getUniqueId());
				if(e.getEntity() instanceof Player && e.getDamager() instanceof Player){
					((Player) e.getDamager()).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 2, false, false));
					((Player) e.getDamager()).playSound(e.getDamager().getLocation(), Sound.ANVIL_LAND, 10, 1.1f);
				}
			}else if(DesertMain.snack.get(e.getDamager().getUniqueId()).equals("protein")){
				DesertMain.snack.remove(e.getDamager().getUniqueId());
				if(e.getDamager() instanceof Player){
					e.getEntity().setVelocity(e.getEntity().getVelocity().multiply(1.2));
					((Player) e.getDamager()).playSound(e.getDamager().getLocation(), Sound.ANVIL_LAND, 10, 1.1f);
				}
			}
		}catch(NullPointerException ignored){}
	}
}
