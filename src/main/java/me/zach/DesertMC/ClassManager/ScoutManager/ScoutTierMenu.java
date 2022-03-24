package me.zach.DesertMC.ClassManager.ScoutManager;

import java.util.ArrayList;

import me.zach.DesertMC.Utils.MiscUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.Utils.Config.ConfigUtils;


public class ScoutTierMenu{
	Player player;
	public ScoutTierMenu(Player player) {
		this.player = player;
	}
	
	Inventory inventory;


	public void openInventory() {
		player.openInventory(getInventory(false));
	}

	public Inventory getInventory(boolean ghost){
		inventory = DesertMain.getInstance.getServer().createInventory(null, 27, ghost ? "Scout Class Rewards" : "Scout Class");
		ItemStack empty = MiscUtils.getEmptyPane();
		for(int i = 0;i<27;i++) {
			inventory.setItem(i, empty);
		}
		ItemStack nselected = new ItemStack(Material.STAINED_GLASS, 1, (byte) 5);

		ItemMeta nsm = nselected.getItemMeta();
		nsm.setDisplayName(ChatColor.GREEN + "Scout Class");
		ArrayList<String> nsml = new ArrayList<String>();

		if(ghost) nsml.add(ChatColor.YELLOW + "Visit Kothy at the cafe!");
		else nsml.add(ChatColor.YELLOW + "Click " + ChatColor.DARK_GRAY + "to select the " + ChatColor.YELLOW + "Scout " + ChatColor.DARK_GRAY + "Class!");
		nsm.setLore(nsml);
		nselected.setItemMeta(nsm);

		ItemStack selected = new ItemStack(Material.STAINED_CLAY, 1, (byte) 5);
		ItemMeta selectedMeta = selected.getItemMeta();
		ArrayList<String> sml = new ArrayList<>();


		selectedMeta.setDisplayName(ChatColor.DARK_GREEN + "Scout Class");
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
			ArrayList<String> ylore = new ArrayList<>();
			ArrayList<String> glore = new ArrayList<>();
			ArrayList<String> rlore = new ArrayList<>();

			ymeta.setDisplayName(ChatColor.YELLOW + "Scout Tier " + level);
			ylore.add(ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "Current Level");
			rmeta.setDisplayName(ChatColor.RED + "Scout Tier " + level);


			gmeta.setDisplayName(ChatColor.GREEN + "Scout Tier " + level);


			switch (level) {
				case 1:
					glore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					glore.add(ChatColor.GREEN + " - Speed 1 for 4s on kill.");
					ylore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					ylore.add(ChatColor.YELLOW + " - Speed 1 for 4s on kill.");
					rlore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					rlore.add(ChatColor.RED + " - Speed 1 for 4s on kill.");
					//add level 1 rewards
					break;

				case 2:
					glore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					glore.add(ChatColor.GREEN + " - Unlock the " + ChatColor.BOLD + "Scout Shop.");
					glore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "SHOP UNLOCK" + ChatColor.GREEN + " - Energy Snack");
					ylore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					ylore.add(ChatColor.YELLOW + " - Unlock the " + ChatColor.BOLD + "Scout Shop.");
					ylore.add(ChatColor.YELLOW + "" + ChatColor.BOLD + "SHOP UNLOCK" + ChatColor.YELLOW + " - Energy Snack");
					rlore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					rlore.add(ChatColor.RED + " - Unlock the " + ChatColor.BOLD + "Scout Shop.");
					rlore.add(ChatColor.RED + "" + ChatColor.BOLD + "SHOP UNLOCK" + ChatColor.RED + " - Energy Snack");
					//add level 2 rewards
					break;
				case 3:
					glore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					glore.add(ChatColor.GREEN + "" + ChatColor.BOLD + " SHOP UNLOCK" + ChatColor.GREEN + " - Scout Goggles ");
					glore.add(ChatColor.DARK_GRAY + "(Diamond Helmet that while worn ");
					glore.add(ChatColor.DARK_GRAY + " provides the ability to see invisible players).");

					ylore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					ylore.add(ChatColor.YELLOW + "" + ChatColor.BOLD + " SHOP UNLOCK" + ChatColor.YELLOW + " - Scout Goggles ");
					ylore.add(ChatColor.DARK_GRAY + "(Diamond Helmet that while worn ");
					ylore.add(ChatColor.DARK_GRAY + " provides the ability to see invisible players).");

					rlore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					rlore.add(ChatColor.RED + "" + ChatColor.BOLD + " SHOP UNLOCK" + ChatColor.RED + " - Scout Goggles ");
					rlore.add(ChatColor.DARK_GRAY + "(Diamond Helmet that while worn ");
					rlore.add(ChatColor.DARK_GRAY + " provides the ability to see invisible players).");
					break;
				case 4:
					glore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					glore.add(ChatColor.GREEN + " - +2 Absorbtion hearts on kill.");
					glore.add(ChatColor.GREEN + ChatColor.BOLD.toString() + "SHOP UNLOCK " + ChatColor.GREEN + "- Anti-Focus Enchantment - Tier I & II");
					glore.add(ChatColor.GREEN + "§lSHOP UNLOCK§a - Scout Dagger");

					ylore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					ylore.add(ChatColor.YELLOW + " - +2 Absorbtion hearts on kill.");
					ylore.add(ChatColor.YELLOW + ChatColor.BOLD.toString() + "SHOP UNLOCK " + ChatColor.YELLOW + " - Anti-Focus Enchantment - Tier I & II");
					ylore.add(ChatColor.YELLOW + "§lSHOP UNLOCK§e - Scout Dagger");

					rlore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					rlore.add(ChatColor.RED + " - +2 Absorbtion hearts on kill.");
					rlore.add(ChatColor.RED + ChatColor.BOLD.toString() + "SHOP UNLOCK " + ChatColor.RED + " - Anti-Focus Enchantment - Tier I & II");
					rlore.add(ChatColor.RED + "§lSHOP UNLOCK§c - Scout Dagger");
					break;
				case 5:
					glore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					glore.add(ChatColor.GREEN + " - Traveller");
					glore.add(ChatColor.GREEN + " Every 100 unique blocks travelled, gain +2% speed.");
					glore.add(ChatColor.GREEN + "Resets on death.");
					ylore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					ylore.add(ChatColor.YELLOW + " - Traveller");
					ylore.add(ChatColor.YELLOW + " Every 100 unique blocks travelled, gain +2% speed.");
					ylore.add(ChatColor.YELLOW + " Resets on death.");
					rlore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					rlore.add(ChatColor.RED + " - Traveller");
					rlore.add(ChatColor.RED + " Every 100 unique blocks travelled, gain +2% speed.");
					rlore.add(ChatColor.RED + " Resets on death.");
					break;
				case 6:
					glore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					glore.add(ChatColor.GREEN + "" + ChatColor.BOLD + " SHOP UNLOCK " + ChatColor.GREEN + " - Scout Blade");
					glore.add(ChatColor.GREEN + ChatColor.BOLD.toString() + "SHOP UNLOCK " + ChatColor.GREEN + "- Anti-Focus Enchantment - Tier III");
					ylore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					ylore.add(ChatColor.YELLOW + "" + ChatColor.BOLD + " SHOP UNLOCK " + ChatColor.YELLOW + " - Scout Blade");
					ylore.add(ChatColor.YELLOW + ChatColor.BOLD.toString() + "SHOP UNLOCK " + ChatColor.YELLOW + "- Anti-Focus Enchantment - Tier III");
					rlore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					rlore.add(ChatColor.RED + "" + ChatColor.BOLD + " SHOP UNLOCK " + ChatColor.RED + " - Scout Blade");
					rlore.add(ChatColor.RED + ChatColor.BOLD.toString() + "SHOP UNLOCK " + ChatColor.RED + "- Anti-Focus Enchantment - Tier III");
					break;
				case 7:
					glore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					glore.add(ChatColor.GREEN + ChatColor.BOLD.toString() + "SHOP UNLOCK " + ChatColor.GREEN + "- Spirit Guard Enchantment - Tier I & II");
					ylore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					ylore.add(ChatColor.YELLOW + ChatColor.BOLD.toString() + "SHOP UNLOCK " + ChatColor.YELLOW + "- Spirit Guard Enchantment - Tier I & II");
					rlore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					rlore.add(ChatColor.RED + ChatColor.BOLD.toString() + "SHOP UNLOCK " + ChatColor.RED + "- Spirit Guard Enchantment - Tier I & II");
					break;
				case 8:
					glore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					glore.add(ChatColor.GREEN + " - Deal +15% damage while sprinting.");
					glore.add(ChatColor.GREEN + ChatColor.BOLD.toString() + "SHOP UNLOCK " + ChatColor.GREEN + "- Spirit Guard Enchantment - Tier III");
					ylore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					ylore.add(ChatColor.YELLOW + " - Deal +15% damage while sprinting.");
					ylore.add(ChatColor.YELLOW + ChatColor.BOLD.toString() + "SHOP UNLOCK " + ChatColor.YELLOW + "- Spirit Guard Enchantment - Tier III");
					rlore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					rlore.add(ChatColor.RED + " - Deal +15% damage while sprinting.");
					rlore.add(ChatColor.RED + ChatColor.BOLD.toString() + "SHOP UNLOCK " + ChatColor.RED + "- Spirit Guard Enchantment - Tier III");
					break;
				case 9:
					glore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					glore.add(ChatColor.RED + "F" + ChatColor.GOLD + "A" + ChatColor.YELLOW + "L" + ChatColor.GREEN + "L" + ChatColor.BLUE + "E" + ChatColor.LIGHT_PURPLE + "N" + ChatColor.RED + " BOOTS");
					ylore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					ylore.add(ChatColor.RED + "F" + ChatColor.GOLD + "A" + ChatColor.YELLOW + "L" + ChatColor.GREEN + "L" + ChatColor.BLUE + "E" + ChatColor.LIGHT_PURPLE + "N" + ChatColor.RED + " BOOTS");
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
			if(ConfigUtils.getLevel("scout",player) > level) {
				inventory.setItem(i, greenlevel);
			} else if(ConfigUtils.getLevel("scout",player) == level) {
				inventory.setItem(i, yellowlevel);
			} else if(ConfigUtils.getLevel("scout",player) < level) {
				inventory.setItem(i, redlevel);
			}
		}

		if(ConfigUtils.findClass(player).equalsIgnoreCase("scout")){
			inventory.setItem(22,selected);
		}else{
			inventory.setItem(22,nselected);
		}
		return inventory;
	}
}
