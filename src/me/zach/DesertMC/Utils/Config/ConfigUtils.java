package me.zach.DesertMC.Utils.Config;

import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.Utils.MiscUtils;
import net.jitse.npclib.NPCLib;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

import static me.zach.DesertMC.DesertMain.*;

public class ConfigUtils {

	private static final DesertMain main = DesertMain.getInstance;
	private static final FileConfiguration config = main.getConfig();

	private ConfigUtils() {

	}


	public static boolean deductSouls(Player player, int amount){
		int souls = config.getInt("players." + player.getUniqueId() + ".souls");
		if(souls >= amount){
			config.set("players." + player.getUniqueId() + ".souls", souls - amount);
			main.saveConfig();
			return true;
		}else return false;
	}

	public static void addGems(Player player, int amount){
		config.set("players." + player.getUniqueId() + ".balance", getGems(player) + amount);
		main.saveConfig();
	}

	public static void addSouls(Player player, int amount){
		config.set("players." + player.getUniqueId() + ".souls", getSouls(player) + amount);
		main.saveConfig();
	}

	public static int getSouls(Player player){
		return config.getInt("players." + player.getUniqueId() + ".souls");
	}

	public static boolean deductGems(Player player, int amount){
		int gems = config.getInt("players." + player.getUniqueId() + ".balance");
		if(gems >= amount){
			config.set("players." + player.getUniqueId() + ".balance", gems - amount);
			main.saveConfig();
			return true;
		}else return false;
	}

	public static int getLevel(String playerclass, Player player) throws NullPointerException {
		if(!config.contains("players." + player.getUniqueId() + ".classes." + playerclass + ".level")) return 0;
		return config.getInt("players." + player.getUniqueId() + ".classes." + playerclass + ".level");
	}
	
	public static int getXpToNext(Player player, String playerclass) throws NullPointerException{
		if(!config.contains("players." + player.getUniqueId() + ".classes." + playerclass + ".xptonext")){
			return 0;
		}
		return config.getInt("players." + player.getUniqueId() + ".classes." + playerclass + ".xptonext");
	}
	
	public static String findClass(Player player) {
		
		if(config.getString("players." + player.getUniqueId() + ".classes.inuse") != null) {
			return config.getString("players." + player.getUniqueId() + ".classes.inuse");
		} else {
			return "none";
		}
		
	}

	public static Object getXP(Player player, String playerclass) throws NullPointerException {


			return config.getInt("players." + player.getUniqueId() + ".classes." + playerclass + ".hasxp");


	}

	public static int getGems(Player p){
		return config.getInt("players." + p.getUniqueId() + ".balance");
	}

	public static void resetclass(Player player, String playerclass){
		// 100 xp to next level
		config.set("players." + player.getUniqueId() + ".classes." + playerclass + ".xptonext", 100);
		config.set("players." + player.getUniqueId() + ".classes." + playerclass + ".hasxp", 0);
		config.set("players." + player.getUniqueId() + ".classes." + playerclass + ".level", 1);
		main.saveConfig();
	}

	private static void cexp(Player player, String classtoaddto, int amount){
		int xptonext = getXpToNext(player, classtoaddto);
		if(xptonext <= amount) {
			int pastlevel = config.getInt("players." + player.getUniqueId() + ".classes." + classtoaddto + ".level");
			if(pastlevel == 10) return;
			switch (pastlevel) {
				case 1:
					config.set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".xptonext", 500);
					config.set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".hasxp", 0);
					break;
				case 2:
					config.set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".xptonext", 1000);
					config.set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".hasxp", 0);
					break;
				case 3:
					config.set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".xptonext", 2500);
					config.set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".hasxp", 0);
					break;
				case 4:
					config.set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".xptonext", 4000);
					config.set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".hasxp", 0);
					break;
				case 5:
					config.set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".xptonext", 7000);
					config.set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".hasxp", 0);
					break;
				case 6:
					config.set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".xptonext", 8500);
					config.set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".hasxp", 0);
					break;
				case 7:
					config.set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".xptonext", 10000);
					config.set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".hasxp", 0);
					break;
				case 8:
					config.set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".xptonext", 20000);
					config.set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".hasxp", 0);
					break;
				case 9:
					config.set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".xptonext", "MAX");
					config.set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".hasxp", "MAX");
					break;
			}
			config.set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".level", getLevel(classtoaddto, player) + 1);
			player.sendTitle(ChatColor.BLUE + "You leveled up!", ChatColor.DARK_GRAY + "You are now level " + ChatColor.AQUA + (pastlevel + 1));
			main.saveConfig();
			cexp(player, classtoaddto, amount - xptonext);
		}else {
			if(!getXP(player,classtoaddto).equals("MAX")) {
				config.set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".hasxp", (Integer)getXP(player, classtoaddto) + amount);
				config.set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".xptonext", getXpToNext(player, classtoaddto) - amount);
			}
			main.saveConfig();
		}
	}

	public static void setClass(Player player, String newclass){
		config.set("players." + player.getUniqueId() + ".classes.inuse", newclass);
	}

	private static void gexp(Player player, int amount){
		if(amount == 0) return;
		if(lv >= 59) return;
		if(DesertMain.xpToNext <= currentProgress + amount){
			player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "EXP MILESTONE!" + ChatColor.GREEN + " You just reached EXP milestone " + lv + "!");
			TextComponent component = new TextComponent(TextComponent.fromLegacyText("Click here to view your milestones progression and claim rewards!"));
			component.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
			component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/expmilestones"));
			player.spigot().sendMessage(component);
			MiscUtils.ootChestFanfare(player);
			lv++;
			int prevProgress = currentProgress;
			int prevNext = xpToNext;
			currentProgress = 0;
			if(lv >= 29) xpToNext = lv * 300;
			else xpToNext = lv * 200;
			if(lv == 59) player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "MILESTONES MAXED!" + ChatColor.GREEN + " Open the EXP Milestones inventory using /expmilestones and reset your milestones to gain a STAR, and a potential cosmetic!");
			unclaimed.add(lv - 1);
			gexp(player, amount - (prevNext - prevProgress));
		}else{
			currentProgress += amount;
		}
	}

	public static void addXP(Player player, String classtoaddto, int amount) {
		int xptonext = getXpToNext(player, classtoaddto);
		if(getLevel(classtoaddto, player) >= 10) {
			return;
		}
		if(booster.containsKey(player.getUniqueId())) amount = Math.round(amount * booster.get(player.getUniqueId()));
		cexp(player, classtoaddto, amount);
		gexp(player, amount);
	}
}
