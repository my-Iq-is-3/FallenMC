package me.zach.DesertMC.GameMechanics.hitbox;

import me.zach.DesertMC.Utils.structs.Pair;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class HitboxListener implements Listener{
    public static boolean isInSpawn(Location l){
        return HitboxManager.get("spawn").isInside(l);
    }

    public static boolean isInCafe(Location l){
        return (HitboxManager.get("cafe1").isInside(l) && HitboxManager.get("cafe2").isInside(l));
    }


}