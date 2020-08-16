package me.zach.DesertMC;

import org.bukkit.ChatColor;
@SuppressWarnings("unused")
public enum Prefix {

    DEBUG(ChatColor.GREEN,"DEBUG",true),
    SERVER(ChatColor.RED,"SERVER",false);


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
            return color + "[" + bold + content + color + "]";
        }else{
            return color + "[" + content + "]";
        }
    }
}
