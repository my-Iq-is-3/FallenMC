package me.zach.DesertMC.GameMechanics.hitbox.hitboxes;

import me.zach.DesertMC.GameMechanics.hitbox.Hitbox;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public class OneDimensionalHitbox implements Hitbox {
    String axis;
    double from;
    double to;
    public boolean isInside(Location l){
        if(axis.equals("x")){
            return l.getX() <= from && l.getX() >= to;
        }else if(axis.equals("y")){
            return l.getY() <= from && l.getY() >= to;
        }else if(axis.equals("z")){
            return l.getZ() <= from && l.getZ() >= to;
        }else return false;
    }

    public Map<String, Object> serialize(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("axis", axis);
        map.put("from", from);
        map.put("to", to);
        return map;
    }

    public OneDimensionalHitbox(Map<String, Object> serialized){
        this.from = (double) serialized.get("from");
        this.to = (double) serialized.get("to");
        this.axis = (String) serialized.get("axis");
    }

    public OneDimensionalHitbox(String axis, double from, double to){
        this.axis = axis;
        this.from = from;
        this.to = to;
    }
}
