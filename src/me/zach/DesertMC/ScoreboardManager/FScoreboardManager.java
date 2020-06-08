package me.zach.DesertMC.ScoreboardManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.Utils.Config.ConfigUtils;
import me.zach.DesertMC.PlayerManager.Events;


public class FScoreboardManager {
	private static final FileConfiguration economyConfig = Bukkit.getPluginManager().getPlugin("Econo").getConfig();

	public static void initialize(Player player) {
			ScoreboardManager smanager = Bukkit.getScoreboardManager();
			Scoreboard mains = smanager.getNewScoreboard();
			Objective objective = mains.registerNewObjective("main", "dummy");
			objective.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "FallenMC");
			objective.setDisplaySlot(DisplaySlot.SIDEBAR);
			System.out.println("ConfigUtils: " + ConfigUtils.INSTANCE);
			System.out.println("Level: " + ConfigUtils.INSTANCE.getLevel(ConfigUtils.INSTANCE.findClass(player),player));
			System.out.println("Class: " + ConfigUtils.INSTANCE.findClass(player));


			Score gems = objective.getScore("Gems" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + economyConfig.getInt("players." + player.getUniqueId() + ".balance"));
			Score level = objective.getScore("Level" + ChatColor.DARK_GRAY + ": " + ChatColor.AQUA + ConfigUtils.INSTANCE.getLevel(ConfigUtils.INSTANCE.findClass(player), player));
			Score souls = objective.getScore("Souls" + ChatColor.DARK_GRAY + ": " + ChatColor.LIGHT_PURPLE + DesertMain.getInstance.getConfig().getInt("players." + player.getUniqueId() + ".souls"));
			Score xp;
			//set experience element

			if(ConfigUtils.INSTANCE.getXP(player, ConfigUtils.INSTANCE.findClass(player)).equals("MAX")) {
				xp = objective.getScore("XP" + ChatColor.DARK_GRAY + ": " + ChatColor.AQUA + "MAX");

			}else {

				int totalxp = ConfigUtils.INSTANCE.getXpToNext(player, ConfigUtils.INSTANCE.findClass(player)) + (Integer)ConfigUtils.INSTANCE.getXP(player,ConfigUtils.INSTANCE.findClass(player));
				xp = objective.getScore("XP" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + ConfigUtils.INSTANCE.getXP(player, ConfigUtils.INSTANCE.findClass(player)) + ChatColor.WHITE + "/" + ChatColor.DARK_GRAY + totalxp);
			}

			//Now we have to make the first letter uppercase for the player classes
			String betterclass = ConfigUtils.INSTANCE.findClass(player);
			char[] betterclassarr = betterclass.toCharArray();
			betterclassarr[0] = Character.toUpperCase(betterclassarr[0]);
			Score playerclass = objective.getScore("Class" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + new String(betterclassarr));

			Events.ks.putIfAbsent(player.getUniqueId(), 0);

			//killstreaks
			Score killstreak = objective.getScore("Killstreak" + ChatColor.DARK_GRAY + ": " + ChatColor.RED + Events.ks.get(player.getUniqueId()));

			//ip
			Score ip = objective.getScore(ChatColor.YELLOW + "Make sure to view our shop!");

			//blanks
			Score blank1 = objective.getScore("   ");
			Score blank2 = objective.getScore("  ");
			Score blank3 = objective.getScore(" ");
			Score blank4 = objective.getScore("");

			//now set them to their respective value
			blank1.setScore(1);
			killstreak.setScore(2);
			blank2.setScore(3);
			gems.setScore(4);
			souls.setScore(5);
			blank3.setScore(6);
			playerclass.setScore(7);
			xp.setScore(8);
			ip.setScore(0);
			level.setScore(9);
			blank4.setScore(10);

			player.setScoreboard(mains);








	}

}
