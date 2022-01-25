package me.zach.DesertMC.Utils;

import me.zach.DesertMC.Prefix;
import me.zach.DesertMC.Utils.Config.ConfigUtils;
import me.zach.databank.saver.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class TitleUtils {
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
        p.sendMessage(ChatColor.GOLD + "Unlocked title " + pr + ChatColor.GOLD + "!\n§7Select it again with /selecttitle " + pr.name());
        setTitle(p,pr);
    }
}
