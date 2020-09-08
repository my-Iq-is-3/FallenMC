package me.zach.DesertMC.GUImanager;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import me.zach.DesertMC.DesertMain;
import net.md_5.bungee.api.ChatColor;

public class KitsOrTraits {
	Plugin main = DesertMain.getPlugin(DesertMain.class);
	Player player = null;
	public KitsOrTraits(Player player) {
		this.player = player;
	}
	public Inventory kot = main.getServer().createInventory(null, 9, "Selector");
	public void openInventory() {
			ItemStack empty = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15);
			ItemMeta em = empty.getItemMeta();
			em.setDisplayName(" ");
			empty.setItemMeta(em);
			
			for(int i=0;i<9;i++) {
	
				kot.setItem(i, empty);
			}
			
			//----------------------------------------------
			ItemStack traitSelector = new ItemStack(Material.NETHER_STAR);
			ItemMeta trait = traitSelector.getItemMeta();
			trait.setDisplayName(ChatColor.YELLOW + "Trait Viewer");
			ArrayList<String> lore = new ArrayList<String>();
			lore.add(ChatColor.DARK_GRAY + "Click me to open the trait viewer");
			trait.setLore(lore);
			traitSelector.setItemMeta(trait);
			kot.setItem(2, traitSelector);
			
			
			//-----------------------------------------------
			
			
			ItemStack classSelector = new ItemStack(Material.POTION);
			ItemMeta classS = classSelector.getItemMeta();
			classS.setDisplayName(ChatColor.GREEN + "Class Selector");
			ArrayList<String> perkLore = new ArrayList<String>();
			perkLore.add(ChatColor.DARK_GRAY + "Click me to open the class menu");
			classS.setLore(perkLore);
			classSelector.setItemMeta(classS);
			kot.setItem(4, classSelector);
			
			//------------------------------------------------
			
			ItemStack shopSelector = new ItemStack(Material.DIAMOND_CHESTPLATE);
			ItemMeta shop = shopSelector.getItemMeta();
			shop.setDisplayName(ChatColor.BLUE + "Item Shop");
			ArrayList<String> shopLore = new ArrayList<String>();
			shopLore.add(ChatColor.DARK_GRAY + "Click me to open the item shop");
			shop.setLore(shopLore);
			shopSelector.setItemMeta(shop);
			kot.setItem(6, shopSelector);
			player.openInventory(kot);
	}
}

