package me.zach.DesertMC.GameMechanics.hitbox;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.HashMap;
import java.util.Map;

public class CircleHitbox implements Hitbox{
    Location center;
    int radius;
//    String name;

    public CircleHitbox(Location center, int radius){
        this.center = center;
        this.radius = radius;
//        this.name = name;
    }

    public CircleHitbox(Map<String,Object> map){
        center = (Location) map.get("center");
        radius = (int) map.get("radius");
//        name = (String) map.get("name");
    }

    public boolean isInside(Location l) {
        return (l.distance(center) <= radius);
    }

//    @Override
//    public String getName() {
//        return name;
//    }

    @Override
    public Map<String, Object> serialize() {
        HashMap<String,Object> map = new HashMap<>();
        map.put("center",center);
        map.put("radius",radius);
//        map.put("name",name);
        return map;
    }
}
