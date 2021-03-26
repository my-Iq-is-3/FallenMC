package me.zach.DesertMC.Utils.RankUtils;

import me.zach.DesertMC.Prefix;
import me.zach.DesertMC.Utils.TitleUtils;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class RankEvents implements Listener {
    public RankEvents(Plugin pl){
        p = pl;
    }
    Plugin p;
    @EventHandler
    public void addRankColor(AsyncPlayerChatEvent e){
        if(p.getConfig().getString("players." + e.getPlayer().getUniqueId() + ".rank") != null){
            if(p.getConfig().getString("players." + e.getPlayer().getUniqueId() + ".rank").equals("COOWNER")) e.setFormat(Rank.valueOf(p.getConfig().getString("players." + e.getPlayer().getUniqueId() + ".rank")).c + "" + ChatColor.BOLD + e.getPlayer().getName() + ChatColor.GRAY + ": " + ChatColor.RESET + e.getMessage());
            else e.setFormat(Rank.valueOf(p.getConfig().getString("players." + e.getPlayer().getUniqueId() + ".rank")).c + e.getPlayer().getName() + ChatColor.GRAY + ": " + ChatColor.RESET + e.getMessage());

        }else{
            e.setFormat(ChatColor.GRAY + e.getPlayer().getName() + ": " + ChatColor.GRAY + e.getMessage());
        }
        Prefix title = null;
        String displayCase = null;
        try{
            title = Prefix.valueOf(p.getConfig().getString("players." + e.getPlayer().getUniqueId() + ".title"));
        }catch(Exception exc){
            if(!(exc instanceof NullPointerException)){
                exc.printStackTrace();
            }
        }
        try{
            displayCase = p.getConfig().getString("players." + e.getPlayer().getUniqueId() + ".displaycase");
        }catch(Exception exc){
            if(!(exc instanceof NullPointerException)){
                exc.printStackTrace();
            }
        }

        if(displayCase != null) e.setFormat(displayCase + ChatColor.DARK_GRAY + " | " + ChatColor.RESET +  e.getFormat());
        if(title != null) e.setFormat(title + "" + ChatColor.DARK_GRAY + " | " + ChatColor.RESET + e.getFormat());
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void giveRankTitle(PlayerJoinEvent e){
        if(p.getConfig().getString("players." + e.getPlayer().getUniqueId() + ".rank") != null) {
            if (!p.getConfig().getStringList("players." + e.getPlayer().getUniqueId() + ".titles").contains(Rank.valueOf(p.getConfig().getString("players." + e.getPlayer().getUniqueId() + ".rank")).p.name())){
                Player pl = e.getPlayer();
                pl.sendMessage(Prefix.SERVER + ":" +  Rank.valueOf(p.getConfig().getString("players." + pl.getUniqueId() + ".rank")).c + " Wow, thank you so much for buying one of our ranks!! It helps support the server so much. You have our greatest gratitude, " + ChatColor.RED + pl.getName() + "!");
                pl.playSound(pl.getLocation(), Sound.ENDERDRAGON_DEATH, 10, 1);
                pl.sendTitle(Rank.valueOf(p.getConfig().getString("players." + pl.getUniqueId() + ".rank")).c + "Thank you!", ChatColor.DARK_RED + "<3");
                TitleUtils.addTitle(pl, Rank.valueOf(p.getConfig().getString("players." + pl.getUniqueId() + ".rank")).p);

            }
        }
    }
}
