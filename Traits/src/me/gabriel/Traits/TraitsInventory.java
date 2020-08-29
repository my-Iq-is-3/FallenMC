package me.gabriel.Traits;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class TraitsInventory implements Listener {
	private static Plugin plugin = Bukkit.getPluginManager().getPlugin("Traits");
	private static Plugin gemsPl = Bukkit.getPluginManager().getPlugin("Fallen");

	public static void initializeTraits(Player player) {
		plugin.getConfig().set(player.getUniqueId().toString() + ".trait.speed.bonus", 0);
		plugin.getConfig().set(player.getUniqueId().toString() + ".trait.speed.level", 0);
		plugin.getConfig().set(player.getUniqueId().toString() + ".trait.health.bonus", 0);
		plugin.getConfig().set(player.getUniqueId().toString() + ".trait.health.level", 0);
		plugin.getConfig().set(player.getUniqueId().toString() + ".trait.attack.bonus", 0);
		plugin.getConfig().set(player.getUniqueId().toString() + ".trait.attack.level", 0);
		plugin.getConfig().set(player.getUniqueId().toString() + ".trait.defense.bonus", 0);
		plugin.getConfig().set(player.getUniqueId().toString() + ".trait.defense.level", 0);
		plugin.getConfig().set(player.getUniqueId() + ".traittokens", 0);
		plugin.getConfig().set(player.getUniqueId().toString() + ".defense.ttstonext", 1);
		plugin.getConfig().set(player.getUniqueId().toString() + ".defense.gemstonext", 250);
		plugin.getConfig().set(player.getUniqueId().toString() + ".speed.ttstonext", 1);
		plugin.getConfig().set(player.getUniqueId().toString() + ".speed.gemstonext", 250);
		plugin.getConfig().set(player.getUniqueId().toString() + ".health.ttstonext", 1);
		plugin.getConfig().set(player.getUniqueId().toString() + ".health.gemstonext", 250);
		plugin.getConfig().set(player.getUniqueId().toString() + ".attack.ttstonext", 1);
		plugin.getConfig().set(player.getUniqueId().toString() + ".attack.gemstonext", 250);
		plugin.saveConfig();

	}

	public static ItemStack createTrait(String displayname, Material material) {
		ItemStack trait = new ItemStack(material);
		ItemMeta meta = trait.getItemMeta();
		meta.setDisplayName(ChatColor.AQUA + displayname);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(ChatColor.YELLOW + "Click to open the trait upgrade menu for " + displayname);
		meta.setLore(lore);
		trait.setItemMeta(meta);
		return trait;
	}

	public static void openTraitInventory(Player player) {
		Inventory i = plugin.getServer().createInventory(null, 27, "Traits Menu");
		for (int j = 0; j < 27; j++) {
			ItemStack empty = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
			ItemMeta em = empty.getItemMeta();
			em.setDisplayName(" ");
			empty.setItemMeta(em);
			i.setItem(j, empty);

		}

		i.setItem(10, createTrait("Health", Material.REDSTONE_BLOCK));
		i.setItem(12, createTrait("Speed", Material.NETHER_STAR));
		i.setItem(14, createTrait("Defense", Material.DIAMOND_CHESTPLATE));
		i.setItem(16, createTrait("Attack", Material.DIAMOND_SWORD));

		player.openInventory(i);
	}

	public static void openSpecificTraitInventory(String trait, Player player, Material material) {
		int Bonus = plugin.getConfig().getInt(player.getUniqueId().toString() + ".trait." + trait + ".bonus");
		int Level = plugin.getConfig().getInt(player.getUniqueId().toString() + ".trait." + trait + ".level");
		int NextLevel = Level + 1;
		int NextBonus = Bonus + 3;

		// Stats item
		ItemStack statsitem = new ItemStack(material);
		ItemMeta statsmeta = statsitem.getItemMeta();
		statsmeta.setDisplayName(
				ChatColor.GREEN + trait.substring(0, 1).toUpperCase() + trait.substring(1) + " Trait Stats");
		statsmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		ArrayList<String> statslore = new ArrayList<String>();
		statslore.add(ChatColor.YELLOW + "Current trait bonus: " + ChatColor.BLUE + Bonus + "% " + ChatColor.YELLOW
				+ "better " + trait);
		statslore.add(ChatColor.YELLOW + "Level: " + ChatColor.BLUE + Level);
		statslore.add(ChatColor.YELLOW + "Max level: " + ChatColor.BLUE + Level + "/20");
		statsmeta.setLore(statslore);
		statsitem.setItemMeta(statsmeta);

		// Upgrade item

		ItemStack upgradeitem = new ItemStack(Material.DIAMOND);
		ItemMeta upgrademeta = upgradeitem.getItemMeta();
		upgrademeta.setDisplayName(ChatColor.GREEN + "Train Trait");
		upgrademeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		ArrayList<String> upgradelore = new ArrayList<String>();

		if (plugin.getConfig().getInt(player.getUniqueId().toString() + ".trait." + trait + ".level") == 20) {
			upgrademeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, false);
			upgrademeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			upgrademeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			upgradelore.add(ChatColor.GOLD + "You have maxed out this trait!");
			plugin.getConfig().set(player.getUniqueId().toString() + "." + trait + ".canupgrade", false);
			plugin.saveConfig();

		} else {
			upgradelore.add(ChatColor.YELLOW + "Materials required to train this trait to level " + ChatColor.BLUE
					+ NextLevel + ChatColor.YELLOW + ": ");
			upgradelore.add(ChatColor.BLUE + ""
					+ plugin.getConfig().getInt(player.getUniqueId().toString() + "." + trait + ".ttstonext")
					+ ChatColor.YELLOW + " Trait Tokens, " + ChatColor.BLUE
					+ plugin.getConfig().getInt(player.getUniqueId().toString() + "." + trait + ".gemstonext")
					+ ChatColor.GREEN + " Gems");
			upgradelore.add(ChatColor.YELLOW + "Level " + ChatColor.BLUE + NextLevel + ChatColor.YELLOW
					+ " TOTAL rewards: " + NextBonus + "% " + ChatColor.YELLOW + "better " + trait + ".");

			if (gemsPl.getConfig().getInt("players." + player.getUniqueId().toString() + ".balance") >= plugin.getConfig()
					.getInt(player.getUniqueId().toString() + "." + trait + ".gemstonext")
					&& plugin.getConfig().getInt(player.getUniqueId().toString() + ".traittokens") >= plugin.getConfig()
							.getInt(player.getUniqueId() + "." + trait + ".ttstonext")) {
				upgradelore
						.add(ChatColor.YELLOW + "" + ChatColor.BOLD + "You have enough materials to train this trait!");
				upgradelore.add(ChatColor.YELLOW + "" + ChatColor.BOLD + "Click to upgrade!");
				plugin.getConfig().set(player.getUniqueId().toString() + "." + trait + ".canupgrade", true);
			} else {
				upgradelore.add(ChatColor.RED + "You do not have the required materials!");
				plugin.getConfig().set(player.getUniqueId().toString() + "." + trait + ".canupgrade", false);

			}
			plugin.saveConfig();
		}
		upgrademeta.setLore(upgradelore);
		upgradeitem.setItemMeta(upgrademeta);

		// TODO Autotrait

		ItemStack autotraititem = new ItemStack(Material.BARRIER);
		ItemMeta autotraitmeta = autotraititem.getItemMeta();
		autotraitmeta.setDisplayName(ChatColor.RED + "Auto Trait");
		autotraitmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		ArrayList<String> autotraitlore = new ArrayList<String>();
		autotraitlore.add(ChatColor.YELLOW + "This feature is coming in a later update!");
		autotraitmeta.setLore(autotraitlore);
		autotraititem.setItemMeta(autotraitmeta);

		// Trait Token Item
		ItemStack traittokenitem = new ItemStack(Material.DOUBLE_PLANT);
		ItemMeta traittokenmeta = traittokenitem.getItemMeta();
		traittokenmeta.setDisplayName(ChatColor.GREEN + "Trait Tokens: " + ChatColor.GREEN
				+ plugin.getConfig().getInt(player.getUniqueId() + ".traittokens"));
		traittokenitem.setItemMeta(traittokenmeta);

		// Gems item
		ItemStack gemsitem = new ItemStack(Material.EMERALD);
		ItemMeta gemsmeta = traittokenitem.getItemMeta();
		gemsmeta.setDisplayName(ChatColor.GREEN + "Gems: " + ChatColor.GREEN
				+ gemsPl.getConfig().getInt("players." + player.getUniqueId() + ".balance"));
		gemsitem.setItemMeta(gemsmeta);

		Inventory i = plugin.getServer().createInventory(null, 36,
				trait.substring(0, 1).toUpperCase() + trait.substring(1) + " Trait");

		for (int j = 0; j < 36; j++) {
			ItemStack empty = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
			ItemMeta em = empty.getItemMeta();
			em.setDisplayName(" ");
			empty.setItemMeta(em);
			i.setItem(j, empty);

		}

		i.setItem(11, autotraititem);
		i.setItem(13, statsitem);
		i.setItem(35, traittokenitem);
		i.setItem(15, upgradeitem);
		i.setItem(27, gemsitem);

		player.openInventory(i);

	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		Inventory i = event.getClickedInventory();
		ItemStack item = event.getCurrentItem();
		if (i == null) {
			return;
		}
		if (i.getTitle().equalsIgnoreCase("Traits Menu")) {
			event.setCancelled(true);
			if (item.getType().equals(Material.NETHER_STAR)) {
				openSpecificTraitInventory("speed", player, Material.NETHER_STAR);
				return;
			}

			if (item.getType().equals(Material.REDSTONE_BLOCK)) {
				openSpecificTraitInventory("health", player, Material.REDSTONE_BLOCK);
				return;
			}

			if (item.getType().equals(Material.DIAMOND_CHESTPLATE)) {
				openSpecificTraitInventory("defense", player, Material.DIAMOND_CHESTPLATE);
				return;
			}

			if (item.getType().equals(Material.DIAMOND_SWORD)) {
				openSpecificTraitInventory("attack", player, Material.DIAMOND_SWORD);

				return;
			}
		}
		if (i.getTitle().equalsIgnoreCase("Health Trait") || i.getTitle().equalsIgnoreCase("Speed Trait")
				|| i.getTitle().equalsIgnoreCase("Defense Trait") || i.getTitle().equalsIgnoreCase("Attack Trait")) {
			event.setCancelled(true);
			String traitNoLowercase = i.getTitle().replaceAll(" Trait", "");
			String trait = traitNoLowercase.substring(0, 1).toLowerCase() + traitNoLowercase.substring(1);
			int Level = plugin.getConfig().getInt(player.getUniqueId().toString() + ".trait." + trait + ".level");

			if (item.getType().equals(Material.DIAMOND)) {
				if (plugin.getConfig().getBoolean(player.getUniqueId().toString() + "." + trait + ".canupgrade")) {
					gemsPl.getConfig().set("players." + player.getUniqueId().toString() + ".balance",
							gemsPl.getConfig().getInt("players" + player.getUniqueId().toString() + ".balance") - plugin.getConfig()
									.getInt(player.getUniqueId().toString() + "." + trait + ".gemstonext"));
					plugin.getConfig().set(player.getUniqueId().toString() + ".traittokens",
							plugin.getConfig().getInt(player.getUniqueId().toString() + ".traittokens") - plugin
									.getConfig().getInt(player.getUniqueId().toString() + "." + trait + ".ttstonext"));
					plugin.getConfig().set(player.getUniqueId().toString() + ".trait." + trait + ".bonus",
							plugin.getConfig().getInt(player.getUniqueId().toString() + ".trait." + trait + ".bonus")
									+ 2);
					plugin.getConfig().set(player.getUniqueId().toString() + ".trait." + trait + ".level",
							plugin.getConfig().getInt(player.getUniqueId().toString() + ".trait." + trait + ".level")
									+ 1);
					if(trait.equals("health") || trait.equals("speed")) new Events().forHSetSsetAndInit(player);


					plugin.saveConfig();

					Level = Level + 1;

					plugin.getConfig().set(player.getUniqueId().toString() + "." + trait + ".gemstonext",
							Level * 250 + 250);
					plugin.saveConfig();
					player.sendMessage(ChatColor.GREEN + "Trait trained to level " + plugin.getConfig()
							.getInt(player.getUniqueId().toString() + ".trait." + trait + ".level"));

					/* pls don't get mad i tried a for loop it didn't work, got frustrated so i did
					 the ifs manually*/

					if (Level >= 16) {

						plugin.getConfig().set(player.getUniqueId().toString() + "." + trait + ".ttstonext", 3);
						plugin.saveConfig();
						if (trait.equalsIgnoreCase("health")) {
							openSpecificTraitInventory(trait, player, Material.REDSTONE_BLOCK);
							if (Level == 20) {
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 1);
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 2);
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 3);
								player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "TRAIT MAXED!");
							} else {
								player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 2);

							}
						}
						if (trait.equalsIgnoreCase("speed")) {
							openSpecificTraitInventory(trait, player, Material.NETHER_STAR);
							if (Level == 20) {
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 1);
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 2);
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 3);
								player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "TRAIT MAXED!");
							} else {
								player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 2);

							}
						}
						if (trait.equalsIgnoreCase("attack")) {
							openSpecificTraitInventory(trait, player, Material.DIAMOND_SWORD);
							if (Level == 20) {
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 1);
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 2);
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 3);
								player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "TRAIT MAXED!");
							} else {
								player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 2);

							}
						}
						if (trait.equalsIgnoreCase("defense")) {
							openSpecificTraitInventory(trait, player, Material.DIAMOND_CHESTPLATE);
							if (Level == 20) {
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 1);
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 2);
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 3);
								player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "TRAIT MAXED!");
							} else {
								player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 2);

							}
						}
						return;
					}
					if (Level >= 12) {

						plugin.getConfig().set(player.getUniqueId().toString() + "." + trait + ".ttstonext", 2);
						plugin.saveConfig();
						if (trait.equalsIgnoreCase("health")) {
							openSpecificTraitInventory(trait, player, Material.REDSTONE_BLOCK);
							if (Level == 20) {
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 1);
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 2);
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 3);
								player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "TRAIT MAXED!");
							} else {
								player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 2);

							}
						}
						if (trait.equalsIgnoreCase("speed")) {
							openSpecificTraitInventory(trait, player, Material.NETHER_STAR);
							if (Level == 20) {
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 1);
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 2);
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 3);
								player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "TRAIT MAXED!");
							} else {
								player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 2);

							}
						}
						if (trait.equalsIgnoreCase("attack")) {
							openSpecificTraitInventory(trait, player, Material.DIAMOND_SWORD);
							if (Level == 20) {
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 1);
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 2);
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 3);
								player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "TRAIT MAXED!");
							} else {
								player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 2);

							}
						}
						if (trait.equalsIgnoreCase("defense")) {
							openSpecificTraitInventory(trait, player, Material.DIAMOND_CHESTPLATE);
							if (Level == 20) {
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 1);
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 2);
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 3);
								player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "TRAIT MAXED!");
							} else {
								player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 2);

							}
						}
						return;
					}
					if (Level >= 8) {

						plugin.getConfig().set(player.getUniqueId().toString() + "." + trait + ".ttstonext", 2);
						plugin.saveConfig();
						if (trait.equalsIgnoreCase("health")) {
							openSpecificTraitInventory(trait, player, Material.REDSTONE_BLOCK);
							if (Level == 20) {
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 1);
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 2);
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 3);
								player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "TRAIT MAXED!");
							} else {
								player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 2);

							}
						}
						if (trait.equalsIgnoreCase("speed")) {
							openSpecificTraitInventory(trait, player, Material.NETHER_STAR);
							if (Level == 20) {
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 1);
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 2);
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 3);
								player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "TRAIT MAXED!");
							} else {
								player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 2);

							}
						}
						if (trait.equalsIgnoreCase("attack")) {
							openSpecificTraitInventory(trait, player, Material.DIAMOND_SWORD);
							if (Level == 20) {
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 1);
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 2);
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 3);
								player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "TRAIT MAXED!");
							} else {
								player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 2);

							}
						}
						if (trait.equalsIgnoreCase("defense")) {
							openSpecificTraitInventory(trait, player, Material.DIAMOND_CHESTPLATE);
							if (Level == 20) {
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 1);
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 2);
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 3);
								player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "TRAIT MAXED!");
							} else {
								player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 2);

							}
						}
						return;
					}
					if (Level >= 4) {

						plugin.getConfig().set(player.getUniqueId().toString() + "." + trait + ".ttstonext", 2);
						plugin.saveConfig();
						if (trait.equalsIgnoreCase("health")) {
							openSpecificTraitInventory(trait, player, Material.REDSTONE_BLOCK);
							if (Level == 20) {
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 1);
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 2);
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 3);
								player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "TRAIT MAXED!");
							} else {
								player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 2);

							}
						}
						if (trait.equalsIgnoreCase("speed")) {
							openSpecificTraitInventory(trait, player, Material.NETHER_STAR);
							if (Level == 20) {
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 1);
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 2);
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 3);
								player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "TRAIT MAXED!");
							} else {
								player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 2);

							}
						}
						if (trait.equalsIgnoreCase("attack")) {
							openSpecificTraitInventory(trait, player, Material.DIAMOND_SWORD);
							if (Level == 20) {
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 1);
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 2);
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 3);
								player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "TRAIT MAXED!");
							} else {
								player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 2);

							}
						}
						if (trait.equalsIgnoreCase("defense")) {
							openSpecificTraitInventory(trait, player, Material.DIAMOND_CHESTPLATE);
							if (Level == 20) {
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 1);
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 2);
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 3);
								player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "TRAIT MAXED!");
							} else {
								player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 2);

							}
						}
						return;
					}
					if (Level < 4) {

						plugin.getConfig().set(player.getUniqueId().toString() + "." + trait + ".ttstonext", 1);
						plugin.saveConfig();
						if (trait.equalsIgnoreCase("health")) {
							openSpecificTraitInventory(trait, player, Material.REDSTONE_BLOCK);
							if (Level == 20) {
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 1);
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 2);
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 3);
								player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "TRAIT MAXED!");
							} else {
								player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 2);

							}
						}
						if (trait.equalsIgnoreCase("speed")) {
							openSpecificTraitInventory(trait, player, Material.NETHER_STAR);
							if (Level == 20) {
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 1);
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 2);
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 3);
								player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "TRAIT MAXED!");
							} else {
								player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 2);

							}
						}
						if (trait.equalsIgnoreCase("attack")) {
							openSpecificTraitInventory(trait, player, Material.DIAMOND_SWORD);
							if (Level == 20) {
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 1);
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 2);
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 3);
								player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "TRAIT MAXED!");
							} else {
								player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 2);

							}
						}
						if (trait.equalsIgnoreCase("defense")) {
							openSpecificTraitInventory(trait, player, Material.DIAMOND_CHESTPLATE);
							if (Level == 20) {
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 1);
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 2);
								player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 3);
								player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "TRAIT MAXED!");
							} else {
								player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 2);

							}
						}
						return;
					}

				} else {
					player.playSound(player.getLocation(), Sound.ANVIL_LAND, 10, 3);
				}
			}

		}

	}

}
