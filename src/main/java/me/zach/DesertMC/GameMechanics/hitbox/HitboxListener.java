package me.zach.DesertMC.GameMechanics.hitbox;

import org.bukkit.Location;

public class HitboxListener {
    public static boolean isInSpawn(Location l){
        return HitboxManager.get("spawn").isInside(l);
    }

    public static boolean isInCafe(Location l){
        return (HitboxManager.get("cafe1").isInside(l) || HitboxManager.get("cafe2").isInside(l));
    }
}