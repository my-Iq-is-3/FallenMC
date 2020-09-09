package me.zach.DesertMC.Utils;

import me.zach.DesertMC.Prefix;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class TitleUtils {
    private static final Plugin pl = Bukkit.getPluginManager().getPlugin("Fallen");
    public static boolean hasTitle(Player p, Prefix pr){
        return pl.getConfig().getStringList("players." + p.getUniqueId() + ".titles").contains(pr.name());
    }
    public static void initializeTitles(Player p){
        pl.getConfig().set("players." + p.getUniqueId() + ".titles", new ArrayList<String>());
        pl.saveConfig();
    }
    public static boolean setTitle(Player p, Prefix pr){
        if(pl.getConfig().getStringList("players." + p.getUniqueId() + ".titles").contains(pr.name())){
            pl.getConfig().set("players." + p.getUniqueId() + ".title", pr.name());
            pl.saveConfig();
            return true;
        }else return false;
    }
    public static void resetTitle(Player p){
        pl.getConfig().set("players." + p.getUniqueId() + ".title", null);
        pl.saveConfig();
    }
    public static void addTitle(Player p, Prefix pr){
        List<String> titles = pl.getConfig().getStringList("players." + p.getUniqueId() + "titles");
        titles.add(pr.name());
        p.sendMessage(ChatColor.GRAY + "You have just received the title \"" + pr + ChatColor.GRAY +"\". Wear it with pride using /selecttitle " + pr.name());
        pl.getConfig().set("players." + p.getUniqueId() + ".titles", titles);
        pl.saveConfig();
    }
}
