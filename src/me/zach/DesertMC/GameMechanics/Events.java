package me.zach.DesertMC.GameMechanics;


import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTEntity;
import de.tr7zw.nbtapi.NBTItem;

import me.zach.DesertMC.ClassManager.TravellerEvents;
import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.ClassManager.CoruManager.EventsForCorruptor;
import me.zach.DesertMC.ClassManager.ScoutManager.EventsForScout;
import me.zach.DesertMC.ClassManager.TankManager.EventsForTank;
import me.zach.DesertMC.ClassManager.WizardManager.EventsForWizard;
import me.zach.DesertMC.GameMechanics.EXPMilesstones.MilestonesUtil;
import me.zach.DesertMC.Prefix;
import me.zach.DesertMC.ScoreboardManager.FScoreboardManager;
import me.zach.DesertMC.Utils.Config.ConfigUtils;
import me.zach.DesertMC.Utils.Particle.ParticleEffect;
import me.zach.DesertMC.Utils.PlayerUtils;
import me.zach.DesertMC.Utils.RankUtils.Rank;
import me.zach.DesertMC.Utils.TitleUtils;
import me.zach.DesertMC.Utils.nbt.NBTUtil;
import me.zach.DesertMC.cosmetics.Cosmetic;
import me.zach.artifacts.events.ArtifactEvents;
import me.zach.artifacts.gui.inv.ArtifactData;
import me.zach.artifacts.gui.inv.items.CreeperTrove;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;


import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static me.zach.DesertMC.Utils.RankUtils.RankEvents.rankSession;

public class Events implements Listener{
	public static Set<UUID> invincible = new HashSet<>();
	Plugin main = DesertMain.getInstance;
	public static HashMap<UUID,Integer> ks = new HashMap<>();
	public static HashMap<UUID, Integer> scoutTraveller = new HashMap<>();

	public static HashMap<Arrow, ItemStack> arrowArray = new HashMap<>();

//	public void initScoutTraveller(){
//		new BukkitRunnable(){
//			public void run(){
//				TravellerEvents.travelled.forEach((uuid, blocks) -> {
//					if(ConfigUtils.findClass(uuid).equalsIgnoreCase("scout") && ConfigUtils.getLevel("scout", uuid) > 5){
//						Player player = Bukkit.getPlayer(uuid);
//						float walkSpeed = player.getWalkSpeed();
//						int newSpeed = walkSpeed +
//						if(walkSpeed != )
//					}
//				});
//			}
//		}.runTaskTimer(main, 10, 77);
//	}

	@EventHandler
	public void arrowShoot(ProjectileLaunchEvent event){
		if(event.getEntity().getShooter() instanceof Player && event.getEntity() instanceof Arrow){
			Arrow arrow = (Arrow) event.getEntity();
			Player player = (Player) event.getEntity().getShooter();
			arrowArray.put(arrow, player.getInventory().getItemInHand());
			ArtifactEvents.shootEvent(event);

			Cosmetic cosmetic = Cosmetic.getSelected(player, Cosmetic.CosmeticType.ARROW_TRAIL);
			if(cosmetic != null) cosmetic.activateArrow(arrow, new ArtifactData(player).bowS());
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
			if(event.getPlayer().hasPermission("admin")){
				return;
			}
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "You do not have permission to use this command.");
		}
	}

	public void cancelAnvil(InventoryOpenEvent event){
		if(event.getInventory() instanceof AnvilInventory){
			Player player = (Player) event.getPlayer();
			event.setCancelled(true);
			Bukkit.getLogger().warning("Uh-oh! Player " + player.getName() + " (" + player.getUniqueId() + ") just attempted to open an anvil inventory. Don't worry, I cancelled it.");
		}
	}

	public void travellerTank(EntityDamageByEntityEvent e){
		if(e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();
			if(ConfigUtils.getLevel(ConfigUtils.findClass(player), player) > 5){
				UUID uuid = e.getDamager().getUniqueId();
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
		Set<Block> blockSet = TravellerEvents.travelled.get(uuid);
		if(blockSet != null){
			double multiplier = 1 + blockSet.size() * 0.0002;
			e.setDamage(e.getDamage() * multiplier);
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



	public static void check(DesertMain main){
		new BukkitRunnable(){
			@Override
			public void run() {
				for(Player p : Bukkit.getOnlinePlayers()){
					if(!DesertMain.eating.contains(p.getUniqueId()))
					p.setFoodLevel(20);
					Location location = p.getLocation();
					Location locbefore = location.clone();


					if (!PlayerUtils.fighting.containsKey(p.getUniqueId()))
						PlayerUtils.fighting.put(p.getUniqueId(), 11);
					int incombat = PlayerUtils.fighting.get(p.getUniqueId());
					if (incombat > 0) PlayerUtils.fighting.put(p.getUniqueId(), incombat - 1);


					if(locbefore.getBlock().getType().equals(Material.LAVA) || locbefore.getBlock().getType().equals(Material.STATIONARY_LAVA)){
						p.sendMessage(Prefix.DEBUG + "1");
						if(ConfigUtils.findClass(p).equals("corrupter") && ConfigUtils.getLevel("corrupter",p) > 7){
							if(p.getHealth() + 0.5 <= p.getMaxHealth()){
								p.setHealth(p.getHealth() + 0.5);
							}else if(p.getHealth() + 0.5  > p.getMaxHealth()){
								p.setHealth(p.getMaxHealth());
							}
							ParticleEffect.VILLAGER_HAPPY.display(1,1,1,0,50,locbefore,10);
						}
					}
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


	private Player getPlayer(Entity entity) {

		if (entity instanceof Player) return (Player) entity;
		else return (Player) ((Arrow) entity).getShooter();
	}
	// ---------------------------------------------------------------------------


	@EventHandler
	public void onKill(EntityDamageByEntityEvent event) {

		ArtifactEvents.hitEvent(event);
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
		if (event.getEntity() instanceof Player) {

			PlayerUtils.setFighting((Player) event.getEntity());
		}
		if (event.getDamager() instanceof Player) {

			if(((Player)event.getDamager()).getItemInHand().getType().equals(Material.DOUBLE_PLANT)){
				event.setCancelled(true);
				return;
			}
			if(event.getEntity() instanceof Player){
				if(DesertMain.ct1players.contains(event.getDamager().getUniqueId())){
					event.setDamage(event.getDamage() * 1.1);
				}
				SPolice.onHit(event);
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
			executeKill(event);
		}
	}

	public void executeKill(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && (event.getDamager() instanceof Player || event.getDamager() instanceof Arrow)) {
			Player player = (Player) event.getEntity();
			Player killer = getPlayer(event.getDamager());
			if(player.getHealth() - event.getDamage() < 0.1)
				executeKill(player, killer);


		}

	}


	public static void executeKill(Player player, Player killer) {
		try {
			callOnKill(player, killer);
			EventsForWizard.INSTANCE.wizardt1(killer);
			Location spawn = (Location) DesertMain.getInstance.getConfig().get("server.lobbyspawn");
			player.setHealth(player.getMaxHealth());

			player.teleport(spawn);
			if (player.getFireTicks() > 0)
				player.setFireTicks(0);


			double random = (Math.random() * 5) + 1;
			int soulsgained = 0;
			int randomCompare = 2;
			if (random < randomCompare) {
				try {
					if (new NBTItem(killer.getInventory().getChestplate()).getCompound("CustomAttributes").getString("ID").equals("LUCKY_CHESTPLATE"))
						soulsgained = 2;
					else {
						soulsgained = 1;
					}
				} catch (Exception ex) {
					soulsgained = 1;
					if (!(ex instanceof NullPointerException)) {

						Bukkit.getConsoleSender().sendMessage("Error managing souls. Error:\n" + Arrays.toString(ex.getStackTrace()));
					}
				}
			}
			int xpgained = (ConfigUtils.getLevel(ConfigUtils.findClass(player), player) * 10) + 5 + ks.get(player.getUniqueId()) * 5;

			int gemsgained = (ConfigUtils.getLevel(ConfigUtils.findClass(player), player) * 15) + ks.get(player.getUniqueId()) * 3;

			killer.sendMessage(ChatColor.GREEN + "You killed " + ChatColor.YELLOW + player.getName() + ChatColor.DARK_GRAY + " (" + ChatColor.DARK_GRAY + "+" + ChatColor.BLUE + xpgained + " EXP" + ChatColor.DARK_GRAY + ", +" + ChatColor.GREEN + gemsgained + " Gems" + ChatColor.DARK_GRAY + ", +" + ChatColor.LIGHT_PURPLE + soulsgained + " Souls" + ChatColor.DARK_GRAY + ")");
			ks.putIfAbsent(killer.getUniqueId(), 0);
			ks.put(killer.getUniqueId(), ks.get(killer.getUniqueId()) + 1);

			DesertMain.snack.remove(player.getUniqueId());
			ks.put(player.getUniqueId(), 0);
			player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 0.5f);
			killer.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 4);
			killer.playSound(player.getLocation(), Sound.BURP, 6, 1.3f);
			killer.playSound(player.getLocation(), Sound.SHOOT_ARROW, 7, 1.1f);
			ConfigUtils.addGems(killer, gemsgained);
			ConfigUtils.addXP(killer, ConfigUtils.findClass(killer), xpgained);
			ConfigUtils.addSouls(killer, soulsgained);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void executeUnexpectedKill(EntityDamageEvent event) throws NullPointerException {
		UUID uuid = DesertMain.lastdmgers.get(event.getEntity().getUniqueId());
		Player killer = Bukkit.getPlayer(uuid);
		if (event.getEntity() instanceof Player) {
			executeKill((Player) event.getEntity(), killer);
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) throws Exception {
		if (event instanceof EntityDamageByEntityEvent) return;
		removeFallDMG(event);
		executeUnexpectedKill(event);

	}

	private static void callOnKill(Player player, Player killer) {
		PlayerUtils.setIdle(player);
		PlayerUtils.setFighting(killer);
		EventsForWizard.INSTANCE.wizardt1(killer);
		try{
			if(new NBTItem(killer.getItemInHand()).getCompound("CustomAttributes").getString("ID").equals("WIZARD_BLADE")) EventsForWizard.addBladeCharge(killer);
		}catch(Exception ex){
			if (!(ex instanceof NullPointerException)){
				Bukkit.getConsoleSender().sendMessage("Error adding charge to wizard blade, error:");
				ex.printStackTrace();
			}
		}
		try {
			if(new NBTItem(killer.getInventory().getLeggings()).getString("ID").equals("CORRUPTER_LEGGINGS")) EventsForCorruptor.INSTANCE.corrupterLeggings(killer, player);
		}catch(Exception ex){
			if(!(ex instanceof NullPointerException)){
				Bukkit.getConsoleSender().sendMessage("Error checking for corrupter leggings: " + ex.toString());
			}
		}
		for(int i = 0; i<36; i++){
			ItemStack item = killer.getInventory().getItem(i);
			try{
				NBTItem nbt = new NBTItem(item);
				NBTCompound compound = nbt.getCompound("CustomAttributes");
				if(compound.getString("ID").equals("WIZARD_BLADE")){
					compound.setInteger("CHARGE", 0);
					player.getInventory().setItem(i, nbt.getItem());
				}
			}catch(NullPointerException ignored){}
		}

		SPolice.onKill(killer);
		Cosmetic kSelected = Cosmetic.getSelected(killer, Cosmetic.CosmeticType.KILL_EFFECT);
		if(kSelected != null) kSelected.activateKill(player);
		Cosmetic dSelected = Cosmetic.getSelected(player, Cosmetic.CosmeticType.DEATH_EFFECT);
		if(dSelected != null) dSelected.activateDeath(player);

		if(TravellerEvents.travelled.containsKey(player.getUniqueId()))
			TravellerEvents.travelled.get(player.getUniqueId()).clear();
	}

	@EventHandler
	public void resetConfirmRemove(PlayerQuitEvent e){
		MilestonesUtil.confirming.remove(e.getPlayer().getUniqueId());}

	@EventHandler(priority = EventPriority.LOW)
	public void rankSession(PlayerJoinEvent e){
		try {
			UUID uuid = e.getPlayer().getUniqueId();
			Rank rank = Rank.valueOf(main.getConfig().getString("players." + uuid + ".rank"));
			rankSession.put(uuid, rank);
			Bukkit.getConsoleSender().sendMessage("Updated rank session for player " + e.getPlayer().getName() + ", rankSession:\n" + rankSession + "\nAdded rank " + rank);
		}catch(IllegalArgumentException | NullPointerException ignored){}
	}

	public static boolean dd(Player player){
		AtomicBoolean dd = new AtomicBoolean(false);
		player.getInventory().forEach(item -> {
			if(!dd.get()){
				try {
					if(NBTUtil.INSTANCE.getCustomAttr(item, "ID").equals("DEATH_DEFIANCE")) {
						dd.set(true);
						player.getInventory().remove(item);
						player.playSound(player.getLocation(), Sound.DIG_STONE, 10, 1);
						player.playSound(player.getLocation(), Sound.PISTON_EXTEND, 10, 0.9f);
						new BukkitRunnable() {
							float tone = 0;
							@Override
							public void run() {
								if (tone >= 2) cancel();
								else {
									player.playSound(player.getLocation(), Sound.ORB_PICKUP, 100, tone);
									tone += 0.1f;
								}
							}
						}.runTaskTimer(DesertMain.getInstance, 0, 2);
						player.sendMessage(ChatColor.YELLOW + "You rise from the ashes!\n" + ChatColor.DARK_GRAY + "Consumed 1 Death Defiance");
						ParticleEffect.FLAME.display(1, 10, 1, 0, 200, player.getLocation(), 15);
						player.setHealth(player.getMaxHealth() * 0.2);
						invincible.add(player.getUniqueId());
						new BukkitRunnable() {
							public void run() {
								invincible.remove(player.getUniqueId());
							}
						}.runTaskLater(DesertMain.getInstance, 50);
					}
				}catch(NullPointerException ignored){}
			}
		});
		return dd.get();
	}

	@EventHandler
	public void healthRegen(EntityRegainHealthEvent e){
		if(e.getEntity() instanceof Player && (e.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED) || e.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.REGEN))){
			if(!DesertMain.eating.contains(e.getEntity().getUniqueId())){
				DesertMain.eating.remove(e.getEntity().getUniqueId());
				e.setCancelled(true);
			}else {
				int random = (int) (Math.random() * 3) + 1;
				if (random >= 2) {
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void clearks(PlayerQuitEvent event){
		ks.put(event.getPlayer().getUniqueId(), 0);
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
			/* classes */
			//in use

			//init titles
			TitleUtils.initializeTitles(e.getPlayer());
			//init displaycase
			main.getConfig().set("players." + uuid + ".displaycase", ChatColor.YELLOW + "" + 0);
			//init block notifications
			main.getConfig().set(blockNotifPath, true);
			//save
			main.saveConfig();
			e.setJoinMessage(e.getPlayer().getName() + ChatColor.GOLD + " just joined for the first time, give them a warm welcome!");
		}else{
			if(main.getConfig().getString("players." + uuid + ".rank") != null)
				e.setJoinMessage(Rank.valueOf(main.getConfig().getString("players." + uuid + ".rank")).p + Rank.valueOf(main.getConfig().getString("players." + uuid + ".rank")).c.toString() + " " + e.getPlayer().getName() + " just joined.");
			else e.setJoinMessage("");
		}
		if(!main.getConfig().contains("players." + uuid + ".cosmetics")){
			Bukkit.getLogger().warning(ChatColor.RED + "Initializing cosmetics for player " + e.getPlayer().getName());
			//init cosmetics
			Cosmetic.init(e.getPlayer());
		}
		if(!main.getConfig().contains("players." + uuid + ".displaycase")) main.getConfig().set("players." + uuid + ".displaycase", ChatColor.YELLOW + "" + 0);


		if(main.getConfig().getBoolean(blockNotifPath))
			TravellerEvents.blockNotifs.add(p.getUniqueId());
	}



	/*
	@EventHandler
	public void removeTrail(EntityDamageByEntityEvent e){
		if(e.getDamager() instanceof Arrow){
			UUID uuid = e.getDamager().getUniqueId();
			try{
				Bukkit.getScheduler().cancelTask(Cosmetic.trails.get(uuid));
				Cosmetic.trails.remove(uuid);
			}catch(Exception ex){Cosmetic.trails.remove(uuid);}
		}
	}
	 */



	@EventHandler
	public void snackEat(PlayerItemConsumeEvent event){
		Player player = event.getPlayer();
		try {
			if (NBTUtil.INSTANCE.getCustomAttr(event.getItem(), "ID").equals("LAVA_CAKE")){
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
			if (NBTUtil.INSTANCE.getCustomAttr(event.getItem(), "ID").equals("PROTEIN_SNACK")){
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
			if (NBTUtil.INSTANCE.getCustomAttr(event.getItem(), "ID").equals("MAGIC_SNACK")){
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
			if (NBTUtil.INSTANCE.getCustomAttr(event.getItem(), "ID").equals("ENERGY_SNACK")){
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
