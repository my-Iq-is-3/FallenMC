package me.zach.DesertMC.Utils.RankUtils;

import me.zach.DesertMC.Prefix;
import org.bukkit.ChatColor;

public enum Rank {
    MYSTIC(Prefix.MYSTIC, ChatColor.LIGHT_PURPLE, false),
    SUPPORTER(Prefix.SUPPORTER, ChatColor.GREEN, false),
    COOWNER(Prefix.COOWNER, ChatColor.GOLD, false),
    ADMIN(Prefix.ADMIN, ChatColor.RED, false);
    public final Prefix p;
    public final ChatColor c;
    public final boolean admin;
    Rank(Prefix pr, ChatColor ch, boolean admin){
        c = ch;
        p = pr;
        this.admin = admin;
    }
}
