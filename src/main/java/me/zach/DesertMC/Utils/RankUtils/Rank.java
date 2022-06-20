package me.zach.DesertMC.Utils.RankUtils;

import me.zach.DesertMC.Prefix;
import org.bukkit.ChatColor;

public enum Rank {
    MYSTIC(Prefix.MYSTIC, ChatColor.LIGHT_PURPLE.toString(), false),
    SUPPORTER(Prefix.SUPPORTER, ChatColor.GREEN.toString(), false),
    COOWNER(Prefix.COOWNER, ChatColor.GOLD + ChatColor.BOLD.toString(), true),
    ADMIN(Prefix.ADMIN, ChatColor.RED.toString(), true),
    MODERATOR(Prefix.MODERATOR, ChatColor.YELLOW.toString(), false, true);
    public final Prefix p;
    public final String c;
    public final boolean admin;
    private final boolean mod;

    Rank(Prefix pr, String c, boolean admin){
        this(pr, c, admin, false);
    }

    Rank(Prefix pr, String c, boolean admin, boolean mod){
        this.mod = mod;
        this.c = c;
        p = pr;
        this.admin = admin;
    }

    public boolean isMod(){
        return admin || mod;
    }
}
