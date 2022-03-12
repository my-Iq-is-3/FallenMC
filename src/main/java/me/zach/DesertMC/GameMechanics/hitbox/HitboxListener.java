package me.zach.DesertMC.GameMechanics.hitbox;

import org.bukkit.Location;

public class HitboxListener {
    public static boolean isInSpawn(Location l){
        Hitbox spawn = HitboxManager.get("spawn");
        return spawn != null && spawn.isInside(l);
    }

    public static boolean isInCafe(Location l){
        Hitbox cafeBlob = HitboxManager.get("cafe");
        return cafeBlob != null && cafeBlob.isInside(l);
    }

    public static boolean isInSafeZone(Location location){
        return isInCafe(location) || isInSpawn(location);
    }
}