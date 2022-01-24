package me.zach.DesertMC.Utils;

import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.Prefix;
import me.zach.DesertMC.Utils.Config.ConfigUtils;
import me.zach.databank.saver.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class TitleUtils {
    private static final Plugin pl = DesertMain.getInstance;
    public static boolean hasTitle(Player p, Prefix pr){
        return ConfigUtils.getUnlockedTitles(p.getUniqueId()).contains(pr);
    }
    public static boolean setTitle(Player p, Prefix pr){
        if(hasTitle(p, pr)){
            PlayerData data = ConfigUtils.getData(p);
            data.setTitle(pr);
            return true;
        }else return false;
    }
    public static void resetTitle(Player p){
        ConfigUtils.getData(p).setTitle(null);
    }
    public static void addTitle(Player p, Prefix pr){
        PlayerData data = ConfigUtils.getData(p);
        List<Prefix> titles = data.getTitles();
        titles.add(pr);
        p.sendMessage("Unlocked title " + pr + ChatColor.RESET + "!\n§7Select with /selecttitle " + pr.name());
        setTitle(p,pr);
    }
}
