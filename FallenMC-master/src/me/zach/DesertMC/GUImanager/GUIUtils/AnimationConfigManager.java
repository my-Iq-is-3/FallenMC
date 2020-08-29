
package me.zach.DesertMC.GUImanager.GUIUtils;

import me.zach.DesertMC.DesertMain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class AnimationConfigManager {
    /*
    private DesertMain plugin = DesertMain.getPlugin(DesertMain.class);
    private FileConfiguration animationsCfg;
    public File animationsFile;

    public void setup() {
        if(!plugin.getDataFolder().exists()){
            plugin.getDataFolder().mkdir();
        }
        animationsFile = new File(plugin.getDataFolder(), "animations.yml");
        if(!animationsFile.exists()) {
            try {
                animationsFile.createNewFile();
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Created animations.yml successfully.");

            } catch (IOException e) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Could not create animations.yml: " + Arrays.toString(e.getStackTrace()));
            }
        }
        animationsCfg = YamlConfiguration.loadConfiguration(animationsFile);

    }

    public FileConfiguration getAnimations() {
        return animationsCfg;
    }

    public void saveAnimations() {
        try{

            animationsCfg.save(animationsFile);
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Animations saved successfully.");
        }catch(IOException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Could not save animations: " + Arrays.toString(e.getStackTrace()));
        }
        plugin.saveConfig();

    }

    public void reloadAnimations() {
        animationsCfg = YamlConfiguration.loadConfiguration(animationsFile);
    }
    */


}
