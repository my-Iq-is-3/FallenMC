package me.zach.DesertMC.GameMechanics.hitbox;

import me.zach.DesertMC.Utils.Particle.ParticleEffect;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * @author Spigot user FrozenLegend from thread https://www.spigotmc.org/threads/checking-if-location-is-in-an-area.293771/
 */
public class BoxHitbox implements Hitbox{
    public Location minLocation;
    public Location maxLocation;
//    String name;
    public BoxHitbox(Location firstPoint, Location secondPoint) {
        minLocation = new Location(firstPoint.getWorld(),
                min(firstPoint.getX(), secondPoint.getX()),
                min(firstPoint.getY(), secondPoint.getY()),
                min(firstPoint.getZ(), secondPoint.getZ()));

        maxLocation = new Location(firstPoint.getWorld(),
                max(firstPoint.getX(), secondPoint.getX()),
                max(firstPoint.getY(), secondPoint.getY()),
                max(firstPoint.getZ(), secondPoint.getZ()));
//        this.name = name;
    }

    public boolean isInside(Location loc) {

        return (loc.getX() >= minLocation.getX() &&
                loc.getY() >= minLocation.getY() &&
                loc.getZ() >= minLocation.getZ())
                &&
                (loc.getX() <= maxLocation.getX() &&
                loc.getY() <= maxLocation.getY() &&
                loc.getZ() <= maxLocation.getZ());
    }
//    @Override
//    public String getName() {
//        return name;
//    }


    public BoxHitbox(Map<String,Object> map){
//        name = (String) map.get("name");
        minLocation = (Location) map.get("minLocation");
        maxLocation = (Location) map.get("maxLocation");
    }

    @Override
    public Map<String, Object> serialize() {
        HashMap<String,Object> serialized = new HashMap<>();
        serialized.put("minLocation", minLocation);
//        serialized.put("name",name);
        serialized.put("maxLocation", maxLocation);
        return serialized;
    }
}
