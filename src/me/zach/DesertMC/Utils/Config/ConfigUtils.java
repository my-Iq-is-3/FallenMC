package me.zach.DesertMC.Utils.Config;

import me.zach.DesertMC.DesertMain;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ConfigUtils {
	public static final ConfigUtils INSTANCE = new ConfigUtils();
	private final DesertMain main = DesertMain.getInstance;
	private final FileConfiguration config = main.getConfig();

	private ConfigUtils() {
		
		
		
	}

	public int getLevel(String playerclass, Player player) throws NullPointerException {
		if(!main.getConfig().contains("players." + player.getUniqueId() + ".classes." + playerclass + ".level")) return 0;
		return main.getConfig().getInt("players." + player.getUniqueId() + ".classes." + playerclass + ".level");
	}
	
	public int getXpToNext(Player player, String playerclass) throws NullPointerException{
		if(!main.getConfig().contains("players." + player.getUniqueId() + ".classes." + playerclass + ".xptonext")){
			return 0;
		}
		return main.getConfig().getInt("players." + player.getUniqueId() + ".classes." + playerclass + ".xptonext");
	}
	
	public String findClass(Player player) {
		
		if(main.getConfig().getString("players." + player.getUniqueId() + ".classes.inuse") != null) {
			return main.getConfig().getString("players." + player.getUniqueId() + ".classes.inuse");
		} else {
			return "none";
		}
		
	}

	public Object getXP(Player player, String playerclass) throws NullPointerException {


			return main.getConfig().getInt("players." + player.getUniqueId() + ".classes." + playerclass + ".hasxp");


	}

	public void resetclass(Player player, String playerclass){
		// 100 xp to next level
		main.getConfig().set("players." + player.getUniqueId() + ".classes." + playerclass + ".xptonext", 100);
		main.getConfig().set("players." + player.getUniqueId() + ".classes." + playerclass + ".hasxp", 0);
		main.getConfig().set("players." + player.getUniqueId() + ".classes." + playerclass + ".level", 1);
		main.saveConfig();
	}


	public void setClass(Player player, String newclass){
		config.set("players." + player.getUniqueId() + ".classes.inuse", newclass);
	}

	@SuppressWarnings("deprecation")
	public void addXP(Player player, String classtoaddto, int amount) throws Exception {

		int xptonext = getXpToNext(player, classtoaddto);
		if(getLevel(classtoaddto, player) >= 10) {
			throw new Exception("Cannot add xp to a maxed out class!");
		}

		if(xptonext <= amount) {
			int pastlevel = main.getConfig().getInt("players." + player.getUniqueId() + ".classes." + classtoaddto + ".level");
			switch (pastlevel) {
			case 1:
				main.getConfig().set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".xptonext", 500);
				main.getConfig().set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".hasxp", 0);

				break;
			case 2:
				main.getConfig().set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".xptonext", 1000);
				main.getConfig().set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".hasxp", 0);
				break;
			case 3:
				main.getConfig().set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".xptonext", 2500);
				main.getConfig().set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".hasxp", 0);
				main.saveConfig();
				break;
			case 4:
				main.getConfig().set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".xptonext", 4000);
				main.getConfig().set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".hasxp", 0);
				main.saveConfig();
				break;
			case 5:
				main.getConfig().set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".xptonext", 7000);
				main.getConfig().set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".hasxp", 0);
				main.saveConfig();
				break;
			case 6:
				main.getConfig().set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".xptonext", 8500);
				main.getConfig().set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".hasxp", 0);
				main.saveConfig();
				break;
			case 7:
				main.getConfig().set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".xptonext", 10000);
				main.getConfig().set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".hasxp", 0);
				main.saveConfig();
				break;
			case 8:

				main.getConfig().set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".xptonext", 20000);
				main.getConfig().set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".hasxp", 0);
				main.saveConfig();
				break;
			case 9:

				main.getConfig().set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".xptonext", "MAX");
				main.getConfig().set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".hasxp", "MAX");
				main.saveConfig();
				break;


			}
			main.getConfig().set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".level", getLevel(classtoaddto, player) + 1);
			player.sendTitle(ChatColor.BLUE + "You leveled up!", "" + ChatColor.AQUA + (pastlevel + 1));
			main.saveConfig();

		}else {
			if(!getXP(player,classtoaddto).equals("MAX")) {
			main.getConfig().set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".hasxp", (Integer)getXP(player, classtoaddto) + amount);
			main.getConfig().set("players." + player.getUniqueId() + ".classes." + classtoaddto + ".xptonext", getXpToNext(player, classtoaddto) - amount);
			}
			main.saveConfig();

		}

	}

	
}
