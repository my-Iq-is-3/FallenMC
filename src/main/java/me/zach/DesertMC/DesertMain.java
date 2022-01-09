package me.zach.DesertMC;

import me.zach.DesertMC.ClassManager.CoruManager.EventsForCorruptor;
import me.zach.DesertMC.ClassManager.ScoutManager.EventsForScout;
import me.zach.DesertMC.ClassManager.TankManager.EventsForTank;
import me.zach.DesertMC.ClassManager.TravellerEvents;
import me.zach.DesertMC.CommandsPackage.Commands;
import me.zach.DesertMC.CommandsPackage.ItemCommand;
import me.zach.DesertMC.ClassManager.InvEvents;
import me.zach.DesertMC.GameMechanics.EXPMilesstones.MilestonesEvents;
import me.zach.DesertMC.GameMechanics.EXPMilesstones.MilestonesOverride;
import me.zach.DesertMC.GameMechanics.EXPMilesstones.MilestonesUtil;
import me.zach.DesertMC.GameMechanics.Events;
import me.zach.DesertMC.ClassManager.WizardManager.EventsForWizard;
import me.zach.DesertMC.GameMechanics.NPCStructure.NPCSuper;
import me.zach.DesertMC.GameMechanics.NPCStructure.SavedNPC;
import me.zach.DesertMC.GameMechanics.hitbox.BoxHitbox;
import me.zach.DesertMC.GameMechanics.hitbox.CircleHitbox;
import me.zach.DesertMC.GameMechanics.hitbox.HitboxListener;
import me.zach.DesertMC.GameMechanics.hitbox.HitboxManager;
import me.zach.DesertMC.Utils.MiscUtils;
import me.zach.DesertMC.Utils.RankUtils.RankEvents;
import me.zach.DesertMC.Utils.gui.GUIManager;
import me.zach.DesertMC.holo.HologramEvents;
import net.jitse.npclib.NPCLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.ArrayList;

import java.util.*;


public class DesertMain extends JavaPlugin implements Listener {
	public static DesertMain getInstance;
	public static final Set<UUID> crouchers = new HashSet<>();
	public static final Set<UUID> ct1players = new HashSet<>();
	public static final HashMap<UUID,UUID> lastdmgers = new HashMap<>();
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
	public static Set<UUID> claiming = new HashSet<>();
	public static final ArrayList<Integer> unclaimed = new ArrayList<>();
	public static final String[] NPC_PACKAGES = new String[]{"me.zach.DesertMC.GameMechanics.npcs", "xyz.fallenmc.shops.npcs.clazz"};
	static{
		unclaimed.add(1);
		unclaimed.add(7);
	}

	@Override
	public void onEnable() {
		Bukkit.getConsoleSender().sendMessage("Attempting fallenmc onEnable");
		library = new NPCLib(this);
		getInstance = this;
		ConfigurationSerialization.registerClass(SavedNPC.class);
		ConfigurationSerialization.registerClass(BoxHitbox.class);
		ConfigurationSerialization.registerClass(CircleHitbox.class);
		HitboxManager.loadAll(this);
		String[] cmdsfile = {"hitbox","testench","setks", "resetclass","debug", "speed", "invincible", "setspawn", "kothy", "classexp", "item", "hideplayer", "showplayer", "selecttitle", "spawnnpc", "seizehelditem", "addweight", "expmilestones", "rank", "colors", "confirmreset", "cosmetic", "blocknotifications", "shoptest", "booster"};
		registerCommands(cmdsfile,new Commands());
		registerEvents(this);
		getCommand("item").setExecutor(new ItemCommand());
		PluginCommand command = getCommand("confirmreset");
		command.setExecutor(new MilestonesUtil());
		loadConfig();
		loadNPCs();
		MilestonesOverride.addOverrides();
		Events.check(this);
		Bukkit.getConsoleSender().sendMessage("Fallenmc onEnable success");
	}

	public void onDisable(){
		HitboxManager.saveAll(this);
	}

	private void registerEvents(Plugin p){
		Bukkit.getConsoleSender().sendMessage("registering events...");
		Bukkit.getPluginManager().registerEvents(new Events(), p);
		Bukkit.getPluginManager().registerEvents(new RankEvents(p), p);
		Bukkit.getPluginManager().registerEvents(new InvEvents(), p);
		Bukkit.getPluginManager().registerEvents(EventsForWizard.INSTANCE, p);
		Bukkit.getPluginManager().registerEvents(EventsForCorruptor.INSTANCE, p);
		Bukkit.getPluginManager().registerEvents(EventsForTank.getInstance(), p);
		Bukkit.getPluginManager().registerEvents(EventsForScout.getInstance(), p);
		Bukkit.getPluginManager().registerEvents(new MilestonesEvents(), p);
		Bukkit.getPluginManager().registerEvents(new TravellerEvents(), p);
		Bukkit.getPluginManager().registerEvents(new GUIManager(), p);
		Bukkit.getPluginManager().registerEvents(new HitboxListener(),p);
		Bukkit.getConsoleSender().sendMessage("events registered");
		Bukkit.getPluginManager().registerEvents(new HologramEvents(), this);

	}

	private void loadHitboxes(){
		Bukkit.getConsoleSender().sendMessage("loading hitboxes...");
		HitboxManager.loadAll(this);
		Bukkit.getConsoleSender().sendMessage("loaded hitboxes");
	}

	private void loadNPCs(){
		System.out.println("1");
		List<SavedNPC> npcs = SavedNPC.stored(this);
		System.out.println("loop");
		Bukkit.getConsoleSender().sendMessage("spawning npcs...");
		for(SavedNPC savedNPC : npcs){
			System.out.println("loc");
			Location location = savedNPC.location;
			System.out.println("createnpc");
			savedNPC.npc.createNPC(savedNPC.location);
			Bukkit.getLogger().info("Spawned " + savedNPC.npc.name + " from config at (" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ")");
		}
		Bukkit.getConsoleSender().sendMessage("npcs spawned");
	}

	private void registerCommands(String[] commands, CommandExecutor file){
		Bukkit.getConsoleSender().sendMessage("registering commands...");
		for(String s : commands){
			getCommand(s).setExecutor(file);
		}
		Bukkit.getConsoleSender().sendMessage("commands registered");

	}
	private void loadConfig() {
		getConfig().options().copyDefaults(true);
		saveConfig();
	}
	public static NPCLib getNPCLib(){
		return library;
	}
}
