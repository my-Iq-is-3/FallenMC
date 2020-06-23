package me.ench.main;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class EnchantRefineryMain extends JavaPlugin {
    public static EnchantRefineryMain instancethis;
    public void onEnable() {
        instancethis = this;
        Commands commands = new Commands();
        getCommand("testinv").setExecutor(commands);
        getCommand("givehammer").setExecutor(commands);
        getCommand("givebookdummy").setExecutor(commands);
        Bukkit.getPluginManager().registerEvents(new RefineryInventory(), this);
        loadConfig();
        saveConfig();
    }

    public void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }


}
