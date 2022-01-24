package me.zach.DesertMC.Utils.RankUtils;

import com.destroystokyo.paper.Title;
import me.zach.DesertMC.GameMechanics.EXPMilesstones.MilestonesUtil;
import me.zach.DesertMC.Prefix;
import me.zach.DesertMC.Utils.Config.ConfigUtils;
import me.zach.DesertMC.Utils.StringUtils.StringUtil;
import me.zach.DesertMC.Utils.TitleUtils;
import me.zach.databank.saver.PlayerData;
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
    public static final ArrayList<ChatColor> ccList = new ArrayList<>(Arrays.asList(ChatColor.values()));
    public static final HashMap<String, ChatColor> friendlyCC = new HashMap<>();
    public static final HashMap<ChatColor, String> colorShortcuts = new HashMap<>();
    static{
        ccList.remove(ChatColor.RESET);
        for(int i = 0, ccListSize = ccList.size(); i < ccListSize; i++){
            ChatColor color = ccList.get(i);
            if(color.name().startsWith("DARK_") || color.name().startsWith("LIGHT_")){
                ccList.remove(i);
                ccList.add(0, color);
            }
        }

        colorShortcuts.put(ChatColor.BOLD, "BO");
        colorShortcuts.put(ChatColor.GOLD, "GO");
        colorShortcuts.put(ChatColor.GRAY, "GR");
        colorShortcuts.put(ChatColor.BLACK, "BL");
        colorShortcuts.put(ChatColor.DARK_GRAY, "DGRY");

        for(ChatColor color : ccList){
            String lowerName = color.name().replaceAll("_", " ").toLowerCase();
            lowerName = StringUtil.capitalizeFirst(lowerName);
            friendlyCC.put(color + lowerName, color);
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
        PlayerData data = ConfigUtils.getData(p);
        Rank rank = data.getRank();
        if(rank != null){
            e.setMessage(colorMessage(e.getMessage()));
            if(rank == Rank.COOWNER) e.setFormat(rank.c + "" + ChatColor.BOLD + p.getName() + ChatColor.GRAY + ": " + ChatColor.RESET + e.getMessage());
            else e.setFormat(rank.c + p.getName() + ChatColor.GRAY + ": " + ChatColor.RESET + e.getMessage());
        }else{
            e.setFormat(ChatColor.GRAY + p.getName() + ": " + ChatColor.GRAY + e.getMessage());
        }

        Prefix title = data.getTitle();
        String displayCase = MilestonesUtil.getDisplayCase(p);

        p.playSound(p.getLocation(), Sound.SHOOT_ARROW, 10, 1);

        e.setFormat(displayCase + ChatColor.DARK_GRAY + " | " + ChatColor.RESET +  e.getFormat());
        if(title != null) e.setFormat(title + "" + ChatColor.DARK_GRAY + " | " + ChatColor.RESET + e.getFormat());
    }

    public static String colorMessage(String msg){
        msg = msg.replaceAll("\\b*(?<!\\\\)!BO", ChatColor.BOLD + "");
        msg = msg.replaceAll("\\b*(?<!\\\\)!GO", ChatColor.GOLD + "");
        msg = msg.replaceAll("\\b*(?<!\\\\)!BL", ChatColor.BLACK + "");
        msg = msg.replaceAll("\\b*(?<!\\\\)!GR", ChatColor.GRAY + "");
        msg = msg.replaceAll("\\b*(?<!\\\\)!DGRY", ChatColor.DARK_GRAY + "");
        for(ChatColor c : ccList){
            if(c != ChatColor.RESET){
                msg = msg.replaceAll("\\b*(?<!\\\\)" + c.name(), c + "");
                msg = msg.replaceAll("\\b*(?<!\\\\)!" + colorShortcuts.get(c), c + "");
            }
        }
        msg = msg.replaceAll("\\\\", "");
        return msg;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void giveRankTitle(PlayerJoinEvent e){
        Player p = e.getPlayer();
        Rank rank = ConfigUtils.getRank(p);
        if(rank != null) {
            if(TitleUtils.hasTitle(e.getPlayer(), rank.p)){
                p.sendMessage(rank.c + "Wow, thank you so much for buying one of our ranks!! It helps support the server so much. You have our greatest gratitude, " + rank.c + p.getName() + "!\n");
                p.playSound(p.getLocation(), Sound.ENDERDRAGON_DEATH, 7, 1);
                p.sendTitle(new Title(rank.c + "Thank you!", ChatColor.DARK_RED + "<3"));
                TitleUtils.addTitle(p, rank.p);
            }
        }
    }
}
