package me.zach.DesertMC.Utils.Config;

import me.zach.DesertMC.DesertMain;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

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

	public static int getSouls(Player player){
		return config.getInt("players." + player.getUniqueId() + ".souls");
	}

	public static boolean deductGems(Player player, int amount){
		int gems = config.getInt("players." + player.getUniqueId() + ".gems");
		if(gems >= amount){
			config.set("players." + player.getUniqueId() + ".gems", gems - amount);
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
		return config.getInt("players." + p.getUniqueId() + ".gems");
	}

	public static void resetclass(Player player, String playerclass){
		// 100 xp to next level
		config.set("players." + player.getUniqueId() + ".classes." + playerclass + ".xptonext", 100);
		config.set("players." + player.getUniqueId() + ".classes." + playerclass + ".hasxp", 0);
		config.set("players." + player.getUniqueId() + ".classes." + playerclass + ".level", 1);
		main.saveConfig();
	}


	public static void setClass(Player player, String newclass){
		config.set("players." + player.getUniqueId() + ".classes.inuse", newclass);
	}

	@SuppressWarnings("deprecation")
	public static void addXP(Player player, String classtoaddto, int amount) {

		int xptonext = getXpToNext(player, classtoaddto);
		if(getLevel(classtoaddto, player) >= 10) {
			return;
		}

		if(xptonext <= amount) {
			int pastlevel = config.getInt("players." + player.getUniqueId() + ".classes." + classtoaddto + ".level");
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
				main.saveConfig();
				break;
			case 4:
				config.set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".xptonext", 4000);
				config.set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".hasxp", 0);
				main.saveConfig();
				break;
			case 5:
				config.set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".xptonext", 7000);
				config.set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".hasxp", 0);
				main.saveConfig();
				break;
			case 6:
				config.set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".xptonext", 8500);
				config.set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".hasxp", 0);
				main.saveConfig();
				break;
			case 7:
				config.set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".xptonext", 10000);
				config.set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".hasxp", 0);
				main.saveConfig();
				break;
			case 8:

				config.set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".xptonext", 20000);
				config.set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".hasxp", 0);
				main.saveConfig();
				break;
			case 9:

				config.set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".xptonext", "MAX");
				config.set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".hasxp", "MAX");
				main.saveConfig();
				break;


			}
			config.set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".level", getLevel(classtoaddto, player) + 1);
			player.sendTitle(ChatColor.BLUE + "You leveled up!", ChatColor.DARK_GRAY + "You are now level " + ChatColor.AQUA + (pastlevel + 1));
			main.saveConfig();

		}else {
			if(!getXP(player,classtoaddto).equals("MAX")) {
			config.set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".hasxp", (Integer)getXP(player, classtoaddto) + amount);
			config.set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".xptonext", getXpToNext(player, classtoaddto) - amount);
			}
			main.saveConfig();

		}

	}

	
}
