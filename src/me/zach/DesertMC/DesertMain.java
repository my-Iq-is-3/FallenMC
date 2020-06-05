package me.zach.DesertMC;

import com.avaje.ebean.EbeanServer;
import me.zach.DesertMC.CommandsPackage.ItemCommand;
import me.zach.DesertMC.Utils.Config.ConfigUtils;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;

import me.zach.DesertMC.GUImanager.InvEvents;
import me.zach.DesertMC.PlayerManager.Events;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class DesertMain extends JavaPlugin implements Listener {
	public static DesertMain getInstance;

	public static ArrayList<Player> laststandcd = new ArrayList<>();
	public static ArrayList<Player> mwcd = new ArrayList<>();

	@Override
	public void onEnable() {

			getInstance = this;

			String[] cmdsfile = {"setks", "resetclass","debug", "speed", "invincible", "setspawn", "kot", "classexp", "item", "hideplayer", "showplayer"};
			registerCommands(cmdsfile,new Commands());
			registerEvents(this);

			
			getCommand("item").setExecutor(new ItemCommand());
			getCommand("item").setTabCompleter(new TabCompleter() {
				@Override
				public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
					List<String> args = new ArrayList<String>();
					if(strings.length == 1) {

						if (commandSender.hasPermission("admin") && command.getName().equalsIgnoreCase("item")) {
							args.add("ScoutGoggles");
							args.add("MagicWand");
						}
					}
					return args;
				}
			});



			loadConfig();
			checkForBiome();
	}



	public void forTest(){

	}

	private void registerEvents(Plugin p){
		Bukkit.getPluginManager().registerEvents(new Events(), p);
		Bukkit.getPluginManager().registerEvents(new InvEvents(), p);
	}

	private void registerCommands(String[] commands,CommandExecutor file){
		for(String s : commands){
			getCommand(s).setExecutor(file);
		}
	}

	private void checkForBiome(){
		new BukkitRunnable(){

			@Override
			public void run() {
				for(World w : getServer().getWorlds()){
					for(Player p : w.getPlayers()){

						Location location = p.getLocation();
						if(location.subtract(0,10,0).getBlock().getType().equals(Material.DIAMOND_BLOCK) && ConfigUtils.INSTANCE.getLevel("wizard",p) > 5 && ConfigUtils.INSTANCE.findClass(p).equals("wizard")){
							p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0, true,false));
						}else if(location.subtract(0,10,0).getBlock().getType().equals(Material.EMERALD_BLOCK) && ConfigUtils.INSTANCE.getLevel("tank",p) > 4 && ConfigUtils.INSTANCE.findClass(p).equals("tank")){
							p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0, true,false));
						}else if(location.subtract(0,10,0).getBlock().getType().equals(Material.IRON_BLOCK) && ConfigUtils.INSTANCE.getLevel("scout",p) > 5 && ConfigUtils.INSTANCE.findClass(p).equals("scout")){
							p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0, true,false));
						}else if(location.subtract(0,10,0).getBlock().getType().equals(Material.GOLD_BLOCK) && ConfigUtils.INSTANCE.getLevel("corrupter",p) > 5 && ConfigUtils.INSTANCE.findClass(p).equals("corrupter")){
							p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0, true,false));
						}
					}
				}
			}
		}.runTaskTimer(this,0,60);
	}


	private void loadConfig() {
		getConfig().options().copyDefaults(true);
		saveConfig();
	}
	
	
}
