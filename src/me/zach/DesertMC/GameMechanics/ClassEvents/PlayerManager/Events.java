package me.zach.DesertMC.GameMechanics.ClassEvents.PlayerManager;


import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.GameMechanics.ClassEvents.CorrupterEvents.EventsForCorruptor;
import me.zach.DesertMC.GameMechanics.ClassEvents.ScoutEvents.EventsForScout;
import me.zach.DesertMC.GameMechanics.ClassEvents.TankEvents.EventsForTank;
import me.zach.DesertMC.GameMechanics.ClassEvents.WizardEvents.EventsForWizard;
import me.zach.DesertMC.Prefix;
import me.zach.DesertMC.ScoreboardManager.FScoreboardManager;
import me.zach.DesertMC.Utils.Config.ConfigUtils;
import me.zach.DesertMC.Utils.Particle.ParticleEffect;
import me.zach.DesertMC.Utils.TitleUtils;
import me.zach.DesertMC.Utils.nbt.NBTUtil;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

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
					if(location.subtract(0,10,0).getBlock().getType().equals(Material.DIAMOND_BLOCK) && ConfigUtils.getLevel("wizard",p) > 5 && ConfigUtils.findClass(p).equals("wizard")){
						p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0, true,false));
					}else if(location.subtract(0,10,0).getBlock().getType().equals(Material.EMERALD_BLOCK) && ConfigUtils.getLevel("tank",p) > 4 && ConfigUtils.findClass(p).equals("tank")){
						p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0, true,false));
					}else if(location.subtract(0,10,0).getBlock().getType().equals(Material.IRON_BLOCK) && ConfigUtils.getLevel("scout",p) > 5 && ConfigUtils.findClass(p).equals("scout")){
						p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0, true,false));
					}else if(location.subtract(0,10,0).getBlock().getType().equals(Material.GOLD_BLOCK) && ConfigUtils.getLevel("corrupter",p) > 5 && ConfigUtils.findClass(p).equals("corrupter")){
						p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0, true,false));
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

				new BukkitRunnable(){
					@Override
					public void run() {
						player.setFireTicks(0);
					}
				}.runTaskLater(DesertMain.getInstance,10);

				event.setCancelled(true);
				double random = (Math.random() * 5) + 1;

				int soulsgained;
				if (random < 2) {
					soulsgained = 1;
					if (main.getConfig().get("players." + killer.getUniqueId() + ".souls") != null) {
						main.getConfig().set("player." + killer.getUniqueId() + ".souls", main.getConfig().getInt("player." + killer.getUniqueId() + ".souls") + 1);
					} else {
						main.getConfig().set("player." + killer.getUniqueId() + ".souls", 1);
					}
				} else {
					soulsgained = 0;
				}
				DesertMain.getInstance.getConfig().set("players." + killer.getUniqueId() + ".souls", DesertMain.getInstance.getConfig().getInt("players." + killer.getUniqueId() + ".souls") + soulsgained);
				int xpgained = (ConfigUtils.getLevel(ConfigUtils.findClass(player), player) * 10) + 5 + ks.get(player.getUniqueId()) * 5;

				int gemsgained = (ConfigUtils.getLevel(ConfigUtils.findClass(player), player) * 15) + ks.get(player.getUniqueId()) * 3;
				if (!ks.containsKey(killer.getUniqueId())) {
					ks.put(killer.getUniqueId(), 1);
				} else {
					ks.put(killer.getUniqueId(), ks.get(killer.getUniqueId()) + 1);
				}
				killer.sendMessage(ChatColor.GREEN + "You killed " + ChatColor.YELLOW + player.getName() + ChatColor.DARK_GRAY + " (" + ChatColor.DARK_GRAY + "+" + ChatColor.BLUE + xpgained + " EXP" + ChatColor.DARK_GRAY + ", +" + ChatColor.GREEN + gemsgained + " Gems" + ChatColor.DARK_GRAY + ", +" + ChatColor.LIGHT_PURPLE + soulsgained + " Souls" + ChatColor.DARK_GRAY + ")");
				ks.put(event.getEntity().getUniqueId(), 0);
				player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 0.5f);
				killer.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 4);
				economyConfig.set("players." + killer.getUniqueId() + ".balance", economyConfig.getInt("players." + killer.getUniqueId() + ".balance") + gemsgained);
				ConfigUtils.addXP(killer, ConfigUtils.findClass(killer), xpgained);
			}
		}

	}

	private void executeKill(EntityDamageByEntityEvent event) throws Exception {
		if (event.getEntity() instanceof Player && !(event.getDamager() instanceof Arrow)) {
			if (event.getDamager() instanceof Player) {
				try {
					Player player = (Player) event.getEntity();
					Player killer = (Player) event.getDamager();
					if (player.getHealth() - event.getDamage() < 0.1) {
						Location spawn = (Location) main.getConfig().get("server.lobbyspawn");
						player.setHealth(player.getMaxHealth());

						player.teleport(spawn);
						if(player.getFireTicks() > 0)
							player.setFireTicks(0);
						event.setCancelled(true);
						double random = (Math.random() * 5) + 1;
						int soulsgained;
						if (random < 2) {
							soulsgained = 1;
							if (main.getConfig().get("players." + killer.getUniqueId() + ".souls") != null) {
								main.getConfig().set("players." + killer.getUniqueId() + ".souls", main.getConfig().getInt("players." + killer.getUniqueId() + ".souls") + 1);
							} else {
								main.getConfig().set("players." + killer.getUniqueId() + ".souls", 1);
							}
						} else {
							soulsgained = 0;
						}
						int xpgained = (ConfigUtils.getLevel(ConfigUtils.findClass(player), player) * 10) + 5 + ks.get(player.getUniqueId()) * 5;

						int gemsgained = (ConfigUtils.getLevel(ConfigUtils.findClass(player), player) * 15) + ks.get(player.getUniqueId()) * 3;

						event.getDamager().sendMessage(ChatColor.GREEN + "You killed " + ChatColor.YELLOW + player.getName() + ChatColor.DARK_GRAY + " (" + ChatColor.DARK_GRAY + "+" + ChatColor.BLUE + xpgained + " EXP" + ChatColor.DARK_GRAY + ", +" + ChatColor.GREEN + gemsgained + " Gems" + ChatColor.DARK_GRAY + ", +" + ChatColor.LIGHT_PURPLE + soulsgained + " Souls" + ChatColor.DARK_GRAY + ")");
						if (!ks.containsKey(event.getDamager().getUniqueId())) {
							ks.put(event.getDamager().getUniqueId(), 1);
						} else {
							ks.put(event.getDamager().getUniqueId(), ks.get(event.getDamager().getUniqueId()) + 1);
						}
						ks.put(event.getEntity().getUniqueId(), 0);
						player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 0.5f);
						((Player) event.getDamager()).playSound(player.getLocation(), Sound.LEVEL_UP, 1, 4);
						economyConfig.set("players." + killer.getUniqueId() + ".balance", economyConfig.getInt("players." + killer.getUniqueId() + ".balance") + gemsgained);
						ConfigUtils.addXP(killer, ConfigUtils.findClass(killer), xpgained);
						main.saveConfig();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}


		} else if (event.getDamager() instanceof Arrow) {


			Arrow arrow = (Arrow) event.getDamager();

			if (arrow.getShooter() instanceof Player && event.getEntity() instanceof Player) {
//				ItemStack itemUsed = arrowArray.get(arrow);
				Player killer = (Player) arrow.getShooter();
				Player player = (Player) event.getEntity();
//				NBTItem nbti = new NBTItem(itemUsed);
//				if(nbti.getInteger("DAMAGE") != 0){
//					event.setDamage(nbti.getDouble("DAMAGE"));
//				}
				if (player.getHealth() - event.getDamage() < 0.1) {
					Location spawn = (Location) main.getConfig().get("server.lobbyspawn");
					player.setHealth(player.getMaxHealth());
					if(player.getFireTicks() > 0)
						player.setFireTicks(0);
					player.teleport(spawn);
					event.setCancelled(true);
					double random = (Math.random() * 5) + 1;

					int soulsgained;
					if (random < 2) {
						soulsgained = 1;
						if (main.getConfig().get("players." + killer.getUniqueId() + ".souls") != null) {
							main.getConfig().set("player." + killer.getUniqueId() + ".souls", main.getConfig().getInt("player." + killer.getUniqueId() + ".souls") + 1);
						} else {
							main.getConfig().set("player." + killer.getUniqueId() + ".souls", 1);
						}
					} else {
						soulsgained = 0;
					}
					DesertMain.getInstance.getConfig().set("players." + killer.getUniqueId() + ".souls", DesertMain.getInstance.getConfig().getInt("players." + killer.getUniqueId() + ".souls") + soulsgained);
					int xpgained = (ConfigUtils.getLevel(ConfigUtils.findClass(player), player) * 10) + 5 + ks.get(player.getUniqueId()) * 5;

					int gemsgained = (ConfigUtils.getLevel(ConfigUtils.findClass(player), player) * 15) + ks.get(player.getUniqueId()) * 3;

					killer.sendMessage(ChatColor.GREEN + "You killed " + ChatColor.YELLOW + player.getName() + ChatColor.DARK_GRAY + " (" + ChatColor.DARK_GRAY + "+" + ChatColor.BLUE + xpgained + " EXP" + ChatColor.DARK_GRAY + ", +" + ChatColor.GREEN + gemsgained + " Gems" + ChatColor.DARK_GRAY + ", +" + ChatColor.LIGHT_PURPLE + soulsgained + " Souls" + ChatColor.DARK_GRAY + ")");
					if (!ks.containsKey(event.getDamager().getUniqueId())) {
						ks.put(event.getDamager().getUniqueId(), 1);
					} else {
						ks.put(event.getDamager().getUniqueId(), ks.get(event.getDamager().getUniqueId()) + 1);
					}
					ks.put(event.getEntity().getUniqueId(), 0);
					player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 0.5f);
					killer.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 4);
					economyConfig.set("players." + killer.getUniqueId() + ".balance", economyConfig.getInt("players." + killer.getUniqueId() + ".balance") + gemsgained);
					ConfigUtils.addXP(killer, ConfigUtils.findClass(killer), xpgained);
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
			/* Passives */
			//speed
			main.getConfig().set("players." + e.getPlayer().getUniqueId() + ".passive.speed.level", 0);
			main.getConfig().set("players." + e.getPlayer().getUniqueId() + ".passive.speed.xptonext", 50);
			//health
			main.getConfig().set("players." + e.getPlayer().getUniqueId() + ".passive.health.level", 0);
			main.getConfig().set("players." + e.getPlayer().getUniqueId() + ".passive.health.xptonext", 50);
			//damage
			main.getConfig().set("players." + e.getPlayer().getUniqueId() + ".passive.damage.xptonext", 50);
			main.getConfig().set("players." + e.getPlayer().getUniqueId() + ".passive.damage.level", 0);
			
			//init titles
			TitleUtils.initializeTitles(e.getPlayer());
			//save
			main.saveConfig();
			e.setJoinMessage(e.getPlayer().getName() + ChatColor.GOLD + " just joined for the first time, give them a warm welcome!");
		} else {
			e.setJoinMessage("");
		}


		
	}



}