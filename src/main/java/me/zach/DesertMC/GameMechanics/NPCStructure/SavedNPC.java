package me.zach.DesertMC.GameMechanics.NPCStructure;

import me.zach.DesertMC.CommandsPackage.NPCCommand;
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
    public final String supplier;

    public SavedNPC(Location location, String supplier){
        this.location = location;
        this.supplier = supplier;
    }

    /**
     * Constructs a new {@link SavedNPC} directly from serialization. Only for automated use by Bukkit's {@link ConfigurationSerialization} manager.
     * @param serialized the serialized map of this object
     */
    @SuppressWarnings("unchecked")
    public SavedNPC(Map<String, Object> serialized){
        this.supplier = (String) serialized.get("supplier");
        this.location = (Location) serialized.get("location");
    }

    public static List<SavedNPC> stored(Plugin plugin){
        return MiscUtils.ensureDefault(PATH, new ArrayList<>(), plugin);
    }

    public Map<String, Object> serialize(){
        HashMap<String, Object> serialized = new HashMap<>();
        serialized.put("location", location);
        serialized.put("supplier", supplier);
        return serialized;
    }

    public SimpleNPC constructNPC(){
        return NPCCommand.getNPCSupplier(supplier).get();
    }
}
