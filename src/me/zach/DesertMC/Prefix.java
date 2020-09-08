package me.zach.DesertMC;

import org.bukkit.ChatColor;
@SuppressWarnings("unused")
public enum Prefix {

    DEBUG(ChatColor.DARK_PURPLE,"[DEBUG]",true),
    MYSTIC(ChatColor.LIGHT_PURPLE, "MYSTIC", true),
    SUPPORTER(ChatColor.GREEN, "SUPPORTER", true),
    SERVER(ChatColor.DARK_BLUE,"[SERVER]",false),
    COOWNER(ChatColor.GOLD, "CO-OWNER", false);



    private final ChatColor bold = ChatColor.BOLD;
    private final ChatColor color;
    private final String content;
    private final boolean isBold;
    Prefix(ChatColor color, String content,boolean bold){
        this.color = color;
        this.content = content;
        this.isBold = bold;
    }


    @Override
    public String toString() {
        if(isBold){
            return color + "" + bold + content + color;
        }else{
            return color + content;
        }
    }
}
