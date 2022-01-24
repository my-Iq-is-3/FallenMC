package me.zach.DesertMC;

import org.bukkit.ChatColor;
@SuppressWarnings("unused")
public enum Prefix {

    DEBUG(ChatColor.DARK_PURPLE,"[DEBUG]",false),
    MYSTIC(ChatColor.LIGHT_PURPLE, "MYSTIC", true),
    PIONEER(ChatColor.DARK_PURPLE, "PIONEER", false),
    SUPPORTER(ChatColor.GREEN, "SUPPORTER", true),
    SERVER(ChatColor.BLUE,"[SERVER]",false),
    COOWNER(ChatColor.GOLD, "CO-OWNER", true),
    ADMIN(ChatColor.RED, "ADMIN", true),
    NPC(ChatColor.GRAY, "(" + ChatColor.GREEN + "NPC" + ChatColor.GRAY + ")", false);



    public final ChatColor color;
    public final String content;
    public final boolean isBold;
    Prefix(ChatColor color, String content,boolean bold){
        this.color = color;
        this.content = content;
        this.isBold = bold;
    }


    @Override
    public String toString() {
        if(isBold){
            return color + "" + ChatColor.BOLD + content + color;
        }else{
            return color + content;
        }
    }
}
