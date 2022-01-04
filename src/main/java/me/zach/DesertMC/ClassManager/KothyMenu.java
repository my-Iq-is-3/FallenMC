package me.zach.DesertMC.ClassManager;

import java.util.ArrayList;

import me.zach.DesertMC.Utils.Config.ConfigUtils;
import me.zach.DesertMC.Utils.MiscUtils;
import me.zach.DesertMC.Utils.StringUtils.StringUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import me.zach.DesertMC.DesertMain;
import net.md_5.bungee.api.ChatColor;

public class KothyMenu {
	Plugin main = DesertMain.getPlugin(DesertMain.class);
	Player player;
	public KothyMenu(Player player) {
		this.player = player;
	}
	public Inventory getInventory() {
			Inventory kot = main.getServer().createInventory(null, 9, "Kothy's Selector");
			ItemStack empty = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15);
			ItemMeta em = empty.getItemMeta();
			em.setDisplayName(" ");
			empty.setItemMeta(em);
			
			for(int i=0;i<9;i++) {
				kot.setItem(i, empty);
			}
			
			//----------------------------------------------
			ItemStack traitSelector = MiscUtils.generateItem(Material.NETHER_STAR, ChatColor.YELLOW + "Trait Viewer", StringUtil.wrapLore(ChatColor.DARK_GRAY + "Click me to open the trait menu"), (byte) -1, 1, "TRAITS_VIEWER");
			kot.setItem(2, traitSelector);
			
			//-----------------------------------------------
			
			
			ItemStack classSelector = new ItemStack(Material.POTION);
			ItemMeta classS = classSelector.getItemMeta();
			classS.setDisplayName(ChatColor.GREEN + "Class Selector");
			ArrayList<String> perkLore = new ArrayList<>();
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
			if(ConfigUtils.classesMaxed(player)){
				ItemStack risenBossItem = MiscUtils.generateItem(Material.EYE_OF_ENDER, ChatColor.GOLD + "Risen Boss", StringUtil.wrapLore(ChatColor.DARK_GRAY + "Click me to transform into a Risen Boss"), (byte) -1, 1, "RISEN_MENU");
				kot.setItem(0, risenBossItem);
			}
			return kot;
	}
}

