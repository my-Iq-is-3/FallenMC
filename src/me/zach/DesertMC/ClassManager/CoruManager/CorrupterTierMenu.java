package me.zach.DesertMC.ClassManager.CoruManager;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.Utils.Config.ConfigUtils;

public class CorrupterTierMenu {
	Player player = null;
	public CorrupterTierMenu(Player player) {
		this.player = player;
	}
	
	Inventory inventory = DesertMain.getInstance.getServer().createInventory(null, 27, "Corrupter Class");

	
	public void openInventory() {
		
		for(int i = 0;i<27;i++) {
			ItemStack empty = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15);
			ItemMeta em = empty.getItemMeta();
			em.setDisplayName(" ");
			empty.setItemMeta(em);
			inventory.setItem(i, empty);
		}

		ItemStack nselected = new ItemStack(Material.STAINED_GLASS, 1, (byte) 5);

		ItemMeta nsm = nselected.getItemMeta();
		nsm.setDisplayName(ChatColor.GREEN + "Corrupter Class");
		ArrayList<String> nsml = new ArrayList<String>();

		nsml.add("");
		nsml.add(ChatColor.YELLOW + "Click " + ChatColor.DARK_GRAY + "to select the " + ChatColor.YELLOW + "Corrupter " + ChatColor.DARK_GRAY + "Class!");
		nsm.setLore(nsml);
		nselected.setItemMeta(nsm);

		ItemStack selected = new ItemStack(Material.STAINED_CLAY, 1, (byte) 5);
		ItemMeta selectedMeta = selected.getItemMeta();
		ArrayList<String> sml = new ArrayList<>();


		selectedMeta.setDisplayName(ChatColor.DARK_GREEN + "Corrupter Class");
		sml.add(ChatColor.DARK_GRAY + "Currently selected class");
		selectedMeta.setLore(sml);
		selected.setItemMeta(selectedMeta);

		for(int i=9;i<18;i++) {
			ItemStack greenlevel = new ItemStack(Material.STAINED_GLASS_PANE,1, (byte)5);
			ItemStack redlevel = new ItemStack(Material.STAINED_GLASS_PANE,1, (byte)14);
			ItemStack yellowlevel = new ItemStack(Material.STAINED_GLASS_PANE,1, (byte)4);
			
			ItemMeta ymeta = yellowlevel.getItemMeta();
			ItemMeta rmeta = redlevel.getItemMeta();
			ItemMeta gmeta = greenlevel.getItemMeta();
			int level = i-8;
			ArrayList<String> ylore = new ArrayList<String>();
			ArrayList<String> glore = new ArrayList<String>();
			ArrayList<String> rlore = new ArrayList<String>();
			
			ymeta.setDisplayName(ChatColor.YELLOW + "Corrupter Tier " + level);
			ylore.add(ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "Current Level");
			
			
			
			rmeta.setDisplayName(ChatColor.RED + "Corrupter Tier " + level);
			
			
			gmeta.setDisplayName(ChatColor.GREEN + "Corrupter Tier " + level);
			

			switch (level) {
			case 1:
				glore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
				glore.add(ChatColor.GREEN + " - Deal +10% damage for 4s on kill.");
				ylore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
				ylore.add(ChatColor.YELLOW + " - Deal +10% damage for 4s on kill.");
				rlore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
				rlore.add(ChatColor.RED + " - Deal +10% damage for 4s on kill.");
				
				break;
				
			case 2:
				glore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
				glore.add(ChatColor.GREEN + " - Unlock the " + ChatColor.BOLD + "Corrupter Shop.");
				ylore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
				ylore.add(ChatColor.YELLOW + " - Unlock the " + ChatColor.BOLD + "Corrupter Shop.");
				rlore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
				rlore.add(ChatColor.RED + " - Unlock the " + ChatColor.BOLD + "Corrupter Shop.");
				
				break;
			case 3:
				glore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
				glore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "SHOP UNLOCK" + ChatColor.GREEN + " - Volcanic Sword ");
				glore.add(ChatColor.DARK_GRAY + "(On every fifth kill, everyone around you is ");
				glore.add(ChatColor.DARK_GRAY + "shot away in a massive explosion");
				
				ylore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
				ylore.add(ChatColor.YELLOW + "" + ChatColor.BOLD + "SHOP UNLOCK" + ChatColor.YELLOW + " - Volcanic Sword");
				ylore.add(ChatColor.DARK_GRAY + "(On every fifth kill with this weapon, everyone around you is ");
				ylore.add(ChatColor.DARK_GRAY + "shot away in a massive explosion)");
				
				
				rlore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
				rlore.add(ChatColor.RED + "" + ChatColor.BOLD + "SHOP UNLOCK" + ChatColor.RED + " - Volcanic Sword ");
				rlore.add(ChatColor.DARK_GRAY + "(On every fifth kill with this weapon, everyone around you is ");
				rlore.add(ChatColor.DARK_GRAY + "shot away in a massive explosion)");
				
				break;
			case 4:
				glore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
				glore.add(ChatColor.GREEN + " - Immunity to fire and lava.");
				glore.add(ChatColor.GREEN + "§lSHOP UNLOCK " + ChatColor.GREEN + "- Corrupted Sword");
				glore.add(ChatColor.DARK_GRAY + "Has a chance to spawn " + ChatColor.GOLD + "Hellfire" + ChatColor.DARK_GRAY + " beneath the player you hit.");

				ylore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
				ylore.add(ChatColor.YELLOW + " - Immunity to fire and lava.");
				ylore.add(ChatColor.YELLOW + "§lSHOP UNLOCK " + ChatColor.YELLOW + "- Corrupted Sword");
				ylore.add(ChatColor.DARK_GRAY + "Has a chance to spawn " + ChatColor.GOLD + "Hellfire" + ChatColor.DARK_GRAY + " beneath the player you hit.");

				rlore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
				rlore.add(ChatColor.RED + " - Immunity to fire and lava.");
				rlore.add(ChatColor.RED + "§lSHOP UNLOCK " + ChatColor.RED + "- Corrupted Sword");
				rlore.add(ChatColor.DARK_GRAY + "Has a chance to spawn " + ChatColor.GOLD + "Hellfire" + ChatColor.DARK_GRAY + " beneath the player you hit.");

				break;
			case 5:
				
				glore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
				glore.add(ChatColor.GREEN + " - Permanent strength 1 in the nether biome.");

				ylore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
				ylore.add(ChatColor.YELLOW + " - Permanent strength 1 in the nether biome.");
				rlore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
				rlore.add(ChatColor.RED + " - Permanent strength 1 in the nether biome.");
				break;
			case 6:
				
				
				glore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
				glore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "SHOP UNLOCK " + ChatColor.GREEN + "- No Mercy Enchantment" + ChatColor.DARK_GRAY + " (Armor Enchantment)");
				glore.add(ChatColor.DARK_GRAY + "With this enchantment, every other kill subtracts 0.5 Health from everyone");
				glore.add(ChatColor.DARK_GRAY + "within 6 blocks.");				

				ylore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
				ylore.add(ChatColor.YELLOW + "" + ChatColor.BOLD + "SHOP UNLOCK " + ChatColor.YELLOW + "- No Mercy Enchantment" + ChatColor.DARK_GRAY + " (Armor Enchantment)");
				ylore.add(ChatColor.DARK_GRAY + "With this enchantment, every other kill subtracts 0.5 Health from everyone");
				ylore.add(ChatColor.DARK_GRAY + "within 6 blocks.");				
				
				rlore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
				rlore.add(ChatColor.RED + "" + ChatColor.BOLD + "SHOP UNLOCK " + ChatColor.RED + "- No Mercy Enchantment" + ChatColor.DARK_GRAY + " (Armor Enchantment)");
				rlore.add(ChatColor.DARK_GRAY + "With this enchantment, every other kill subtracts 0.5 Health from everyone");
				rlore.add(ChatColor.DARK_GRAY + "within 6 blocks.");
				break;
				
				
			case 7:

				glore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
				glore.add(ChatColor.GREEN + "- Heal 0.5 Hearts per second while in lava.");
				
				ylore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
				ylore.add(ChatColor.YELLOW + "- Heal 0.5 Hearts per second while in lava.");
				
				rlore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
				rlore.add(ChatColor.RED + "- Heal 0.5 Hearts per second while in lava.");
				break;
			case 8:
				glore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
				glore.add(ChatColor.GREEN + " - Deal +5% damage.");
				ylore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
				ylore.add(ChatColor.YELLOW + " - Deal +5% damage.");
				rlore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
				rlore.add(ChatColor.RED + " - Deal +5% damage.");
				
				break;
			case 9:
				glore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
				glore.add(ChatColor.RED + "F" + ChatColor.GOLD + "A" + ChatColor.YELLOW + "L" + ChatColor.GREEN + "L" + ChatColor.BLUE + "E" + ChatColor.LIGHT_PURPLE + "N" + ChatColor.RED + " LEGGINGS");
				ylore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
				ylore.add(ChatColor.RED + "F" + ChatColor.GOLD + "A" + ChatColor.YELLOW + "L" + ChatColor.GREEN + "L" + ChatColor.BLUE + "E" + ChatColor.LIGHT_PURPLE + "N" + ChatColor.RED + " LEGGINGS");
				rlore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
				rlore.add(ChatColor.RED + "???");
				break;

			}
			ymeta.setLore(ylore);
			gmeta.setLore(glore);
			rmeta.setLore(rlore);
			greenlevel.setItemMeta(gmeta);
			redlevel.setItemMeta(rmeta);
			yellowlevel.setItemMeta(ymeta);
			if(ConfigUtils.getLevel("corrupter",player) > level) {
				inventory.setItem(i, greenlevel);
			} else if(ConfigUtils.getLevel("corrupter",player) == level) {
				inventory.setItem(i, yellowlevel);
			} else if(ConfigUtils.getLevel("corrupter",player) < level) {
				inventory.setItem(i, redlevel);
			}
		}

		if(ConfigUtils.findClass(player).equalsIgnoreCase("corrupter")){
			inventory.setItem(22,selected);
		}else{
			inventory.setItem(22,nselected);
		}

		player.openInventory(inventory);
	}
	
}
