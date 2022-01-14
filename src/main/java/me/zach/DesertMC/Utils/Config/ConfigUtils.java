package me.zach.DesertMC.Utils.Config;

import itempackage.Items;
import me.zach.DesertMC.ClassManager.TravellerEvents;
import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.GameMechanics.EXPMilesstones.MilestonesData;
import me.zach.DesertMC.Utils.MiscUtils;
import me.zach.DesertMC.Utils.StringUtils.StringUtil;
import me.zach.artifacts.gui.inv.ArtifactData;
import me.zach.databank.DBCore;
import me.zach.databank.saver.PlayerData;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import com.destroystokyo.paper.Title;

import java.util.*;
import java.util.function.Supplier;


public class ConfigUtils {
	static final HashMap<String, Supplier<ItemStack>> FALLEN_PIECES = new HashMap<>();
	static{
		FALLEN_PIECES.put("wizard", Items::getFallenHelmet);
		FALLEN_PIECES.put("tank", Items::getFallenChestplate);
		FALLEN_PIECES.put("corrupter", Items::getFallenLeggings);
		FALLEN_PIECES.put("scout", Items::getFallenBoots);
	}

	private static final Plugin main = DesertMain.getInstance;

	private ConfigUtils() {

	}


	public static boolean deductSouls(Player player, int amount){
		PlayerData data = getData(player);
		int souls = data.getSouls();
		if(souls >= amount){
			data.setSouls(souls-amount);
			return true;
		}else return false;
	}

	public static void addGems(Player player,int amount){
		PlayerData data = getData(player);
		data.setGems(data.getGems()+amount);
	}

	public static int getSouls(Player player){

		return getData(player).getSouls();
	}

	public static void addSouls(Player player, int amount){
		PlayerData data = getData(player);
		data.setSouls(data.getSouls()+amount);
	}

	public static boolean deductGems(Player player, int amount){
		PlayerData data = getData(player);
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
		PlayerData data = getData(player);
		return data.getClassLevel(playerclass);
	}
	
	public static int getXpToNext(Player player, String playerclass) throws NullPointerException{
		PlayerData data = getData(player);
		return data.getClassXPR(playerclass);
	}
	
	public static String findClass(Player player) {
		String cClass = getData(player).getCurrentClass();
		return !cClass.equals("") ?  cClass : "none";
		
	}

	public static String findClass(UUID uuid){
		String cClass = getData(uuid).getCurrentClass();
		return !cClass.equals("") ?  cClass : "none";
	}

	public static int getXP(Player player, String playerclass) throws NullPointerException {
		return getData(player).getClassXP(playerclass);
	}

	public static int getGems(Player p){
		return getData(p).getGems();
	}

	public static void resetclass(Player player, String playerclass){
		PlayerData data = getData(player);
		data.setClassXP(playerclass,0);
		data.setClassLevel(playerclass,0);
		data.setClassXPR(playerclass,100);
	}

	public static Location getSpawn(String type){
		Object spawn = main.getConfig().get("server.spawn." + type);
		return spawn instanceof Location ? (Location) spawn : Bukkit.getWorlds().get(0).getSpawnLocation();
	}

	private static void cexp(Player player, String classtoaddto, int amount){
		PlayerData data = getData(player);
		int level = data.getClassLevel(classtoaddto); // 1
		int[] xprTiers = {100, 500, 1000, 2500, 4000, 7000, 8500, 10000, 20000};
		int xpr = data.getClassXPR(classtoaddto);
		if(xpr-data.getClassXP(classtoaddto) <= amount && level < 10){ // player levels up
			int prevProgress = data.getClassXP(classtoaddto);
			level++;
			if(level == 10){
				String classMaxed = StringUtil.stylizeClass(classtoaddto).toUpperCase() + " CLASS MAXED";
				String color = ChatColor.getLastColors(classMaxed);
				player.sendTitle(new Title(classMaxed + "!"));
				player.sendMessage(color + ChatColor.BOLD + ChatColor.stripColor(classMaxed) + color + " Congratulations!");
				Inventory inventory = player.getInventory();
				ItemStack fallenPiece = FALLEN_PIECES.get(classtoaddto).get();
				player.sendMessage(ChatColor.RED + "Your reward, a blessing: " + fallenPiece.getItemMeta().getDisplayName());
				if(!inventory.addItem(fallenPiece).isEmpty()){
					Item dropped = player.getWorld().dropItemNaturally(player.getLocation(), fallenPiece);
					MiscUtils.setOwner(dropped, player);
					player.sendMessage(ChatColor.DARK_GRAY + "Since the fallen deities could not find an open place in your inventory, they have decided to drop off your glorious prize at your current location.");
				}
			}else{
				data.setClassXPR(classtoaddto,xprTiers[level - 1]);
				player.sendTitle(new Title(ChatColor.WHITE.toString() + (level - 1) + " ➞ " + ChatColor.AQUA + ChatColor.BOLD + level, StringUtil.stylizeClass(classtoaddto) + " Class"));
			}
			data.setClassXP(classtoaddto,0);
			data.setClassLevel(classtoaddto,level);
			cexp(player, classtoaddto, amount - (xpr - prevProgress));
		}else if(level < 10){ // doesn't level up
			data.setClassXP(classtoaddto,data.getClassXP(classtoaddto)+amount);
		}
	}

	public static void setClass(Player player, String newclass){
		TravellerEvents.resetTraveller(player);
		getData(player).setCurrentClass(newclass);
	}

	public static void toggleBlockNotifications(Player player, boolean silent){
		final String path = "players." + player.getUniqueId() + ".blocknotifications";
		boolean notifications = main.getConfig().getBoolean(path);
		main.getConfig().set(path, !notifications);
		if(!silent)
			player.sendMessage(notifications ? ChatColor.RED + "New block notifications have been toggled off." : ChatColor.GREEN + "Toggled on new block notifications!");
		UUID uuid = player.getUniqueId();
		if(notifications) TravellerEvents.blockNotifs.remove(uuid);
		else TravellerEvents.blockNotifs.add(uuid);
		main.saveConfig();
	}

	private static void gexp(Player player, int amount){
		if(amount == 0) return;
		MilestonesData data = MilestonesData.get(player);
		if(data.getLevel() >= 59) return;
		if(data.getXpToNext() <= data.getCurrentProgress() + amount){
			int level = data.getLevel();
			String[] levelUp = StringUtil.getCenteredMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "EXP MILESTONE!" + ChatColor.GREEN + " (" + ChatColor.GRAY + (level - 1) + " ➞ " + ChatColor.GREEN + ChatColor.BOLD + (level) + ChatColor.GREEN + ")", ChatColor.GREEN + "You completed level" + ChatColor.BOLD + " " + (level - 1) + ChatColor.GREEN + "!");
			String[] commandMessage = StringUtil.getCenteredMessage("Click here to view your milestones progression", "and claim rewards!");
			List<BaseComponent> components = new ArrayList<>();
			for(String str : commandMessage){
				components.addAll(Arrays.asList(TextComponent.fromLegacyText(str)));
			}
			TextComponent component = new TextComponent(components.toArray(new BaseComponent[0]));
			component.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
			component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(net.md_5.bungee.api.ChatColor.GOLD + "Click to use /expmilestones").create()));
			component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/expmilestones"));
			player.sendMessage(StringUtil.ChatWrapper.HORIZONTAL_LINE + ChatColor.RESET.toString());
			player.sendMessage(levelUp);
			player.spigot().sendMessage(component);
			player.sendMessage("\n" + StringUtil.ChatWrapper.HORIZONTAL_LINE);
			MiscUtils.ootChestFanfare(player);
			int newLevel = level + 1;
			data.setLevel(newLevel);
			int prevProgress = data.getCurrentProgress();
			int prevNext = data.getXpToNext();
			data.setCurrentProgress(0);
			if(newLevel >= 29) data.setXpToNext(data.getLevel() * 300);
			else data.setXpToNext(data.getLevel() * 200);
			if(newLevel == 59) player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "MILESTONES MAXED!" + ChatColor.GREEN + " Open the EXP Milestones inventory using /expmilestones and reset your milestones to gain a STAR, and a potential cosmetic!");
			data.getUnclaimed().add(data.getLevel() - 1);
			gexp(player, amount - (prevNext - prevProgress));
		}else{
			data.setCurrentProgress(data.getCurrentProgress() + amount);
		}
	}

	public static ArtifactData getAD(Player player){
		return getAD(player.getUniqueId());
	}

	public static ArtifactData getAD(UUID uuid){
		return getData(uuid).getArtifactData();
	}
	
	public static PlayerData getData(Player player){
		return getData(player.getUniqueId());
	}
	
	public static PlayerData getData(UUID uuid){
		return DBCore.getInstance().getSaveManager().getData(uuid);
	}

	public static boolean classesMaxed(Player player){
		return classesMaxed(player.getUniqueId());
	}

	public static boolean classesMaxed(UUID uuid){
		PlayerData data = getData(uuid);
		return data.getCorL() == 10 && data.getTankL() == 10 && data.getScoutL() == 10 && data.getWizardL() == 10;
	}

	public static void addXP(Player player, String classtoaddto, int amount) {
		if(DesertMain.booster.containsKey(player.getUniqueId())) amount = Math.round(amount * DesertMain.booster.get(player.getUniqueId()));
		cexp(player, classtoaddto, amount);
		gexp(player, amount);
	}
}
