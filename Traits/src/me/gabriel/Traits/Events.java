package me.gabriel.Traits;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public class Events implements Listener {
	private static Plugin plugin = Bukkit.getPluginManager().getPlugin("Traits");



	@EventHandler(priority = EventPriority.HIGHEST)
	public void forHSetSsetAndInit(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		int hBonus = plugin.getConfig().getInt(player.getUniqueId().toString() + ".trait.health.bonus");
		int sBonus = plugin.getConfig().getInt(player.getUniqueId().toString() + ".trait.speed.bonus");
		if (!Boolean.TRUE.equals(plugin.getConfig().getBoolean(player.getUniqueId().toString() + ".init"))) {
			TraitsInventory.initializeTraits(player);
			plugin.getConfig().set(player.getUniqueId().toString() + ".init", true);
			plugin.saveConfig();
		}	
		player.setMaxHealth(0.2 * hBonus + 20);
		player.setHealth(player.getMaxHealth());
		player.setWalkSpeed((0.2f / 100) * sBonus + 0.2f);
	}

	public void forHSetSsetAndInit(Player player) {
		int hBonus = plugin.getConfig().getInt(player.getUniqueId().toString() + ".trait.health.bonus");
		int sBonus = plugin.getConfig().getInt(player.getUniqueId().toString() + ".trait.speed.bonus");
		if (!Boolean.TRUE.equals(plugin.getConfig().getBoolean(player.getUniqueId().toString() + ".init"))) {
			TraitsInventory.initializeTraits(player);
			plugin.getConfig().set(player.getUniqueId().toString() + ".init", true);
			plugin.saveConfig();
		}
		player.setMaxHealth(0.2 * hBonus + 20);
		player.setHealth(player.getMaxHealth());
		player.setWalkSpeed((0.2f / 100) * sBonus + 0.2f);
	}
	

	@EventHandler(priority = EventPriority.HIGHEST)
	public void forDamageAndDefense(EntityDamageByEntityEvent event) {
		 if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
			 Player damager = (Player) event.getDamager();
			 Player damagee = (Player) event.getEntity();
			 event.setDamage((event.getDamage()/100) * plugin.getConfig().getInt(damager.getUniqueId().toString() + ".trait.attack.bonus") + event.getDamage() - ((event.getDamage()/100) * plugin.getConfig().getInt(damagee.getUniqueId().toString() + ".trait.defense.bonus")));
		 
		 }
	}
}
