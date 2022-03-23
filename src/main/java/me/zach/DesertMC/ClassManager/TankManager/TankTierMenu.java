package me.zach.DesertMC.ClassManager.TankManager;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.Utils.Config.ConfigUtils;

public class TankTierMenu {
	Player player = null;
	public TankTierMenu(Player player) {
		
		this.player = player;
	}
	
	Inventory inventory;


	public void openInventory() {
		player.openInventory(getInventory(false));
	}

	public Inventory getInventory(boolean ghost){
		inventory = DesertMain.getInstance.getServer().createInventory(null, 27, ghost ? "Tank Class Rewards" : "Tank Class");
		for(int i = 0;i<27;i++) {
			ItemStack empty = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15);
			ItemMeta em = empty.getItemMeta();
			em.setDisplayName(" ");
			empty.setItemMeta(em);
			inventory.setItem(i, empty);
		}

		ItemStack nselected = new ItemStack(Material.STAINED_GLASS, 1, (byte) 5);

		ItemMeta nsm = nselected.getItemMeta();
		nsm.setDisplayName(ChatColor.GREEN + "Tank Class");
		ArrayList<String> nsml = new ArrayList<String>();

		if(!ghost) nsml.add(ChatColor.YELLOW + "Click " + ChatColor.DARK_GRAY + "to select the " + ChatColor.YELLOW + "Tank " + ChatColor.DARK_GRAY + "Class!");
		else nsml.add(ChatColor.YELLOW + "Visit Kothy at the Cafe!");
		nsm.setLore(nsml);
		nselected.setItemMeta(nsm);

		ItemStack selected = new ItemStack(Material.STAINED_CLAY, 1, (byte) 5);
		ItemMeta selectedMeta = selected.getItemMeta();
		ArrayList<String> sml = new ArrayList<>();


		selectedMeta.setDisplayName(ChatColor.DARK_GREEN + "Tank Class");
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

			ymeta.setDisplayName(ChatColor.YELLOW + "Tank Tier " + level);
			ylore.add(ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "Current Level");



			rmeta.setDisplayName(ChatColor.RED + "Tank Tier " + level);


			gmeta.setDisplayName(ChatColor.GREEN + "Tank Tier " + level);


			switch (level) {
				case 1:
					glore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					glore.add(ChatColor.GREEN + " - Resistance 1 for 3s on kill.");
					ylore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					ylore.add(ChatColor.YELLOW + " - Resistance 1 for 3s on kill.");
					rlore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					rlore.add(ChatColor.RED + " - Resistance 1 for 3s on kill.");
					//add level 1 rewards
					break;

				case 2:
					glore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					glore.add(ChatColor.GREEN + " - Unlock the " + ChatColor.BOLD + "Tank Shop.");
					glore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "SHOP UNLOCK" + ChatColor.GREEN + " - Protein Snack");
					ylore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					ylore.add(ChatColor.YELLOW + " - Unlock the " + ChatColor.BOLD + "Tank Shop.");
					ylore.add(ChatColor.YELLOW + "" + ChatColor.BOLD + "SHOP UNLOCK" + ChatColor.YELLOW + " - Protein Snack");
					rlore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					rlore.add(ChatColor.RED + " - Unlock the " + ChatColor.BOLD + "Tank Shop.");
					rlore.add(ChatColor.RED + "" + ChatColor.BOLD + "SHOP UNLOCK" + ChatColor.RED + " - Protein Snack");
					//add level 2 rewards
					break;
				case 3:
					glore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");

					glore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "SHOP UNLOCK " + ChatColor.GREEN + "- Bludgeon");

					ylore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");

					ylore.add(ChatColor.YELLOW + "" + ChatColor.BOLD + "SHOP UNLOCK " + ChatColor.YELLOW + "- Bludgeon");

					rlore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");

					rlore.add(ChatColor.RED + "" + ChatColor.BOLD + "SHOP UNLOCK " + ChatColor.RED + "- Bludgeon");
					break;
				case 4:
					glore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					glore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "SHOP UNLOCK" + ChatColor.GREEN + " - Turtle Enchantment - Tier I & II");
					ylore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					ylore.add(ChatColor.YELLOW + "" + ChatColor.BOLD + "SHOP UNLOCK" + ChatColor.YELLOW + " - Turtle Enchantment - Tier I & II");
					rlore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					rlore.add(ChatColor.RED + "" + ChatColor.BOLD + "SHOP UNLOCK" + ChatColor.RED + " - Turtle Enchantment - Tier I & II");
					break;
				case 5:

					glore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					glore.add(ChatColor.GREEN + " - Vengance");
					glore.add(ChatColor.DARK_GRAY + " Slowness 1 and Weakness 4 to your killer for 2s on death.");
					glore.add(ChatColor.DARK_GRAY + " Spawns ambient, threatening purple particles");
					glore.add(ChatColor.GREEN + " - Traveller");
					glore.add(ChatColor.GREEN + " For every 100 unique blocks travelled, take an extra");
					glore.add(ChatColor.GREEN + " 2% less damage. Resets upon death.");

					ylore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					ylore.add(ChatColor.YELLOW + " - Traveller");
					ylore.add(ChatColor.YELLOW + " For every 100 unique blocks travelled, take an extra");
					ylore.add(ChatColor.YELLOW + " 2% less damage. Resets upon death.");
					ylore.add(ChatColor.YELLOW + " - Vengance");
					ylore.add(ChatColor.DARK_GRAY + " Slowness 1 and Weakness 4 to your killer for 2s on death.");
					ylore.add(ChatColor.DARK_GRAY + " Spawns ambient, threatening purple particles");

					rlore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					glore.add(ChatColor.GREEN + " - Traveller");
					glore.add(ChatColor.GREEN + " For every 100 unique blocks travelled, take an extra");
					glore.add(ChatColor.GREEN + " 2% less damage. Resets upon death.");
					rlore.add(ChatColor.RED + " - Vengance");
					rlore.add(ChatColor.DARK_GRAY + " Slowness 1 and Weakness 4 to your killer for 2s on death.");
					rlore.add(ChatColor.DARK_GRAY + " Spawns ambient, threatening purple particles");
					glore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "SHOP UNLOCK " + ChatColor.GREEN + "- Stubborn Boots");
					glore.add(ChatColor.DARK_GRAY + "While wearing, you are immune to the effects of the Magic Wand.");

					ylore.add(ChatColor.YELLOW + "" + ChatColor.BOLD + "SHOP UNLOCK " + ChatColor.YELLOW + "- Stubborn Boots");
					ylore.add(ChatColor.DARK_GRAY + "While wearing, you are immune to the effects of the Magic Wand.");

					rlore.add(ChatColor.RED + "" + ChatColor.BOLD + "SHOP UNLOCK " + ChatColor.RED + "- Stubborn Boots");
					rlore.add(ChatColor.DARK_GRAY + "While wearing, you are immune to the effects of the Magic Wand.");
					break;
				case 6:
					ylore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					ylore.add(ChatColor.YELLOW + "" + ChatColor.BOLD + "SHOP UNLOCK" + ChatColor.YELLOW + " - Stomper");
					ylore.add(ChatColor.YELLOW + "" + ChatColor.BOLD + "SHOP UNLOCK" + ChatColor.YELLOW + " - Turtle Enchantment - Tier III");
					glore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					glore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "SHOP UNLOCK" + ChatColor.GREEN + " - Stomper");
					glore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "SHOP UNLOCK" + ChatColor.GREEN + " - Turtle Enchantment - Tier III");
					rlore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					rlore.add(ChatColor.RED + "" + ChatColor.BOLD + "SHOP UNLOCK" + ChatColor.RED + " - Stomper");
					rlore.add(ChatColor.RED + "" + ChatColor.BOLD + "SHOP UNLOCK" + ChatColor.RED + " - Turtle Enchantment - Tier III");
					break;
				case 7:
					glore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					glore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "SHOP UNLOCK " + ChatColor.GREEN + "- Cruel Blow Enchantment - Tier I & II");
					ylore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					ylore.add(ChatColor.YELLOW + "" + ChatColor.BOLD + "SHOP UNLOCK " + ChatColor.YELLOW + "- Cruel Blow Enchantment - Tier I & II");
					rlore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					rlore.add(ChatColor.RED + "" + ChatColor.BOLD + "SHOP UNLOCK " + ChatColor.RED + "- Cruel Blow Enchantment - Tier I & II");
					break;
				case 8:
					glore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					glore.add(ChatColor.GREEN + " - +5% damage while crouching.");
					glore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "SHOP UNLOCK " + ChatColor.GREEN + "- Cruel Blow Enchantment - Tier III");
					ylore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					ylore.add(ChatColor.YELLOW + " - +5% damage while crouching.");
					ylore.add(ChatColor.YELLOW + "" + ChatColor.BOLD + "SHOP UNLOCK " + ChatColor.YELLOW + "- Cruel Blow Enchantment - Tier III");
					rlore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					rlore.add(ChatColor.RED + " - +5% damage while crouching.");
					rlore.add(ChatColor.RED + "" + ChatColor.BOLD + "SHOP UNLOCK " + ChatColor.RED + "- Cruel Blow Enchantment - Tier III");
					break;
				case 9:
					glore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					glore.add(ChatColor.RED + "F" + ChatColor.GOLD + "A" + ChatColor.YELLOW + "L" + ChatColor.GREEN + "L" + ChatColor.BLUE + "E" + ChatColor.LIGHT_PURPLE + "N" + ChatColor.RED + " CHESTPLATE");
					ylore.add(ChatColor.DARK_GRAY + "Rewards Summary: ");
					ylore.add(ChatColor.RED + "F" + ChatColor.GOLD + "A" + ChatColor.YELLOW + "L" + ChatColor.GREEN + "L" + ChatColor.BLUE + "E" + ChatColor.LIGHT_PURPLE + "N" + ChatColor.RED + " CHESTPLATE");
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
			if(ConfigUtils.getLevel("tank",player) > level) {
				inventory.setItem(i, greenlevel);
			} else if(ConfigUtils.getLevel("tank",player) == level) {
				inventory.setItem(i, yellowlevel);
			} else if(ConfigUtils.getLevel("tank",player) < level) {
				inventory.setItem(i, redlevel);
			}
		}
		if(ConfigUtils.findClass(player).equalsIgnoreCase("tank")){
			inventory.setItem(22,selected);
		}else{
			inventory.setItem(22,nselected);
		}
		return inventory;
	}
}
