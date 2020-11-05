package me.zach.DesertMC.GameMechanics;


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
import me.zach.DesertMC.Utils.TitleUtils;
import me.zach.DesertMC.Utils.nbt.NBTUtil;
import org.apache.commons.lang.ObjectUtils;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class Events implements Listener {

	DesertMain main = DesertMain.getInstance;
	public static HashMap<UUID,Integer> ks = new HashMap<UUID, Integer>();

	HashMap<Arrow, ItemStack> arrowArray = new HashMap<>();

	@EventHandler
	public void arrowShoot(ProjectileLaunchEvent event){
		if(event.getEntity().getShooter() instanceof Player && event.getEntity() instanceof Arrow){
			arrowArray.put((Arrow) event.getEntity(),((Player)event.getEntity().getShooter()).getInventory().getItemInHand());
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
		        		if(main.getConfig().getBoolean("players." + uuid + ".invincible")) {
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
					p.setFoodLevel(20);
					Location location = p.getLocation();
					Location locbefore = location.clone();

					for(Entity e:location.getWorld().getEntities()){
						if(e instanceof ArmorStand){
							NBTEntity nbta = new NBTEntity(e);
							if(nbta.getBoolean("Hellfire")){
								if(e.getLocation().clone().add(0,10,0).distanceSquared(p.getLocation()) <= 2.5){
									if(p.getHealth() <= 4) p.damage(9999999,Bukkit.getPlayer(UUID.fromString(nbta.getString("Owner"))));
									if(p.getHealth() > 4){
										p.damage(0,Bukkit.getPlayer(UUID.fromString(nbta.getString("Owner"))));
										p.setHealth(p.getHealth()-4);
									}
								}
							}
						}
					}





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
		if(DesertMain.crouchers.get(event.getPlayer().getUniqueId()) != null){
			if(DesertMain.crouchers.get(event.getPlayer().getUniqueId())) {
				DesertMain.crouchers.put(event.getPlayer().getUniqueId(), false);
			}else{
				DesertMain.crouchers.put(event.getPlayer().getUniqueId(), true);
			}

		}else{
			DesertMain.crouchers.put(event.getPlayer().getUniqueId(),true);
		}
	}

	@EventHandler
	public void onKill(EntityDamageByEntityEvent event) throws Exception {
		if(event.isCancelled()) return;
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
		
	    if(DesertMain.ct1players.contains(event.getDamager().getUniqueId())){
			event.setDamage(event.getDamage() * 1.1);
		}

		EventsForCorruptor.INSTANCE.t8Event(event);
		EventsForCorruptor.INSTANCE.noMercy(event);
		EventsForCorruptor.INSTANCE.t1Event(event);
		EventsForWizard.INSTANCE.wizardt4(event);
		EventsForWizard.INSTANCE.wizardt1(event);
		EventsForWizard.INSTANCE.wizardt8(event);
		EventsForScout.getInstance().t1Event(event);
		EventsForScout.getInstance().t4Event(event);
		EventsForScout.getInstance().daggerHit(event);
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
		snackHit(event);

		executeKill(event);


		

	}

	@EventHandler
	public void onHit(EntityDamageEvent event) throws Exception {
		if(event instanceof EntityDamageByEntityEvent) return;
		executeUnexpectedKill(event);
	}

	private void executeUnexpectedKill(EntityDamageEvent event) throws Exception {
		UUID uuid = DesertMain.lastdmgers.get(event.getEntity().getUniqueId());
		Player killer = Bukkit.getPlayer(uuid);
		if(event.getEntity() instanceof Player){
			Player player = (Player) event.getEntity();
			if (player.getHealth() - event.getDamage() < 0.1) {
				Location spawn = (Location) main.getConfig().get("server.lobbyspawn");
				player.setHealth(player.getMaxHealth());
				player.teleport(spawn);
				try {
					if(new NBTItem(killer.getInventory().getLeggings()).getString("ID").equals("CORRUPTER_LEGGINGS")) EventsForCorruptor.INSTANCE.corrupterLeggings(killer, player);
				}catch(Exception ex){
					if(!(ex instanceof NullPointerException)){
						Bukkit.getConsoleSender().sendMessage("Error checking for corrupter leggings: " + ex.toString());
					}
				}
				new BukkitRunnable(){
					@Override
					public void run() {
						player.setFireTicks(0);
					}
				}.runTaskLater(DesertMain.getInstance,10);

				event.setCancelled(true);
				int randomCompare = 2;
				double random = (Math.random() * 5) + 1;
				int soulsgained = 0;

				if (random < randomCompare) {
					try {
						if(killer.getInventory().getChestplate().getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "Lucky Chestplate")){
							soulsgained = 2;
						}else soulsgained = 1;
					}catch(Exception ex){
						soulsgained = 1;
						if( !(ex instanceof NullPointerException)){
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

				for(ItemStack item : player.getInventory().getContents()){
					try{
						if(new NBTItem(item).getCompound("CustomAttributes").getString("ID").equals("WIZARD_BLADE")) new NBTItem(item).getCompound("CustomAttributes").setInteger("CHARGE", 0);
					}catch(NullPointerException ignored){}
				}
				DesertMain.snack.remove(player.getUniqueId());

				player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 0.5f);
				killer.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 4);
				economyConfig.set("players." + killer.getUniqueId() + ".balance", economyConfig.getInt("players." + killer.getUniqueId() + ".balance") + gemsgained);
				ConfigUtils.addXP(killer, ConfigUtils.findClass(killer), xpgained);
			}
		}

	}
	private Player getPlayer(Entity entity){
		if(entity instanceof Player) return (Player) entity;
		else return (Player) ((Arrow)entity).getShooter();
	}
	public void executeKill(EntityDamageByEntityEvent event) throws Exception {
		if (event.getEntity() instanceof Player && (event.getDamager() instanceof Player || event.getDamager() instanceof Arrow)) {
			Player player = (Player) event.getEntity();
			Player killer = getPlayer(event.getDamager());

			try {

				if (player.getHealth() - event.getDamage() < 0.1) {
					try{
						if(new NBTItem(((Player) event.getDamager()).getItemInHand()).getCompound("CustomAttributes").getString("ID").equals("WIZARD_BLADE")) EventsForWizard.addBladeCharge(((Player) event.getDamager()));
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
					Location spawn = (Location) main.getConfig().get("server.lobbyspawn");
					player.setHealth(player.getMaxHealth());

					player.teleport(spawn);
					if(player.getFireTicks() > 0)
						player.setFireTicks(0);
					event.setCancelled(true);
					int randomCompare = 2;
					double random = (Math.random() * 5) + 1;
					int soulsgained = 0;
					if (random < randomCompare) {
						try {
							if(killer.getInventory().getChestplate().getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "Lucky Chestplate")){
								soulsgained = 2;
							}else soulsgained = 1;
						}catch(Exception ex){
							soulsgained = 1;
							if( !(ex instanceof NullPointerException)){
								Bukkit.getConsoleSender().sendMessage("Error occurred checking for lucky chestplate. Error:\n" + Arrays.toString(ex.getStackTrace()));
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
					int xpgained = (ConfigUtils.getLevel(ConfigUtils.findClass(player), player) * 10) + 5 + ks.get(player.getUniqueId()) * 5;

					int gemsgained = (ConfigUtils.getLevel(ConfigUtils.findClass(player), player) * 15) + ks.get(player.getUniqueId()) * 3;
					for(ItemStack item : player.getInventory().getContents()){
						try{
							if(new NBTItem(item).getCompound("CustomAttributes").getString("ID").equals("WIZARD_BLADE")) new NBTItem(item).getCompound("CustomAttributes").setInteger("CHARGE", 0);
						}catch(NullPointerException ignored){}
					}
					DesertMain.snack.remove(player.getUniqueId());
					event.getDamager().sendMessage(ChatColor.GREEN + "You killed " + ChatColor.YELLOW + player.getName() + ChatColor.DARK_GRAY + " (" + ChatColor.DARK_GRAY + "+" + ChatColor.BLUE + xpgained + " EXP" + ChatColor.DARK_GRAY + ", +" + ChatColor.GREEN + gemsgained + " Gems" + ChatColor.DARK_GRAY + ", +" + ChatColor.LIGHT_PURPLE + soulsgained + " Souls" + ChatColor.DARK_GRAY + ")");
					if (!ks.containsKey(event.getDamager().getUniqueId())) {
						ks.put(event.getDamager().getUniqueId(), 1);
					} else {
						ks.put(event.getDamager().getUniqueId(), ks.get(event.getDamager().getUniqueId()) + 1);
					}
					ks.put(event.getEntity().getUniqueId(), 0);
					player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 0.5f);
					((Player) event.getDamager()).playSound(player.getLocation(), Sound.LEVEL_UP, 1, 4);
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
	@EventHandler
	public void healthRegen(EntityRegainHealthEvent e){
		if(e.getEntity() instanceof Player && (e.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED) || e.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.REGEN))){
			int random = (int) (Math.random() * 3) + 1;
			if(random >= 2){
				e.setCancelled(true);
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
			//init invincible
			main.getConfig().set("players." + e.getPlayer().getUniqueId() + ".invincible", false);

			
			//init titles
			TitleUtils.initializeTitles(e.getPlayer());
			//save
			main.saveConfig();
			e.setJoinMessage(e.getPlayer().getName() + ChatColor.GOLD + " just joined for the first time, give them a warm welcome!");
		} else {
			e.setJoinMessage("");
		}


		
	}
	public static void executeKill(Player player, Player killer){
		try {


				Location spawn = (Location) DesertMain.getInstance.getConfig().get("server.lobbyspawn");
				player.setHealth(player.getMaxHealth());

				player.teleport(spawn);
				if(player.getFireTicks() > 0)
					player.setFireTicks(0);


				double random = (Math.random() * 5) + 1;
				int soulsgained = 0;
				int randomCompare = 2;
				try {
					if(new NBTItem(killer.getInventory().getLeggings()).getString("ID").equals("CORRUPTER_LEGGINGS")) EventsForCorruptor.INSTANCE.corrupterLeggings(killer, player);
				}catch(Exception ex){
					if(!(ex instanceof NullPointerException)){
						Bukkit.getConsoleSender().sendMessage("Error checking for corrupter leggings: " + ex.toString());
					}
				}
				if (random < randomCompare) {
					try {
						if(killer.getInventory().getChestplate().getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "Lucky Chestplate")) soulsgained = 2;
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
				for(ItemStack item : player.getInventory().getContents()){
					try{
						if(new NBTItem(item).getCompound("CustomAttributes").getString("ID").equals("WIZARD_BLADE")) new NBTItem(item).getCompound("CustomAttributes").setInteger("CHARGE", 0);
					}catch(NullPointerException ignored){}
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
