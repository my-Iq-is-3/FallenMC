package me.zach.DesertMC.GameMechanics.hitbox.hitboxes;

import me.zach.DesertMC.GameMechanics.hitbox.Hitbox;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.HashMap;
import java.util.Map;

public class CircleHitbox implements Hitbox {
    final Location center;
    final int radius;
    final int radiusSquared;

    public CircleHitbox(Location center, int radius){
        this.center = center;
        this.radius = radius;
        this.radiusSquared = radius * radius;
    }

    public CircleHitbox(Map<String,Object> map){
        center = (Location) map.get("center");
        radius = (int) map.get("radius");
        this.radiusSquared = radius * radius;
    }

    public boolean isInside(Location l) {
        return (l.distanceSquared(center) <= radiusSquared);
    }

    @Override
    public Map<String, Object> serialize() {
        HashMap<String,Object> map = new HashMap<>();
        map.put("center",center);
        map.put("radius",radius);
        return map;
    }
}
