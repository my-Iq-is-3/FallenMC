package me.zach.DesertMC.ClassManager;

import me.gabriel.Traits.TraitsInventory;
import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.Utils.Config.ConfigUtils;
import me.zach.DesertMC.Utils.MiscUtils;
import me.zach.DesertMC.Utils.nbt.NBTUtil;
import me.zach.artifacts.gui.inv.ArtifactsBag;
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
import xyz.fallenmc.risenboss.main.inventories.BossActivationInventory;

import java.util.ArrayList;
import java.util.Arrays;


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

		if (inv.getName().startsWith("Kothy")) {
			event.setCancelled(true);
			String id = NBTUtil.getCustomAttrString(item, "ID");
			if (item.getItemMeta().getDisplayName().equals(ChatColor.BLUE + "Item Shop")) {
				shop.updateInventory();
			} else if(item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Class Selector")) {
				ClassSelector classsel = new ClassSelector(player);
				classsel.openInventory();
			}else if(id.equals("RISEN_MENU")){
				player.openInventory(new BossActivationInventory(player).getInventory());
			}else if(id.equals("TRAITS_VIEWER")){
				TraitsInventory.openTraitInventory(player);
			}else if(id.equals("ARTIFACTS_BAG")){
				new ArtifactsBag(player).openInv();
			}
		}
		
		if(inv.getName().equals("Shop")) {
			event.setCancelled(true);
			if (item == null || !item.hasItemMeta()) {
				return;
			}
			if(item.getType().equals(Material.BOW)) {
				if(ConfigUtils.deductGems(player,100)){
					player.getInventory().addItem(MiscUtils.generateItem(Material.BOW, "Bow", MiscUtils.asArrayList(ChatColor.GRAY + "A bow."), (byte) -1, 1, "BOW", 1));
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
				if(ConfigUtils.deductGems(player,50)){
					player.getInventory().addItem(MiscUtils.generateItem(Material.FISHING_ROD, "Fishing Rod", MiscUtils.asArrayList(ChatColor.GRAY + "A fishing rod."), (byte) -1, 1, "FISHING_ROD", 10));
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
					player.getInventory().addItem(MiscUtils.generateItem(Material.IRON_CHESTPLATE, "Iron Chestplate", new ArrayList<>(), (byte) -1, 1, "IRON_CHESTPLATE", 15));
					player.getInventory().addItem(MiscUtils.generateItem(Material.IRON_HELMET, "Iron Helmet", new ArrayList<>(), (byte) -1, 1, "IRON_HELMET", 15));
					shop.updateInventory();
				}
			}
				
				
				
				if(item.getType().equals(Material.IRON_SWORD)) {
						if(ConfigUtils.deductGems(player,150)){
						player.getInventory().addItem(MiscUtils.generateItem(Material.IRON_SWORD, "Iron Sword", MiscUtils.asArrayList(ChatColor.GRAY + "An iron sword."), (byte) -1, 1, "IRON_SWORD", 5));
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

