package me.gabriel.Traits;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class TraitsMain extends JavaPlugin{
	public static TraitsMain instancethis;
	private Commands commands = new Commands();
	public void onEnable() {

		instancethis = this;
		getCommand("traits").setExecutor(commands);
		getCommand("traitsconfig").setExecutor(commands);
		Bukkit.getLogger().info("Events registered?");
		Bukkit.getPluginManager().registerEvents(new TraitsInventory(), this);
		Bukkit.getPluginManager().registerEvents(new Events(), this);
		loadConfig();
		saveConfig();
	}
	public void loadConfig() {
		getConfig().options().copyDefaults(true);
		saveConfig();
	}
}
