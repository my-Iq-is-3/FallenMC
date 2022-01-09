package me.zach.DesertMC.GameMechanics.hitbox;

import me.zach.DesertMC.Utils.structs.Pair;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class HitboxManager {
    public static final String PATH = "server.hitboxes";
    private static Map<String, Object> hitboxDict;

    public static void loadAll(Plugin plugin){
        if(plugin.getConfig().get(PATH) == null){
            plugin.getConfig().createSection(PATH);
        }
        ConfigurationSection section = plugin.getConfig().getConfigurationSection(PATH);
        hitboxDict = section.getValues(false);
    }

    public static void saveAll(Plugin plugin){
        FileConfiguration config = plugin.getConfig();
//        for(Map.Entry<String, Object> hitbox : hitboxDict.entrySet()){
//            config.set(hitbox.getKey(), hitbox.getValue());
//        }
        config.createSection(PATH,hitboxDict);
        plugin.saveConfig();
    }
    public static List<Pair<String, Hitbox>> getAll(){
        List<Pair<String, Hitbox>> hitboxes = new ArrayList<>();
        for(Map.Entry<String, Object> entry : hitboxDict.entrySet()){
            hitboxes.add(new Pair<>(entry.getKey(), (Hitbox) entry.getValue()));
        }
        return hitboxes;
    }
    public static void set(String name, Hitbox h){
        hitboxDict.put(name,h);
    }

    public static Hitbox get(String hitbox){
        return (Hitbox) hitboxDict.get(hitbox);
    }
}
