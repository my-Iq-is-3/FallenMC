package me.zach.DesertMC;

import me.zach.DesertMC.ClassManager.CoruManager.EventsForCorruptor;
import me.zach.DesertMC.ClassManager.ScoutManager.EventsForScout;
import me.zach.DesertMC.ClassManager.TankManager.EventsForTank;
import me.zach.DesertMC.ClassManager.TravellerEvents;
import me.zach.DesertMC.CommandsPackage.Commands;
import me.zach.DesertMC.CommandsPackage.ItemCommand;
import me.zach.DesertMC.ClassManager.InvEvents;
import me.zach.DesertMC.GameMechanics.EXPMilesstones.MilestonesEvents;
import me.zach.DesertMC.GameMechanics.EXPMilesstones.MilestonesInventory;
import me.zach.DesertMC.GameMechanics.EXPMilesstones.MilestonesOverride;
import me.zach.DesertMC.GameMechanics.EXPMilesstones.MilestonesUtil;
import me.zach.DesertMC.GameMechanics.Events;
import me.zach.DesertMC.ClassManager.WizardManager.EventsForWizard;
import me.zach.DesertMC.GameMechanics.SPolice;
import me.zach.DesertMC.GameMechanics.SoulShop;
import me.zach.DesertMC.Utils.RankUtils.RankEvents;
import me.zach.DesertMC.Utils.gui.GUIManager;
import net.jitse.npclib.NPCLib;
import org.bukkit.Bukkit;
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
	public static final Set<UUID> crouchers = new HashSet<>();
	public static final Set<UUID> ct1players = new HashSet<UUID>();
	public static final HashMap<UUID,UUID> lastdmgers = new HashMap<UUID, UUID>();
	public static final Set<Player> laststandcd = new HashSet<>();
	public static final Set<Player> mwcd = new HashSet<>();
	public static final HashMap<UUID,List<UUID>> alertEnchantment	= new HashMap<>();
	public static final Set<UUID> slowed = new HashSet<>();
	public static final Set<UUID> scoutBladeCD = new HashSet<>();
	public static final HashMap<UUID, Block> stomperStage = new HashMap<>();
	public static final Set<UUID> stomperCD = new HashSet<>();
	public static final HashMap<UUID, String> snack = new HashMap<>();
	public static final Set<UUID> eating = new HashSet<>();
	public static final HashMap<UUID, HashMap<String, Double>> weightQueue = new HashMap<>();
	public static final HashMap<UUID, Float> booster = new HashMap<>();
	public static final Set<UUID> blockNotifs = new HashSet<>();
	private static NPCLib library;
	public static int lv = 8;
	public static int xpToNext = 100;
	public static int currentProgress = 0;
	public static int resets = 0;
	public static Set<UUID> claiming = new HashSet<>();
	public static final ArrayList<Integer> unclaimed = new ArrayList<>();
	static{
		unclaimed.add(1);
		unclaimed.add(7);
	}
	//How to generate a random long: (long) (Math.random() * (rightLimit - leftLimit));
	@Override
	public void onEnable() {
		Bukkit.getConsoleSender().sendMessage("1");
		library = new NPCLib(this);
		getInstance = this;
		String[] cmdsfile = {"enchantmentmod","setks", "resetclass","debug", "speed", "invincible", "setspawn", "kot", "classexp", "item", "hideplayer", "showplayer", "selecttitle", "spawnnpc", "seizehelditem", "addweight", "expmilestones", "rank", "colors", "confirmreset", "cosmetic", "blocknotifications"};
		registerCommands(cmdsfile,new Commands());
		registerEvents(this);
		getCommand("item").setExecutor(new ItemCommand());
		getCommand("confirmreset").setExecutor(new MilestonesUtil());
		loadConfig();
		MilestonesOverride.addOverrides();
		Events.check(this);
		Bukkit.getConsoleSender().sendMessage("1.1");
	}


	private void registerEvents(Plugin p){
		Bukkit.getConsoleSender().sendMessage("2");
		Bukkit.getPluginManager().registerEvents(new Events(), p);
		Bukkit.getPluginManager().registerEvents(new RankEvents(this), p);
		Bukkit.getPluginManager().registerEvents(new InvEvents(), p);
		Bukkit.getPluginManager().registerEvents(EventsForWizard.INSTANCE, this);
		Bukkit.getPluginManager().registerEvents(EventsForCorruptor.INSTANCE, this);
		Bukkit.getPluginManager().registerEvents(EventsForTank.getInstance(), this);
		Bukkit.getPluginManager().registerEvents(EventsForScout.getInstance(), this);
		Bukkit.getPluginManager().registerEvents(SPolice.INSTANCE, this);
		Bukkit.getPluginManager().registerEvents(SoulShop.INSTANCE, this);
		Bukkit.getPluginManager().registerEvents(new MilestonesEvents(), this);
		Bukkit.getPluginManager().registerEvents(new TravellerEvents(), this);
		Bukkit.getPluginManager().registerEvents(new GUIManager(), this);
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
	public static NPCLib getNPCLib(){
		return library;
	}
	
	
}
