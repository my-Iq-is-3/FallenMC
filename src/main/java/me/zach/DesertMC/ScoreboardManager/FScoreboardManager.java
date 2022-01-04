package me.zach.DesertMC.ScoreboardManager;

import me.zach.DesertMC.GameMechanics.Events;
import me.zach.DesertMC.Utils.Config.ConfigUtils;
import me.zach.DesertMC.Utils.PlayerUtils;
import me.zach.databank.saver.Key;
import me.zach.databank.saver.PlayerData;
import me.zach.databank.saver.SaveManager;
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
import me.zach.DesertMC.GameMechanics.Events;


public class FScoreboardManager {

	public static void initialize(Player player) {
			ScoreboardManager smanager = Bukkit.getScoreboardManager();
			Scoreboard mains = smanager.getNewScoreboard();
			Objective objective = mains.registerNewObjective("main", "dummy");
			objective.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "FallenMC");
			objective.setDisplaySlot(DisplaySlot.SIDEBAR);
			PlayerData data = ConfigUtils.getData(player);
			Score gems = objective.getScore(ChatColor.WHITE + "Gems: " + ChatColor.GREEN + data.getGems());
			Score level = objective.getScore(ChatColor.WHITE + "Level: " + ChatColor.AQUA + data.getClassLevel(data.getCurrentClass()));
			Score souls = objective.getScore(ChatColor.WHITE + "Souls: " + ChatColor.LIGHT_PURPLE + data.getSouls());
			Score xp;
			String clazz = data.getCurrentClass();
			//set experience element
			if(ConfigUtils.getLevel(ConfigUtils.findClass(player), player) >= 10){
				xp = objective.getScore(ChatColor.WHITE + "XP: " + ChatColor.AQUA + "MAX");
			}else{
				int exp = data.getClassXP(clazz);
				int totalxp = data.getClassXPR(clazz);
				xp = objective.getScore(ChatColor.WHITE + "XP: " + ChatColor.GREEN + exp + ChatColor.WHITE + "/" + ChatColor.DARK_GRAY + totalxp);
			}
			//Now we have to make the first letter uppercase for the player classes
			String betterclass = ConfigUtils.findClass(player);
			char[] betterclassarr = betterclass.toCharArray();
			betterclassarr[0] = Character.toUpperCase(betterclassarr[0]);
			Score playerclass = objective.getScore(ChatColor.WHITE + "Class: " + ChatColor.GREEN + new String(betterclassarr));
			Events.ks.putIfAbsent(player.getUniqueId(), 0);
			//killstreaks
			Score killstreak = objective.getScore(ChatColor.WHITE + "Killstreak: " + ChatColor.RED + Events.ks.get(player.getUniqueId()));
			Score inCombat = objective.getScore(ChatColor.WHITE + "Status: " + ChatColor.GREEN + "???");
			if(PlayerUtils.fighting.containsKey(player.getUniqueId())) {
				int timeleft = PlayerUtils.fighting.get(player.getUniqueId());
				if(timeleft == 0)
					inCombat = objective.getScore(ChatColor.WHITE + "Status: " + ChatColor.GREEN + "Idle");
				else
					inCombat = objective.getScore(ChatColor.WHITE + "Status: " + ChatColor.RED + "Combat " + ChatColor.DARK_GRAY + "(" + PlayerUtils.fighting.get(player.getUniqueId()) + ")");
			}
			//ip
			Score ip = objective.getScore(ChatColor.YELLOW + "play.fallenmc.xyz");
			//blanks
			Score blank1 = objective.getScore("   ");
			Score blank2 = objective.getScore("  ");
			Score blank3 = objective.getScore(" ");
			Score blank4 = objective.getScore("");

			//now set them to their respective value
			ip.setScore(0);
			blank1.setScore(1);
			inCombat.setScore(2);
			killstreak.setScore(3);
			blank2.setScore(4); //
			gems.setScore(5);
			souls.setScore(6);
			blank3.setScore(7); //
			playerclass.setScore(8);
			xp.setScore(9);
			level.setScore(10);

			blank4.setScore(11);

			player.setScoreboard(mains);


			/*
				FALLENMC

				Level: 10
				XP: MAX
				Class: Scout

				Souls: 100
				Gems: 409

				Killstreak: 100
				Status: Combat (10)

				play.fallenmc.xyz
			 */
	}
}
