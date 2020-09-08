package me.zach.DesertMC;

import me.zach.DesertMC.CommandsPackage.ItemCommand;
import me.zach.DesertMC.GUImanager.InvEvents;
import me.zach.DesertMC.GameMechanics.ClassEvents.PlayerManager.Events;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;


public class DesertMain extends JavaPlugin implements Listener {
	public static DesertMain getInstance;
	public static HashMap<UUID,Boolean> crouchers = new HashMap<>();
	public static ArrayList<UUID> ct1players = new ArrayList<UUID>();
	public static HashMap<UUID,UUID> lastdmgers = new HashMap<UUID, UUID>();
	public static ArrayList<Player> laststandcd = new ArrayList<>();
	public static ArrayList<Player> mwcd = new ArrayList<>();

	@Override
	public void onEnable() {
// TODO Color Char (for later access): §

		getInstance = this;
		String[] cmdsfile = {"enchantmentmod","setks", "resetclass","debug", "speed", "invincible", "setspawn", "kot", "classexp", "item", "hideplayer", "showplayer"};
		registerCommands(cmdsfile,new Commands());
		registerEvents(this);



		getCommand("item").setExecutor(new ItemCommand());




		loadConfig();
		Events.check(this);
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



	private void loadConfig() {
		getConfig().options().copyDefaults(true);
		saveConfig();
	}
	
	
}
