package me.zach.DesertMC;

import me.zach.DesertMC.ClassManager.CoruManager.EventsForCorruptor;
import me.zach.DesertMC.ClassManager.ScoutManager.EventsForScout;
import me.zach.DesertMC.ClassManager.TankManager.EventsForTank;
import me.zach.DesertMC.CommandsPackage.Commands;
import me.zach.DesertMC.CommandsPackage.ItemCommand;
import me.zach.DesertMC.ClassManager.InvEvents;
import me.zach.DesertMC.GameMechanics.Events;
import me.zach.DesertMC.ClassManager.WizardManager.EventsForWizard;
import me.zach.DesertMC.Utils.RankUtils.RankEvents;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.ArrayList;

import java.util.*;


public class DesertMain extends JavaPlugin implements Listener {
	public static DesertMain getInstance;
	public static HashMap<UUID,Boolean> crouchers = new HashMap<>();
	public static ArrayList<UUID> ct1players = new ArrayList<UUID>();
	public static HashMap<UUID,UUID> lastdmgers = new HashMap<UUID, UUID>();
	public static ArrayList<Player> laststandcd = new ArrayList<>();
	public static ArrayList<Player> mwcd = new ArrayList<>();
	public static HashMap<UUID,List<UUID>> alertEnchantment	= new HashMap<>();
	public static List<UUID> slowed = new ArrayList<>();
	public static ArrayList<UUID> scoutBladeCD = new ArrayList<>();
	public static HashMap<UUID, Block> stomperStage = new HashMap<>();
	public static ArrayList<UUID> stomperCD = new ArrayList<>();
	public static HashMap<UUID, String> snack = new HashMap<>();
	@Override
	public void onEnable() {
// TODO Color Char (for later access): §

		getInstance = this;
		String[] cmdsfile = {"enchantmentmod","setks", "resetclass","debug", "speed", "invincible", "setspawn", "kot", "classexp", "item", "hideplayer", "showplayer", "selecttitle"};
		registerCommands(cmdsfile,new Commands());
		registerEvents(this);
		getCommand("item").setExecutor(new ItemCommand());
		loadConfig();
		Events.check(this);
	}


	private void registerEvents(Plugin p){
		Bukkit.getPluginManager().registerEvents(new Events(), p);
		Bukkit.getPluginManager().registerEvents(new RankEvents(this), p);
		Bukkit.getPluginManager().registerEvents(new InvEvents(), p);
		Bukkit.getPluginManager().registerEvents(EventsForWizard.INSTANCE, this);
		Bukkit.getPluginManager().registerEvents(EventsForCorruptor.INSTANCE, this);
		Bukkit.getPluginManager().registerEvents(EventsForTank.getInstance(), this);
		Bukkit.getPluginManager().registerEvents(EventsForScout.getInstance(), this);
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
