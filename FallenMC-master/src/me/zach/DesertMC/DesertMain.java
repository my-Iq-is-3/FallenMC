package me.zach.DesertMC;

import me.zach.DesertMC.CommandsPackage.ItemCommand;
import me.zach.DesertMC.GUImanager.GUIUtils.AnimationConfigManager;
import me.zach.DesertMC.GUImanager.GUIUtils.AnimationEvents;
import me.zach.DesertMC.GUImanager.InvEvents;
import me.zach.DesertMC.GameMechanics.ClassEvents.PlayerManager.Events;
import me.zach.DesertMC.Utils.Config.ConfigUtils;
import me.zach.DesertMC.Utils.Particle.ParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;


public class DesertMain extends JavaPlugin implements Listener {
	public static DesertMain getInstance;
	//private AnimationEvents aniInst = new AnimationEvents();
	public static ArrayList<UUID> ct1players = new ArrayList<UUID>();
	public static HashMap<UUID,UUID> lastdmgers = new HashMap<UUID, UUID>();
	public static ArrayList<Player> laststandcd = new ArrayList<>();
	public static ArrayList<Player> mwcd = new ArrayList<>();
	private AnimationConfigManager aniCfgM;

	@Override
	public void onEnable() {
// TODO Color Char (for later access): §

		getInstance = this;


		String[] cmdsfile = {"enchantmentmod","setks", "resetclass","debug", "speed", "invincible", "setspawn", "kot", "classexp", "item", "hideplayer", "showplayer"};
		registerCommands(cmdsfile, new Commands());
		//getCommand("newanimation").setExecutor(aniInst);
		registerEvents(this);



		getCommand("item").setExecutor(new ItemCommand());
        getCommand("item").setTabCompleter((commandSender, command, s, strings) -> {
            List<String> args = new ArrayList<String>();
            if (strings.length == 1) {

                if (commandSender.hasPermission("admin") && command.getName().equalsIgnoreCase("item")) {
                    args = Arrays.asList("ScoutGoggles", "MagicWand", "VolcanicSword", "Mythical", "Dagger");
                }
            }
            return args;
        });




		loadConfig();
		check();
	}
/*
	public void loadAnimationManager(){
		aniCfgM = new AnimationConfigManager();
		aniCfgM.setup();
		aniCfgM.saveAnimations();
		aniCfgM.reloadAnimations();
	}


	public void saveAnimations() {
		aniCfgM.saveAnimations();
	}

	public FileConfiguration getAnimations() {
		return aniCfgM.getAnimations();
	}

 */


	private void registerEvents(Plugin p){
		Bukkit.getPluginManager().registerEvents(new Events(), p);
		Bukkit.getPluginManager().registerEvents(new InvEvents(), p);
		//Bukkit.getPluginManager().registerEvents(aniInst, p);
	}

	private void registerCommands(String[] commands, CommandExecutor file){
		for(String s : commands){
			getCommand(s).setExecutor(file);
		}
	}



	private void check(){
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

						if(location.getBlock().getType().equals(Material.LAVA) || location.getBlock().getType().equals(Material.STATIONARY_LAVA)){
							if(ConfigUtils.findClass(p).equals("corrupter") && ConfigUtils.getLevel("corrupter",p) > 7){
								if(p.getHealth() + 0.5 <= p.getMaxHealth()){
									p.setHealth(p.getHealth() + 0.5);
								}else if(p.getHealth() + 0.5  > p.getMaxHealth()){
									p.setHealth(p.getMaxHealth());
								}
								ParticleEffect.VILLAGER_HAPPY.display(0.2f,0.2f,0.2f,0,10,location,10);
							}
						}
					}
			}
		}.runTaskTimer(this,0,20);
	}


	private void loadConfig() {
		getConfig().options().copyDefaults(true);
		saveConfig();
	}
	
	
}
