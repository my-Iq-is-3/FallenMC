package me.zach.DesertMC.GameMechanics.hitbox.hitboxes;

import me.zach.DesertMC.GameMechanics.hitbox.Hitbox;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlobHitbox implements Hitbox {
    public final List<Hitbox> hitboxes;
    public boolean isInside(Location l){
        for(Hitbox hitbox : hitboxes){
            if(hitbox.isInside(l)){
                return true;
            }
        }
        return false;
    }

    public Map<String, Object> serialize(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("hitboxes", hitboxes);
        return map;
    }

    /**
     * Constructor for automatic deserialization from config
     * @param serialized Raw object data
     */
    public BlobHitbox(Map<String, Object> serialized){
        this.hitboxes = (List<Hitbox>) serialized.get("hitboxes");
    }

    public BlobHitbox(){
        hitboxes = new ArrayList<>();
    }
}
