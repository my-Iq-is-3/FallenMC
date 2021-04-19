package me.zach.DesertMC.GameMechanics;


import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTEntity;
import de.tr7zw.nbtapi.NBTItem;

import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.ClassManager.CoruManager.EventsForCorruptor;
import me.zach.DesertMC.ClassManager.ScoutManager.EventsForScout;
import me.zach.DesertMC.ClassManager.TankManager.EventsForTank;
import me.zach.DesertMC.ClassManager.WizardManager.EventsForWizard;
import me.zach.DesertMC.Prefix;
import me.zach.DesertMC.ScoreboardManager.FScoreboardManager;
import me.zach.DesertMC.Utils.Config.ConfigUtils;
import me.zach.DesertMC.Utils.Particle.ParticleEffect;
import me.zach.DesertMC.Utils.RankUtils.Rank;
import me.zach.DesertMC.Utils.RankUtils.RankEvents;
import me.zach.DesertMC.Utils.TitleUtils;
import me.zach.DesertMC.Utils.nbt.NBTUtil;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static me.zach.DesertMC.Utils.RankUtils.RankEvents.rankSession;

public class Events implements Listener{
	public static ArrayList<UUID> invincible = new ArrayList<>();
	DesertMain main = DesertMain.getInstance;
	public static HashMap<UUID,Integer> ks = new HashMap<>();

	HashMap<Arrow, ItemStack> arrowArray = new HashMap<>();

	@EventHandler
	public void arrowShoot(ProjectileLaunchEvent event){
		if(event.getEntity().getShooter() instanceof Player && event.getEntity() instanceof Arrow){
			arrowArray.put((Arrow) event.getEntity(),((Player)event.getEntity().getShooter()).getInventory().getItemInHand());
			//ArtifactEvents.shootEvent(event);
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

	FileConfiguration economyConfig = Bukkit.getPluginManager().getPlugin("Fallen").getConfig();


	public static void check(DesertMain main){
		new BukkitRunnable(){
			@Override
			public void run() {
				for(Player p : Bukkit.getOnlinePlayers()){
					if(!DesertMain.eating.contains(p.getUniqueId()))
					p.setFoodLevel(20);
					Location location = p.getLocation();
					Location locbefore = location.clone();
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
	public void onKill(EntityDamageByEntityEvent event) throws Exception {
		//ArtifactEvents.hitEvent(event);
		if(event.isCancelled()) return;
		if(event.getDamage() == 0) return;
		if(event.getDamager() instanceof Player && event.getEntity() instanceof Player){
			Player damager = (Player) event.getDamager();
			DesertMain.lastdmgers.put(event.getEntity().getUniqueId(), damager.getUniqueId());
		}else if(event.getDamager() instanceof Arrow){
			Arrow damager = (Arrow) event.getDamager();
			if(damager.getShooter() instanceof Player){
				Player shooter = (Player) damager.getShooter();
				DesertMain.lastdmgers.put(event.getEntity().getUniqueId(), shooter.getUniqueId());
			}
		}

		if(event.getDamager() instanceof Player){

			if(((Player)event.getDamager()).getItemInHand().getType().equals(Material.DOUBLE_PLANT)){
				event.setCancelled(true);
				return;
			}
			if(event.getEntity() instanceof Player){
				if(DesertMain.ct1players.contains(event.getDamager().getUniqueId())){
					event.setDamage(event.getDamage() * 1.1);
				}
				SPolice.onHit(event);
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
			snackHit(event);
			executeKill(event);
		}

	}

	@EventHandler
	public void eating(PlayerInteractEvent e){
		try {
			if (e.getPlayer().getItemInHand().getType().equals(Material.COOKIE)) {
				if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
					e.getPlayer().setFoodLevel(19);
					DesertMain.eating.add(e.getPlayer().getUniqueId());
					new BukkitRunnable(){
						public void run(){

							e.getPlayer().setFoodLevel(20);
							new BukkitRunnable(){
								public void run(){
									DesertMain.eating.remove(e.getPlayer().getUniqueId());
								}
							}.runTaskLater(Bukkit.getPluginManager().getPlugin("Fallen"), 10);
						}
					}.runTaskLater(Bukkit.getPluginManager().getPlugin("Fallen"), 60);
				}
			}
		}catch(NullPointerException ignored){}
	}

	@EventHandler
	public void onHit(EntityDamageEvent event) throws Exception {
		if(event instanceof EntityDamageByEntityEvent) return;
		executeUnexpectedKill(event);
	}

	private void executeUnexpectedKill(EntityDamageEvent event) throws NullPointerException {
		UUID uuid = DesertMain.lastdmgers.get(event.getEntity().getUniqueId());
		Player killer = Bukkit.getPlayer(uuid);

		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if (player.getHealth() - event.getDamage() < 0.1) {
				event.setCancelled(true);
				if(dd(player)) return;
				callOnKill(player, killer);
				Location spawn = (Location) main.getConfig().get("server.lobbyspawn");
				if (spawn == null) {
					player.teleport(player.getWorld().getSpawnLocation());
				} else player.teleport(spawn);
				new BukkitRunnable() {
					@Override
					public void run() {
						player.setFireTicks(0);
					}
				}.runTaskLater(DesertMain.getInstance, 10);


				int randomCompare = 2;
				double random = (Math.random() * 5) + 1;
				int soulsgained = 0;
				if (random < randomCompare) {
					try {
						
							if (killer.getInventory().getChestplate().getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "Lucky Chestplate")) {
								soulsgained = 2;
							} else soulsgained = 1;
					} catch (Exception ex) {
						soulsgained = 1;
						if (!(ex instanceof NullPointerException)) {
							Bukkit.getConsoleSender().sendMessage("Error occurred checking for lucky chestplate. Error:\n" + Arrays.toString(ex.getStackTrace()));
						}
					}
					
						if (main.getConfig().get("players." + killer.getUniqueId() + ".souls") != null) {
							main.getConfig().set("players." + killer.getUniqueId() + ".souls", main.getConfig().getInt("players." + killer.getUniqueId() + ".souls") + soulsgained);
						} else {
							main.getConfig().set("players." + killer.getUniqueId() + ".souls", soulsgained);
						}
				}
				int xpgained = (ConfigUtils.getLevel(ConfigUtils.findClass(player), player) * 10) + 5 + ks.get(player.getUniqueId()) * 5;

				int gemsgained = (ConfigUtils.getLevel(ConfigUtils.findClass(player), player) * 15) + ks.get(player.getUniqueId()) * 3;
				 if (!ks.containsKey(killer.getUniqueId())) {
					ks.put(killer.getUniqueId(), 1);
				} else {
					ks.put(killer.getUniqueId(), ks.get(killer.getUniqueId()) + 1);
				}
				
					killer.sendMessage(ChatColor.GREEN + "You killed " + ChatColor.YELLOW + player.getName() + ChatColor.DARK_GRAY + " (" + ChatColor.DARK_GRAY + "+" + ChatColor.BLUE + xpgained + " EXP" + ChatColor.DARK_GRAY + ", +" + ChatColor.GREEN + gemsgained + " Gems" + ChatColor.DARK_GRAY + ", +" + ChatColor.LIGHT_PURPLE + soulsgained + " Souls" + ChatColor.DARK_GRAY + ")");
				ks.put(event.getEntity().getUniqueId(), 0);
				DesertMain.snack.remove(player.getUniqueId());

				player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 0.5f);
				 killer.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 4);
				
					economyConfig.set("players." + killer.getUniqueId() + ".balance", economyConfig.getInt("players." + killer.getUniqueId() + ".balance") + gemsgained);
				 ConfigUtils.addXP(killer, ConfigUtils.findClass(killer), xpgained);
			}
		}
	}

	private static void callOnKill(Player player, Player killer){
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

	}
	private Player getPlayer(Entity entity){
		if(entity instanceof Player) return (Player) entity;
		else return (Player) ((Arrow)entity).getShooter();
	}
	public void executeKill(EntityDamageByEntityEvent event){
		if (event.getEntity() instanceof Player && (event.getDamager() instanceof Player || event.getDamager() instanceof Arrow)) {
			Player player = (Player) event.getEntity();
			Player killer = getPlayer(event.getDamager());

			try {
				if (player.getHealth() - event.getDamage() < 0.1) {
					event.setCancelled(true);
					if(dd(player)) return;
					callOnKill(player, killer);
					Location spawn = (Location) main.getConfig().get("server.lobbyspawn");
					player.setHealth(player.getMaxHealth());
					player.teleport(spawn);
					if(player.getFireTicks() > 0)
						player.setFireTicks(0);
					int random = new Random().nextInt(4);
					int soulsgained = 0;
					if (random == 0) {
						Bukkit.getConsoleSender().sendMessage("Souls gained should be 1");
						try {
							if(killer.getInventory().getChestplate().getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "Lucky Chestplate")){
								soulsgained = 2;
							}else soulsgained = 1;
						}catch(Exception ex){
							soulsgained = 1;
							if( !(ex instanceof NullPointerException)){
								Bukkit.getConsoleSender().sendMessage("Error occurred checking for lucky chestplate. Error:\n" + ex);
							}
						}
						if (main.getConfig().get("players." + killer.getUniqueId() + ".souls") != null) {
							main.getConfig().set("players." + killer.getUniqueId() + ".souls", main.getConfig().getInt("players." + killer.getUniqueId() + ".souls") + soulsgained);
						} else {
							main.getConfig().set("players." + killer.getUniqueId() + ".souls", soulsgained);
						}
					} else {
						soulsgained = 0;
					}
					int xpgained = (ConfigUtils.getLevel(ConfigUtils.findClass(player), player) * 10) + 5 + (ks.get(event.getDamager().getUniqueId()) * 5);
					int gemsgained = (ConfigUtils.getLevel(ConfigUtils.findClass(player), player) * 15) + (ks.get(event.getDamager().getUniqueId()) * 3);
					DesertMain.snack.remove(player.getUniqueId());
					event.getDamager().sendMessage(ChatColor.GREEN + "You killed " + ChatColor.YELLOW + player.getName() + ChatColor.DARK_GRAY + " (" + ChatColor.DARK_GRAY + "+" + ChatColor.BLUE + xpgained + " EXP" + ChatColor.DARK_GRAY + ", +" + ChatColor.GREEN + gemsgained + " Gems" + ChatColor.DARK_GRAY + ", +" + ChatColor.LIGHT_PURPLE + soulsgained + " Souls" + ChatColor.DARK_GRAY + ")");
					if (!ks.containsKey(event.getDamager().getUniqueId())) {
						ks.put(event.getDamager().getUniqueId(), 1);
					} else {
						ks.put(event.getDamager().getUniqueId(), ks.get(event.getDamager().getUniqueId()) + 1);
					}
					ks.put(event.getEntity().getUniqueId(), 0);
					player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 0.5f);
					killer.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 4);
					killer.playSound(player.getLocation(), Sound.BURP, 6, 1.3f);
					killer.playSound(player.getLocation(), Sound.SHOOT_ARROW, 7, 1.1f);
					economyConfig.set("players." + killer.getUniqueId() + ".balance", economyConfig.getInt("players." + killer.getUniqueId() + ".balance") + gemsgained);
					ConfigUtils.addXP(killer, ConfigUtils.findClass(killer), xpgained);
					main.saveConfig();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}



		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void rankSession(PlayerJoinEvent e){
		try {
			UUID uuid = e.getPlayer().getUniqueId();
			Rank rank = Rank.valueOf(main.getConfig().getString("player." + uuid + ".rank"));
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
						}.runTaskTimer(Bukkit.getPluginManager().getPlugin("Fallen"), 0, 2);
						player.sendMessage(ChatColor.YELLOW + "You rise from the ashes!\n" + ChatColor.DARK_GRAY + "Consumed 1 Death Defiance");
						ParticleEffect.FLAME.display(1, 10, 1, 0, 200, player.getLocation(), 15);
						player.setHealth(player.getMaxHealth() * 0.2);
						invincible.add(player.getUniqueId());
						new BukkitRunnable() {
							public void run() {
								invincible.remove(player.getUniqueId());
							}
						}.runTaskLater(Bukkit.getPluginManager().getPlugin("Fallen"), 50);
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
		if(!ks.containsKey(event.getPlayer().getUniqueId())) ks.put(event.getPlayer().getUniqueId(), 0);
	}
	
	@EventHandler
	public void onFirstJoin(PlayerJoinEvent e) {
		e.getPlayer().sendMessage("working");
		if(!(main.getConfig().contains("players." + e.getPlayer().getUniqueId()))) {
			main.getConfig().createSection("players." + e.getPlayer().getUniqueId());
			/* classes */
			//in use
			main.getConfig().set("players." + e.getPlayer().getUniqueId() + ".classes.inuse", "none");
			
			//level
			main.getConfig().set("players." + e.getPlayer().getUniqueId() + ".classes.tank.level", 1);
			main.getConfig().set("players." + e.getPlayer().getUniqueId() + ".classes.wizard.level", 1);
			main.getConfig().set("players." + e.getPlayer().getUniqueId() + ".classes.scout.level", 1);
			main.getConfig().set("players." + e.getPlayer().getUniqueId() + ".classes.corrupter.level", 1);
			//xp to next
			main.getConfig().set("players." + e.getPlayer().getUniqueId() + ".classes.tank.xptonext", 100);
			main.getConfig().set("players." + e.getPlayer().getUniqueId() + ".classes.scout.xptonext", 100);
			main.getConfig().set("players." + e.getPlayer().getUniqueId() + ".classes.wizard.xptonext", 100);
			main.getConfig().set("players." + e.getPlayer().getUniqueId() + ".classes.corrupter.xptonext", 100);
			//has xp
			main.getConfig().set("players." + e.getPlayer().getUniqueId() + ".classes.tank.hasxp", 0);
			main.getConfig().set("players." + e.getPlayer().getUniqueId() + ".classes.scout.hasxp", 0);
			main.getConfig().set("players." + e.getPlayer().getUniqueId() + ".classes.wizard.hasxp", 0);
			main.getConfig().set("players." + e.getPlayer().getUniqueId() + ".classes.corrupter.hasxp", 0);
			//init titles
			TitleUtils.initializeTitles(e.getPlayer());
			//save
			main.saveConfig();
			e.setJoinMessage(e.getPlayer().getName() + ChatColor.GOLD + " just joined for the first time, give them a warm welcome!");
		} else {
			if(main.getConfig().getString("players." + e.getPlayer().getUniqueId() + ".rank") != null)e.setJoinMessage(Rank.valueOf(main.getConfig().getString("players." + e.getPlayer().getUniqueId() + ".rank")).p + Rank.valueOf(main.getConfig().getString("players." + e.getPlayer().getUniqueId() + ".rank")).c.toString() + " " + e.getPlayer().getName() + " just joined.");
			else e.setJoinMessage("");
		}
		Player p = e.getPlayer();
		p.sendMessage(ChatColor.GREEN + "Welcome to the FallenMC pre-alpha testing server!\nUse /kot to select a class and see it's rewards." + ChatColor.YELLOW + " If you are not opped, press your designated op command block." + ChatColor.GREEN + " Use /item to grant any of the items from the classes. Items are tabcompleteable, press space then tab after typing out /item to see all of them." + ChatColor.YELLOW + " An items ability WILL NOT WORK if you don't have the right class selected and at the right level. To do this, select the class with /kot and level it using /classexp (your class here in lowercase) 99999." + ChatColor.GREEN + "\n To open the traits menu use /traits, and to give yourself more trait tokens use /traitsconfig set int (your uuid here) 99999. \n For the Enchant Refinery, use /testinv, but you will need a hammer and book first. To get a hammer, enter /givehammer (level 1-5). Each level is a different hammer. To give yourself a dummy book, use /givebookdummy. \nPLease scroll up to read this whole thing, and have fun testing!");


		
	}
	public static void executeKill(Player player, Player killer){
		try {
			if(dd(player)) return;
				callOnKill(player, killer);
				EventsForWizard.INSTANCE.wizardt1(killer);
				Location spawn = (Location) DesertMain.getInstance.getConfig().get("server.lobbyspawn");
				player.setHealth(player.getMaxHealth());

				player.teleport(spawn);
				if(player.getFireTicks() > 0)
					player.setFireTicks(0);


				double random = (Math.random() * 5) + 1;
				int soulsgained = 0;
				int randomCompare = 2;
				if (random < randomCompare) {
					try {
						if(new NBTItem(killer.getInventory().getChestplate()).getCompound("CustomAttributes").getString("ID").equals("LUCKY_CHESTPLATE")) soulsgained = 2;
						else{
							soulsgained = 1;
						}
					}catch(Exception ex){
						soulsgained = 1;
						if( !(ex instanceof NullPointerException)){

							Bukkit.getConsoleSender().sendMessage("Error managing souls. Error:\n" + Arrays.toString(ex.getStackTrace()));
						}
					}
					if (DesertMain.getInstance.getConfig().get("players." + killer.getUniqueId() + ".souls") != null) {
						DesertMain.getInstance.getConfig().set("players." + killer.getUniqueId() + ".souls", DesertMain.getInstance.getConfig().getInt("players." + killer.getUniqueId() + ".souls") + soulsgained);
					} else {
						DesertMain.getInstance.getConfig().set("players." + killer.getUniqueId() + ".souls", soulsgained);
					}

				}
				int xpgained = (ConfigUtils.getLevel(ConfigUtils.findClass(player), player) * 10) + 5 + ks.get(player.getUniqueId()) * 5;

				int gemsgained = (ConfigUtils.getLevel(ConfigUtils.findClass(player), player) * 15) + ks.get(player.getUniqueId()) * 3;

				killer.sendMessage(ChatColor.GREEN + "You killed " + ChatColor.YELLOW + player.getName() + ChatColor.DARK_GRAY + " (" + ChatColor.DARK_GRAY + "+" + ChatColor.BLUE + xpgained + " EXP" + ChatColor.DARK_GRAY + ", +" + ChatColor.GREEN + gemsgained + " Gems" + ChatColor.DARK_GRAY + ", +" + ChatColor.LIGHT_PURPLE + soulsgained + " Souls" + ChatColor.DARK_GRAY + ")");
				if (!ks.containsKey(killer.getUniqueId())) {
					ks.put(killer.getUniqueId(), 1);
				} else {
					ks.put(killer.getUniqueId(), ks.get(killer.getUniqueId()) + 1);
				}
				DesertMain.snack.remove(player.getUniqueId());
				ks.put(player.getUniqueId(), 0);
				player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 0.5f);
				((Player) killer).playSound(player.getLocation(), Sound.LEVEL_UP, 1, 4);
				killer.playSound(player.getLocation(), Sound.BURP, 6, 1.3f);
				killer.playSound(player.getLocation(), Sound.SHOOT_ARROW, 7, 1.1f);
				Bukkit.getPluginManager().getPlugin("Fallen").getConfig().set("players." + killer.getUniqueId() + ".balance", Bukkit.getPluginManager().getPlugin("Fallen").getConfig().getInt("players." + killer.getUniqueId() + ".balance") + gemsgained);
				ConfigUtils.addXP(killer, ConfigUtils.findClass(killer), xpgained);
				DesertMain.getInstance.saveConfig();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
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
