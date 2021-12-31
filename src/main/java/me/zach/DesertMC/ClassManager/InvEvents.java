package me.zach.DesertMC.ClassManager;

import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.Utils.Config.ConfigUtils;
import me.zach.databank.saver.PlayerData;
import me.zach.databank.saver.SaveManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.zach.DesertMC.ClassManager.CoruManager.CorrupterTierMenu;
import me.zach.DesertMC.ClassManager.ScoutManager.ScoutTierMenu;
import me.zach.DesertMC.ClassManager.TankManager.TankTierMenu;
import me.zach.DesertMC.ClassManager.WizardManager.WizardTierMenu;



public class InvEvents implements Listener {

	@SuppressWarnings("unused")
	@EventHandler
	public void shopClick(InventoryClickEvent event) {
		ItemStack item = event.getCurrentItem();
		Inventory inv = event.getInventory();
		Player player = (Player) event.getWhoClicked();
		ItemStack[] updater = inv.getContents();
		int balance = ConfigUtils.getGems(player);

		ItemShop shop = new ItemShop(player);

		if (inv.getName().equals("Selector")) {
			
			event.setCancelled(true);
			
			
			if (item.getItemMeta().getDisplayName().equals(ChatColor.BLUE + "Item Shop")) {
				shop.updateInventory();
			} else if(item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Class Selector")) {
				ClassSelector classsel = new ClassSelector(player);
				classsel.openInventory();
			}
				
			
		}
		
		if(inv.getName().equals("Shop")) {
			
			event.setCancelled(true);
			if (item == null || !item.hasItemMeta()) {
				return;
			}
			
			
			if(item.getType().equals(Material.BOW)) {
				if(ConfigUtils.deductGems(player,100)){
					player.getInventory().addItem(new ItemStack(Material.BOW));
					shop.updateInventory();
				}
			}
				
			if(item.getType().equals(Material.ARROW)) {
				if(ConfigUtils.deductGems(player,30)){
					player.getInventory().addItem(new ItemStack(Material.ARROW,10));
					shop.updateInventory();
				}
			}
				
			if(item.getType().equals(Material.FISHING_ROD)) {
				if(ConfigUtils.deductGems(player,500)){
					player.getInventory().addItem(new ItemStack(Material.FISHING_ROD));
					shop.updateInventory();
				}
			}
				
			if(item.getType().equals(Material.GOLDEN_APPLE)) {
				if(ConfigUtils.deductGems(player,250)){
					player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE));
					shop.updateInventory();
				}
			}
				
			if(item.getType().equals(Material.IRON_BLOCK)) {
				if(ConfigUtils.deductGems(player,600)){
					player.getInventory().addItem(new ItemStack(Material.IRON_CHESTPLATE));
					player.getInventory().addItem(new ItemStack(Material.IRON_HELMET));
					shop.updateInventory();
				}
			}
				
				
				
				if(item.getType().equals(Material.IRON_SWORD)) {
						if(ConfigUtils.deductGems(player,150)){
						player.getInventory().addItem(new ItemStack(Material.IRON_SWORD));
						shop.updateInventory();
					}
				}

			
				
			
				
				
				
		}
	
	}

	@EventHandler
	public void classClick(InventoryClickEvent event) {
		Inventory classinv = event.getInventory();
		ItemStack item = event.getCurrentItem();
		Player player = (Player) event.getWhoClicked();

		if(classinv == null) {
			return;
		}



		if(classinv.getName().equals("Class Selector")) {
			event.setCancelled(true);
			/*
			 * 12: tank
			 * 14: scout
			 * 30: wizard
			 * 32: corrupter
			 */
			switch(event.getRawSlot()) {
				case 30:
					WizardTierMenu wtm = new WizardTierMenu(player);
					wtm.openInventory();
					
					break;
				case 32:
					CorrupterTierMenu ctm = new CorrupterTierMenu(player);
					ctm.openInventory();
					break;
				case 14:
					ScoutTierMenu stm = new ScoutTierMenu(player);
					stm.openInventory();
					break;
				case 12:
					TankTierMenu ttm = new TankTierMenu(player);
					ttm.openInventory();
					break;
				default:
					break;
					
			}


		}

		if(classinv.getName().equals("Wizard Class")){
			event.setCancelled(true);
			if(item.getType().equals(Material.STAINED_GLASS)){
				ConfigUtils.setClass(player, "wizard");
				new WizardTierMenu(player).openInventory();
			}
		}

		if(classinv.getName().equals("Scout Class")){
			event.setCancelled(true);
			if(item.getType().equals(Material.STAINED_GLASS)){
				ConfigUtils.setClass(player, "scout");
				new ScoutTierMenu(player).openInventory();
			}
		}

		if(classinv.getName().equals("Tank Class")){
			event.setCancelled(true);
			if(item.getType().equals(Material.STAINED_GLASS)){
				ConfigUtils.setClass(player, "tank");
				new TankTierMenu(player).openInventory();
			}
		}

		if(classinv.getName().equals("Corrupter Class")){
			event.setCancelled(true);
			if(item.getType().equals(Material.STAINED_GLASS)){
				ConfigUtils.setClass(player, "corrupter");
				new CorrupterTierMenu(player).openInventory();
			}
		}
	}
}

