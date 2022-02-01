package me.zach.DesertMC.ClassManager;

import java.util.ArrayList;

import me.zach.DesertMC.Utils.Config.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import me.zach.DesertMC.DesertMain;
import net.md_5.bungee.api.ChatColor;

public class ItemShop {
	Plugin main = DesertMain.getPlugin(DesertMain.class);
	public Inventory shop = main.getServer().createInventory(null, 36, "Shop");
	Player player;
	public ItemShop(Player player) {
		this.player = player;
	}
	
	
	
	public void updateInventory() {
		int balance = ConfigUtils.getGems(player);
		for(int i=0;i<36;i++) {
			ItemStack empty = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15);
			ItemMeta em = empty.getItemMeta();
			em.setDisplayName(" ");
			empty.setItemMeta(em);
			shop.setItem(i, empty);
		}
		ItemStack gemCount = new ItemStack(Material.EMERALD,1);
		ItemMeta gemM = gemCount.getItemMeta();
		if(balance == 1) {
			gemM.setDisplayName(ChatColor.WHITE + "You have " + ChatColor.GREEN + "1 Gem");
		} else {
			gemM.setDisplayName(ChatColor.WHITE + "You have " + ChatColor.GREEN + balance + ChatColor.GREEN + " Gems");
		}
		gemCount.setItemMeta(gemM);
		shop.setItem(35, gemCount);
		//bow
		ItemStack bow = new ItemStack(Material.BOW);
		ItemMeta bowM = bow.getItemMeta();
		bowM.setDisplayName(ChatColor.WHITE + "Bow");
		ArrayList<String> bowLore = new ArrayList<String>();
		bowLore.add(ChatColor.DARK_GRAY + "Click to buy a bow that lasts " + ChatColor.RED + "1" + ChatColor.DARK_GRAY + " life");
		bowLore.add("");
		bowLore.add(ChatColor.WHITE + "Cost:" + ChatColor.GREEN + " 100 Gems");
		if(balance >= 100) {
			bowLore.add(ChatColor.GREEN + "Click to buy!");
		} else {
			bowLore.add(ChatColor.RED + "You cannot afford this item!");
		}
		bowM.setLore(bowLore);
		bow.setItemMeta(bowM);
		shop.setItem(10, bow);
	
		
		//fishing rod
		//-----------------------------------------------------
		ItemStack frod = new ItemStack(Material.FISHING_ROD);
		ItemMeta fmeta = frod.getItemMeta();
		fmeta.setDisplayName(ChatColor.WHITE + "Fishing Rod");
		ArrayList<String> flore = new ArrayList<String>();
		flore.add(ChatColor.DARK_GRAY + "Click to buy a fishing rod that lasts " + ChatColor.RED + "10" + ChatColor.DARK_GRAY + " lives");
		flore.add("");
		flore.add(ChatColor.WHITE + "Cost:" + ChatColor.GREEN + " 50 Gems");
		if(balance >= 50) {
			flore.add(ChatColor.GREEN + "Click to buy!");
		} else {
			flore.add(ChatColor.RED + "You cannot afford this item!");
		}
		fmeta.setLore(flore);
		frod.setItemMeta(fmeta);
		shop.setItem(12, frod);
		//------------------------------------------------------
		
		
		//golden apple
		ItemStack gap = new ItemStack(Material.GOLDEN_APPLE, 1);
		ItemMeta gmeta = gap.getItemMeta();
		ArrayList<String> glore = new ArrayList<String>();
		gmeta.setDisplayName(ChatColor.GOLD + "Golden Apple");
		glore.add(ChatColor.DARK_GRAY + "A golden apple that is consumed");
		glore.add(ChatColor.DARK_GRAY + "on eat.");
		glore.add(ChatColor.RED + " -" + ChatColor.DARK_GRAY + " Regen II for 5 seconds");
		glore.add(ChatColor.RED + " -" + ChatColor.DARK_GRAY + " Absorption I for 2 minutes (" + ChatColor.YELLOW + "❤❤" + ChatColor.DARK_GRAY + ")");
		glore.add("");
		glore.add(ChatColor.WHITE + "Cost: " + ChatColor.GREEN + "75 Gems");
		if(balance >= 75) {
			glore.add(ChatColor.GREEN + "Click to buy!");
		} else {
			glore.add(ChatColor.RED + "You cannot afford this item!");
		}
		gmeta.setLore(glore);
		gap.setItemMeta(gmeta);
		shop.setItem(14, gap);
		//---------------------------------------------------------
		//arrrow
		
		ItemStack arrow = new ItemStack(Material.ARROW, 10);
		ItemMeta arrowMeta = arrow.getItemMeta();
		ArrayList<String> aLore = new ArrayList<String>();
		arrowMeta.setDisplayName(ChatColor.WHITE + "Arrow " + ChatColor.DARK_GRAY + "x10");
		arrow.setItemMeta(arrowMeta);
		addLore(arrow, 30, aLore, player);
		shop.setItem(19, arrow);
		
		//----------------------------------------------------------
		//iron package
		ItemStack itemPackage = new ItemStack(Material.IRON_BLOCK);
		ItemMeta ipackMeta = itemPackage.getItemMeta();
		ipackMeta.setDisplayName(ChatColor.WHITE + "Iron Package");
		ArrayList<String> packLore = new ArrayList<String>();
		packLore.add(ChatColor.DARK_GRAY + "Contents: ");
		packLore.add(ChatColor.RED + " -" + ChatColor.DARK_GRAY + " x1 Iron Helmet");
		packLore.add(ChatColor.RED + " -" + ChatColor.DARK_GRAY + " x1 Iron Chestplate");
		packLore.add(ChatColor.GRAY + "All items: " + ChatColor.RED + "15" + ChatColor.GRAY + " lives");
		ipackMeta.setLore(packLore);
		itemPackage.setItemMeta(ipackMeta);
		addLore(itemPackage, 250, packLore, player);
		shop.setItem(16, itemPackage);

		//diamond package
		/*ItemStack itemPackageD = new ItemStack(Material.DIAMOND_BLOCK);
		ItemMeta ipackDMeta = itemPackage.getItemMeta();
		ipackDMeta.setDisplayName(ChatColor.WHITE + "Diamond Package");
		ArrayList<String> dpackLore = new ArrayList<String>();
		dpackLore.add(ChatColor.DARK_GRAY + "Contents: ");
		dpackLore.add(ChatColor.RED + " -" + ChatColor.DARK_GRAY + " x1 Diamond Helmet");
		dpackLore.add(ChatColor.RED + " -" + ChatColor.DARK_GRAY + " x1 Diamond Chestplate");
		dpackLore.add(ChatColor.RED + " -" + ChatColor.DARK_GRAY + " x1 Diamond Sword");
		ipackDMeta.setLore(dpackLore);
		itemPackageD.setItemMeta(ipackDMeta);
		addLore(itemPackageD, 100, dpackLore, player);
		shop.setItem(25, itemPackageD);*/

		//---------------------------------------------------------------
		//iron sword
		ItemStack sword = new ItemStack(Material.IRON_SWORD);
		ItemMeta swordMeta = sword.getItemMeta();
		swordMeta.setDisplayName(ChatColor.WHITE + "Iron Sword");
		ArrayList<String> swordlore = new ArrayList<>();
		swordlore.add(ChatColor.DARK_GRAY + "An Iron sword.");
		swordlore.add(ChatColor.DARK_GRAY + "Lasts: " + ChatColor.RED + "5" + ChatColor.GRAY + " lives");
		swordMeta.setLore(swordlore);
		sword.setItemMeta(swordMeta);
		addLore(sword, 125, swordlore, player);
		shop.setItem(25, sword);
		player.openInventory(shop);
	}
	
	
	public void addLore(ItemStack item, int cost, ArrayList<String> lore, Player player) {
		lore.add("");
		lore.add(ChatColor.WHITE + "Cost: " + ChatColor.GREEN + cost + ChatColor.GREEN + " Gems");
		if(ConfigUtils.getGems(player) >= cost) {
			lore.add(ChatColor.GREEN + "Click to buy!");
		} else {
			lore.add(ChatColor.RED + "You cannot afford this item!");
		}
		ItemMeta itemMeta = item.getItemMeta();
		
		itemMeta.setLore(lore);
		item.setItemMeta(itemMeta);
	}
	
}
