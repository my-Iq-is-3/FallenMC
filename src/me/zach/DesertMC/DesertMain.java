package me.zach.DesertMC;

import me.zach.DesertMC.CommandsPackage.ItemCommand;
import me.zach.DesertMC.GUImanager.InvEvents;
import me.zach.DesertMC.PlayerManager.Events;
import me.zach.DesertMC.Utils.Config.ConfigUtils;
import me.zach.DesertMC.mythicalitems.Mythical;
import me.zach.DesertMC.mythicalitems.items.DestroyerItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
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

	public static ArrayList<UUID> ct1players = new ArrayList<UUID>();
	public static HashMap<UUID,UUID> lastdmgers = new HashMap<UUID, UUID>();
	public static ArrayList<Player> laststandcd = new ArrayList<>();
	public static ArrayList<Player> mwcd = new ArrayList<>();

	@Override
	public void onEnable() {
//		Color Char (for later access): §

		getInstance = this;
		String[] cmdsfile = {"enchantmentmod","setks", "resetclass","debug", "speed", "invincible", "setspawn", "kot", "classexp", "item", "hideplayer", "showplayer"};
		registerCommands(cmdsfile,new Commands());
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


	private void registerEvents(Plugin p){
		Bukkit.getPluginManager().registerEvents(new Events(), p);
		Bukkit.getPluginManager().registerEvents(new InvEvents(), p);
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
							p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 40, 0, true,false));
						}
						if(locbefore.getBlock().getType().equals(Material.LAVA)){
							if(ConfigUtils.findClass(p).equals("corrupter") && ConfigUtils.getLevel("corrupter",p) > 7){
								p.setHealth(p.getHealth() + 0.5);
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
