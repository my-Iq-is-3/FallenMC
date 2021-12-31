package me.zach.DesertMC.GameMechanics.NPCStructure;

import net.jitse.npclib.api.NPC;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.HashMap;
import java.util.Map;

public class SavedNPC implements ConfigurationSerializable {
    public static String PATH = "server.npcs";
    public final Location location;
    Class<? extends NPCSuper> clazz;
    public final NPCSuper npc;

    public SavedNPC(NPCSuper npc){
        NPC npcSpawned = npc.npc;
        if(npcSpawned == null) throw new IllegalArgumentException("Cannot construct SavedNPC with unspawned NPCSuper");
        this.location = npcSpawned.getLocation();
        this.clazz = npc.getClass();
        this.npc = npc;
    }

    /**
     * Constructs a new {@link SavedNPC} directly from serialization. Only for automated use by Bukkit's {@link ConfigurationSerialization} manager.
     * @param serialized the serialized map of this object
     */
    @SuppressWarnings("unchecked")
    public SavedNPC(Map<String, Object> serialized) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
        this.clazz = (Class<? extends NPCSuper>) Class.forName((String) serialized.get("clazz"));
        this.location = (Location) serialized.get("location");
        this.npc = clazz.newInstance();
    }

    public Map<String, Object> serialize(){
        HashMap<String, Object> serialized = new HashMap<>();
        serialized.put("clazz", clazz.getName());
        serialized.put("location", location);
        return serialized;
    }
}
