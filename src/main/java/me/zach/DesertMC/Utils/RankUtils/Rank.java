package me.zach.DesertMC.Utils.RankUtils;

import me.zach.DesertMC.Prefix;
import org.bukkit.ChatColor;

public enum Rank {
    MYSTIC(Prefix.MYSTIC, ChatColor.LIGHT_PURPLE.toString(), false),
    SUPPORTER(Prefix.SUPPORTER, ChatColor.GREEN.toString(), false),
    COOWNER(Prefix.COOWNER, ChatColor.GOLD + ChatColor.BOLD.toString(), true),
    ADMIN(Prefix.ADMIN, ChatColor.RED.toString(), true);
    public final Prefix p;
    public final String c;
    public final boolean admin;
    Rank(Prefix pr, String c, boolean admin){
        this.c = c;
        p = pr;
        this.admin = admin;
    }
}
