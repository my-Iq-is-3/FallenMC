package me.zach.DesertMC.GameMechanics.hitbox.hitboxes;

import me.zach.DesertMC.GameMechanics.hitbox.Hitbox;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.HashMap;
import java.util.Map;

public class CircleHitbox implements Hitbox {
    Location center;
    int radius;

    public CircleHitbox(Location center, int radius){
        this.center = center;
        this.radius = radius;
    }

    public CircleHitbox(Map<String,Object> map){
        center = (Location) map.get("center");
        radius = (int) map.get("radius");
    }

    public boolean isInside(Location l) {
        return (l.distanceSquared(center) <= radius * radius);
    }

    @Override
    public Map<String, Object> serialize() {
        HashMap<String,Object> map = new HashMap<>();
        map.put("center",center);
        map.put("radius",radius);
        return map;
    }
}
