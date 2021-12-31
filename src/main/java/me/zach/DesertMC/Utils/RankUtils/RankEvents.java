package me.zach.DesertMC.Utils.RankUtils;

import me.zach.DesertMC.GameMechanics.EXPMilesstones.MilestonesUtil;
import me.zach.DesertMC.Prefix;
import me.zach.DesertMC.Utils.TitleUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class RankEvents implements Listener {
    public static final ArrayList<ChatColor> ccList = new ArrayList<>();
    public static final HashMap<String, ChatColor> friendlyCC = new HashMap<>();
    public static final HashMap<UUID, Rank> rankSession = new HashMap<>();
    public static final HashMap<ChatColor, String> colorShortcuts = new HashMap<>();
    static{
        ccList.addAll(Arrays.asList(ChatColor.values()));

        ccList.remove(ChatColor.RESET);

        colorShortcuts.put(ChatColor.BOLD, "BO");
        colorShortcuts.put(ChatColor.GOLD, "GO");
        colorShortcuts.put(ChatColor.GRAY, "GR");
        colorShortcuts.put(ChatColor.BLACK, "BL");

        for(ChatColor color : ccList){
            String lowerName = color.name().replaceAll("_", " ").toLowerCase();
            StringBuilder builder = new StringBuilder(lowerName);
            builder.replace(0, 1, lowerName.substring(0, 1).toUpperCase());
            friendlyCC.put(color + builder.toString(), color);
        }

        for(ChatColor color : ccList){
            if(color.name().startsWith("DARK_")) colorShortcuts.put(color, "D" + color.name().replace("DARK_", "").charAt(0));
        }

        for(ChatColor color : ccList){
            if(color.name().startsWith("LIGHT_")) colorShortcuts.put(color, "L" + color.name().replace("LIGHT_", "").charAt(0));
        }

        for(ChatColor color : ccList){
            if(!colorShortcuts.containsKey(color) && !color.equals(ChatColor.BOLD) && !color.equals(ChatColor.GOLD) && !color.equals(ChatColor.BLACK) && !color.equals(ChatColor.GRAY)) colorShortcuts.put(color, color.name().charAt(0) + "");
        }
        Bukkit.getConsoleSender().sendMessage("Finished generating color shortcuts map, colorShortcuts: " + colorShortcuts.values());
    }
    public RankEvents(Plugin plugin){
        pl = plugin;
    }
    Plugin pl;
    @EventHandler
    public void addChatFormatting(AsyncPlayerChatEvent e){
        Player p = e.getPlayer();
        if(rankSession.containsKey(p.getUniqueId())){
            Rank rank = rankSession.get(p.getUniqueId());
            e.setMessage(colorSupporterMessage(e.getMessage()));
            if(rank.equals(Rank.COOWNER)) e.setFormat(rank.c + "" + ChatColor.BOLD + p.getName() + ChatColor.GRAY + ": " + ChatColor.RESET + e.getMessage());
            else e.setFormat(rank.c + p.getName() + ChatColor.GRAY + ": " + ChatColor.RESET + e.getMessage());
        }else{
            e.setFormat(ChatColor.GRAY + p.getName() + ": " + ChatColor.GRAY + e.getMessage());
        }

        Prefix title = null;
        String displayCase = MilestonesUtil.getDisplayCase(p);
        String unparsedTitle = pl.getConfig().getString("players." + p.getUniqueId() + ".title");
        if(unparsedTitle != null) title = Prefix.valueOf(unparsedTitle);

        p.playSound(p.getLocation(), Sound.SHOOT_ARROW, 10, 1);

        e.setFormat(displayCase + ChatColor.DARK_GRAY + " | " + ChatColor.RESET +  e.getFormat());
        if(title != null) e.setFormat(title + "" + ChatColor.DARK_GRAY + " | " + ChatColor.RESET + e.getFormat());
    }

    public String colorSupporterMessage(String msg){
        msg = msg.replaceAll("\\b*(?<!\\\\)!BO", ChatColor.BOLD + "");
        msg = msg.replaceAll("\\b*(?<!\\\\)!GO", ChatColor.GOLD + "");
        msg = msg.replaceAll("\\b*(?<!\\\\)!BL", ChatColor.BLACK + "");
        msg = msg.replaceAll("\\b*(?<!\\\\)!GR", ChatColor.GRAY + "");
        for(ChatColor c : ccList){
            if(!c.equals(ChatColor.RESET)){
                msg = msg.replaceAll("\\b*(?<!\\\\)" + c.name(), c + "");
                msg = msg.replaceAll("\\b*(?<!\\\\)!" + colorShortcuts.get(c), c + "");
            }
        }

        msg = msg.replaceAll("\\\\", "");
        return msg;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void giveRankTitle(PlayerJoinEvent e){
        if(pl.getConfig().getString("players." + e.getPlayer().getUniqueId() + ".rank") != null) {
            if (!pl.getConfig().getStringList("players." + e.getPlayer().getUniqueId() + ".titles").contains(Rank.valueOf(pl.getConfig().getString("players." + e.getPlayer().getUniqueId() + ".rank")).p.name())){
                Player p = e.getPlayer();
                Rank rank = Rank.valueOf(pl.getConfig().getString("players." + p.getUniqueId() + ".rank"));
                p.sendMessage(Prefix.SERVER + ":" +  rank.c + " Wow, thank you so much for buying one of our ranks!! It helps support the server so much. You have our greatest gratitude, " + rank.c + p.getName() + "!");
                p.playSound(p.getLocation(), Sound.ENDERDRAGON_DEATH, 8, 1);
                p.sendTitle(rank.c + "Thank you!", ChatColor.DARK_RED + "<3");
                TitleUtils.addTitle(p, Rank.valueOf(pl.getConfig().getString("players." + p.getUniqueId() + ".rank")).p);
            }
        }
    }
}
