package me.zach.DesertMC.Utils.RankUtils;

import me.zach.DesertMC.Prefix;
import org.bukkit.ChatColor;

public enum Rank {
    MYSTIC(Prefix.MYSTIC, ChatColor.LIGHT_PURPLE),
    SUPPORTER(Prefix.SUPPORTER, ChatColor.GREEN);
    protected final Prefix p;
    protected final ChatColor c;
    Rank(Prefix pr, ChatColor ch){
        c = ch;
        p = pr;
    }
}
