package me.zach.DesertMC.GameMechanics.NPCStructure;

import me.zach.DesertMC.Utils.MiscUtils;
import net.jitse.npclib.api.NPC;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SavedNPC implements ConfigurationSerializable {
    public static String PATH = "server.npcs";
    public final Location location;
    Class<? extends SimpleNPC> clazz;
    public final SimpleNPC npc;
    List<?> params;

    public SavedNPC(SimpleNPC npc){
        NPC npcSpawned = npc.npc;
        if(npcSpawned == null) throw new IllegalArgumentException("Cannot construct SavedNPC with unspawned SimpleNPC");
        this.location = npcSpawned.getLocation();
        this.clazz = npc.getClass();
        this.npc = npc;
        this.params = npc.params();
    }

    /**
     * Constructs a new {@link SavedNPC} directly from serialization. Only for automated use by Bukkit's {@link ConfigurationSerialization} manager.
     * @param serialized the serialized map of this object
     */
    @SuppressWarnings("unchecked")
    public SavedNPC(Map<String, Object> serialized) throws InstantiationException, IllegalAccessException, ClassNotFoundException, InvocationTargetException{
        this.clazz = (Class<? extends SimpleNPC>) Class.forName((String) serialized.get("clazz"));
        this.location = (Location) serialized.get("location");
        this.params = (List<?>) serialized.getOrDefault("params", new ArrayList<>());
        this.npc = (SimpleNPC) clazz.getConstructors()[0].newInstance(params.toArray(new Object[0]));
    }

    public static List<SavedNPC> stored(Plugin plugin){
        return MiscUtils.ensureDefault(PATH, new ArrayList<>(), plugin);
    }

    public Map<String, Object> serialize(){
        HashMap<String, Object> serialized = new HashMap<>();
        serialized.put("clazz", clazz.getName());
        serialized.put("location", location);
        serialized.put("params", params);
        return serialized;
    }
}
