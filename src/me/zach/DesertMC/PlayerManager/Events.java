package me.zach.DesertMC.PlayerManager;


import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.GameMechanics.ClassEvents.CorrupterEvents.EventsForCorruptor;
import me.zach.DesertMC.GameMechanics.ClassEvents.WizardEvents.EventsForWizard;
import me.zach.DesertMC.ScoreboardManager.FScoreboardManager;
import me.zach.DesertMC.Utils.Config.ConfigUtils;
import me.zach.DesertMC.Utils.NBTUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
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
			arrowArray.put((Arrow) event.getEntity(),((Player)event.getEntity().getShooter()).getInventory().getItemInMainHand());
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



	FileConfiguration economyConfig = Bukkit.getPluginManager().getPlugin("Econo").getConfig();
	@EventHandler
	public void onKill(EntityDamageByEntityEvent event) throws Exception {

		if(event.getCause().equals(EntityDamageEvent.DamageCause.FALL)){
			event.setCancelled(true);
		}


	    if(event.isCancelled()) return;


	    if(DesertMain.ct1players.contains(event.getDamager().getUniqueId())){
			event.setDamage(event.getDamage() * 1.1);
		}
		EventsForCorruptor.INSTANCE.t1Event(event);

		EventsForWizard.INSTANCE.wizardt4(event);
		EventsForWizard.INSTANCE.wizardt1(event);
		EventsForWizard.INSTANCE.wizardt8(event);

		EventsForCorruptor.INSTANCE.volcanicSword(event);
		executeKill(event);

		if(event.getDamager() instanceof Player){
            Player killer = (Player) event.getDamager();

            if(NBTUtil.INSTANCE.getCustomAttr(killer.getInventory().getItemInMainHand(), "ID").equals("MAGIC_WAND")){
                EventsForWizard.INSTANCE.magicWandHit((Player)event.getEntity(),(Player)event.getDamager());

            }

        }

	}



	private void executeKill(EntityDamageByEntityEvent event) throws Exception {
		if(event.getEntity() instanceof Player && !(event.getDamager() instanceof Arrow)) {
			if(event.getDamager() instanceof Player){
				try {
					Player player = (Player) event.getEntity();
					Player killer = (Player) event.getDamager();
					if(player.getHealth() - event.getDamage() < 0.1) {
						Location spawn = (Location) main.getConfig().get("server.lobbyspawn");
						player.setHealth(player.getMaxHealth());

						player.teleport(spawn);
						event.setCancelled(true);
						double random = (Math.random() * 5) + 1;

						int soulsgained;
						if(random < 2){
							soulsgained = 1;
							if(main.getConfig().get("players." + killer.getUniqueId() + ".souls") != null){
								main.getConfig().set("players." + killer.getUniqueId() + ".souls", main.getConfig().getInt("player." + killer.getUniqueId() + ".souls") + 1);
							}else{
								main.getConfig().set("players." + killer.getUniqueId() + ".souls", 1);
							}
						}else{
							soulsgained = 0;
						}
						int xpgained = (ConfigUtils.getLevel(ConfigUtils.findClass(player),player) * 10) + 5 + ks.get(player.getUniqueId()) * 5;

						int gemsgained = (ConfigUtils.getLevel(ConfigUtils.findClass(player),player) * 15) + ks.get(player.getUniqueId()) * 3;

						event.getDamager().sendMessage(ChatColor.GREEN + "You killed " + ChatColor.YELLOW + player.getName() + ChatColor.DARK_GRAY + " (" + ChatColor.DARK_GRAY + "+" + ChatColor.BLUE + xpgained + " EXP" + ChatColor.DARK_GRAY + ", +" + ChatColor.GREEN + gemsgained + " Gems" + ChatColor.DARK_GRAY + ", +" + ChatColor.LIGHT_PURPLE + soulsgained + " Souls" + ChatColor.DARK_GRAY + ")");
						if(!ks.containsKey(event.getDamager().getUniqueId())) {
							ks.put(event.getDamager().getUniqueId(), 1);
						}else {
							ks.put(event.getDamager().getUniqueId(), ks.get(event.getDamager().getUniqueId()) + 1);
						}
						ks.put(event.getEntity().getUniqueId(), 0);












						player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BASS, 1, 0.5f);
						((Player) event.getDamager()).playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 4);
						economyConfig.set("players." + killer.getUniqueId() + ".balance", economyConfig.getInt("players." + killer.getUniqueId() + ".balance") + gemsgained);
						ConfigUtils.addXP(killer,ConfigUtils.findClass(killer), xpgained);
						main.saveConfig();
					}

					// Some more events that execute on hit.








				} catch (Exception e) {
					e.printStackTrace();
				}
			}



		}else if(event.getDamager() instanceof Arrow){


			Arrow arrow = (Arrow) event.getDamager();

			if(arrow.getShooter() instanceof Player && event.getEntity() instanceof Player){
//				ItemStack itemUsed = arrowArray.get(arrow);
				Player killer = (Player) arrow.getShooter();
				Player player = (Player) event.getEntity();
//				NBTItem nbti = new NBTItem(itemUsed);
//				if(nbti.getInteger("DAMAGE") != 0){
//					event.setDamage(nbti.getDouble("DAMAGE"));
//				}
				if(player.getHealth() - event.getDamage() < 0.1) {
					Location spawn = (Location) main.getConfig().get("server.lobbyspawn");
					player.setHealth(player.getMaxHealth());

					player.teleport(spawn);
					event.setCancelled(true);
					double random = (Math.random() * 5) + 1;

					int soulsgained;
					if(random < 2){
						soulsgained = 1;
						if(main.getConfig().get("players." + killer.getUniqueId() + ".souls") != null){
							main.getConfig().set("player." + killer.getUniqueId() + ".souls", main.getConfig().getInt("player." + killer.getUniqueId() + ".souls") + 1);
						}else{
							main.getConfig().set("player." + killer.getUniqueId() + ".souls", 1);
						}
					}else{
						soulsgained = 0;
					}
					DesertMain.getInstance.getConfig().set("players." + killer.getUniqueId() + ".souls", DesertMain.getInstance.getConfig().getInt("players." + killer.getUniqueId() + ".souls") + soulsgained);
					int xpgained = (ConfigUtils.getLevel(ConfigUtils.findClass(player),player) * 10) + 5 + ks.get(player.getUniqueId()) * 5;

					int gemsgained = (ConfigUtils.getLevel(ConfigUtils.findClass(player),player) * 15) + ks.get(player.getUniqueId()) * 3;

					killer.sendMessage(ChatColor.GREEN + "You killed " + ChatColor.YELLOW + player.getName() + ChatColor.DARK_GRAY + " (" + ChatColor.DARK_GRAY + "+" + ChatColor.BLUE + xpgained + " EXP" + ChatColor.DARK_GRAY + ", +" + ChatColor.GREEN + gemsgained + " Gems" + ChatColor.DARK_GRAY + ", +" + ChatColor.LIGHT_PURPLE + soulsgained + " Souls" + ChatColor.DARK_GRAY + ")");
					if(!ks.containsKey(event.getDamager().getUniqueId())) {
						ks.put(event.getDamager().getUniqueId(), 1);
					}else {
						ks.put(event.getDamager().getUniqueId(), ks.get(event.getDamager().getUniqueId()) + 1);
					}
					ks.put(event.getEntity().getUniqueId(), 0);
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BASS, 1, 0.5f);
					killer.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 4);
					economyConfig.set("players." + killer.getUniqueId() + ".balance", economyConfig.getInt("players." + killer.getUniqueId() + ".balance") + gemsgained);
					ConfigUtils.addXP(killer,ConfigUtils.findClass(killer), xpgained);
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
			
			
			//save
			main.saveConfig();
			e.setJoinMessage(e.getPlayer().getName() + ChatColor.GOLD + " just joined, give them a warm welcome!");
		} else {
			e.setJoinMessage("");
		}
		
	}

}
