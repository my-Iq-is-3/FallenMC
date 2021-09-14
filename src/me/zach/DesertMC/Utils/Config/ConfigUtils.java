package me.zach.DesertMC.Utils.Config;

import me.zach.DesertMC.ClassManager.TravellerEvents;
import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.GameMechanics.EXPMilesstones.MilestonesUtil;
import me.zach.DesertMC.Utils.MiscUtils;
import me.zach.DesertMC.Utils.StringUtils.StringUtil;
import me.zach.databank.saver.Key;
import me.zach.databank.saver.PlayerData;
import me.zach.databank.saver.SaveManager;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static me.zach.DesertMC.DesertMain.*;

public class ConfigUtils {

	private static final Plugin main = DesertMain.getInstance;

	private ConfigUtils() {

	}


	public static boolean deductSouls(Player player, int amount){
		PlayerData data = SaveManager.getData(player);
		int souls = data.getSouls();
		if(souls >= amount){
			data.setSouls(souls-amount);
			return true;
		}else return false;
	}

	public static void addGems(Player player,int amount){
		PlayerData data = SaveManager.getData(player);
		data.setGems(data.getGems()+amount);
	}

	public static int getSouls(Player player){

		return SaveManager.getData(player).getSouls();
	}

	public static void addSouls(Player player, int amount){
		PlayerData data = SaveManager.getData(player);
		data.setSouls(data.getSouls()+amount);
	}

	public static boolean deductGems(Player player, int amount){
		PlayerData data = SaveManager.getData(player);
		int gems = data.getGems();
		if(gems >= amount){
			data.setGems(gems - amount);
			return true;
		}else return false;
	}

	public static int getLevel(String playerclass, UUID uuid){
		return getLevel(playerclass, Bukkit.getPlayer(uuid));
	}

	public static int getLevel(String playerclass, Player player) throws NullPointerException {
		PlayerData data = SaveManager.getData(player);
		return data.getClassLevel(playerclass);
	}
	
	public static int getXpToNext(Player player, String playerclass) throws NullPointerException{
		PlayerData data = SaveManager.getData(player);
		return data.getClassXPR(playerclass);
	}
	
	public static String findClass(Player player) {
		String cClass = SaveManager.getData(player).getCurrentClass();
		return !cClass.equals("") ?  cClass : "none";
		
	}

	public static String findClass(UUID uuid){
		String cClass = SaveManager.getData(uuid).getCurrentClass();
		return !cClass.equals("") ?  cClass : "none";
	}

	public static int getXP(Player player, String playerclass) throws NullPointerException {
		return SaveManager.getData(player).getClassXP(playerclass);
	}

	public static int getGems(Player p){
		return SaveManager.getData(p).getGems();
	}

	public static void resetclass(Player player, String playerclass){
		PlayerData data = SaveManager.getData(player);
		data.setClassXP(playerclass,0);
		data.setClassLevel(playerclass,0);
		data.setClassXPR(playerclass,100);
	}

	public static Location getSpawn(String type){
		Object spawn = main.getConfig().get("server.spawn." + type);
		return spawn instanceof Location ? (Location) spawn : null;
	}

	private static void cexp(Player player, String classtoaddto, int amount){

		PlayerData data = SaveManager.getData(player);
		int level = data.getClassLevel(classtoaddto); // 1
		int[] xprTiers = {100, 500, 1000, 2500, 4000, 7000, 8500, 10000, 20000};
		int xpr = data.getClassXPR(classtoaddto);
		if(xpr <= amount && level < 10){ // player levels up
			int prevProgress = data.getClassXP(classtoaddto);
			data.setClassXP(classtoaddto,0);
			data.setClassXPR(classtoaddto,xprTiers[level]);
			data.setClassLevel(classtoaddto,data.getClassLevel(classtoaddto)+1);
			cexp(player, classtoaddto, amount - (xpr - prevProgress));
		}else{ // doesn't level up
			data.setClassXP(classtoaddto,data.getClassXP(classtoaddto)+amount);
			if(level == 10) data.setClassXP(classtoaddto,Key.MAX_XP);
		}
	}

	public static void setClass(Player player, String newclass){
		TravellerEvents.resetTraveller(player);
		SaveManager.getData(player).setCurrentClass(newclass);
	}

	public static void toggleBlockNotifications(Player player, boolean silent){
		final String path = "players." + player.getUniqueId() + ".blocknotifs";
		boolean notifications = main.getConfig().getBoolean(path);
		main.getConfig().set(path, !notifications);
		if(!silent)
			player.sendMessage(notifications ? ChatColor.RED + "New block notifications have been toggled off." : ChatColor.GREEN + "Toggled on new block notifications!");
		UUID uuid = player.getUniqueId();
		if (notifications) TravellerEvents.blockNotifs.remove(uuid);
		else TravellerEvents.blockNotifs.add(uuid);
		main.saveConfig();
	}

	private static void gexp(Player player, int amount){
		if(amount == 0) return;
		if(lv >= 59) return;
		if(DesertMain.xpToNext <= currentProgress + amount){
			String[] levelUp = StringUtil.getCenteredMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "EXP MILESTONE!" + ChatColor.GREEN + " (" + ChatColor.GRAY + (DesertMain.lv - 1) + " ➞ " + ChatColor.GREEN + ChatColor.BOLD + (DesertMain.lv - 1) + ChatColor.GREEN + ")", ChatColor.GREEN + "You reached level" + ChatColor.BOLD + " " + (DesertMain.lv - 1) + ChatColor.GREEN + "!");
			String[] commandMessage = StringUtil.getCenteredMessage("Click here to view your milestones progression", "and claim rewards!");
			List<BaseComponent> components = new ArrayList<>();
			for(String str : commandMessage){
				components.addAll(Arrays.asList(TextComponent.fromLegacyText(str)));
			}
			TextComponent component = new TextComponent(components.toArray(new BaseComponent[0]));
			component.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
			component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(net.md_5.bungee.api.ChatColor.GOLD + "Click to use /expmilestones").create()));
			component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/expmilestones"));
			player.sendMessage(StringUtil.ChatWrapper.THICK_HORIZONTAL_LINE.toString() + "\n");
			player.sendMessage(levelUp);
			player.spigot().sendMessage(component);
			player.sendMessage("\n" + StringUtil.ChatWrapper.THICK_HORIZONTAL_LINE);
			MiscUtils.ootChestFanfare(player);
			lv++;
			int prevProgress = currentProgress;
			int prevNext = xpToNext;
			currentProgress = 0;
			if(lv >= 29) xpToNext = lv * 300;
			else xpToNext = lv * 200;
			if(lv == 59) player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "MILESTONES MAXED!" + ChatColor.GREEN + " Open the EXP Milestones inventory using /expmilestones and reset your milestones to gain a STAR, and a potential cosmetic!");
			unclaimed.add(lv - 1);
			MilestonesUtil.setDisplayCase(MilestonesUtil.getDisplayCase(player).replaceAll("\\d+", DesertMain.lv + ""), player);

			gexp(player, amount - (prevNext - prevProgress));
		}else{
			currentProgress += amount;
		}
	}

	public static void addXP(Player player, String classtoaddto, int amount) {
		if(booster.containsKey(player.getUniqueId())) amount = Math.round(amount * booster.get(player.getUniqueId()));
		cexp(player, classtoaddto, amount);
		gexp(player, amount);
	}
}
