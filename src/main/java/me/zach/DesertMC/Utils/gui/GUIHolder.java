package me.zach.DesertMC.Utils.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public interface GUIHolder extends InventoryHolder {
    default void inventoryOpen(Player player, Inventory inventory, InventoryOpenEvent event){}
    default void inventoryClose(Player player, Inventory inventory, InventoryCloseEvent event){}
    default void inventoryClick(Player player, int slot, ItemStack clickedItem, ClickType clickType, InventoryClickEvent event){}
    default void bottomInventoryClick(Player player, Inventory inventory, int slot, ItemStack clickedItem, ClickType clickType, InventoryClickEvent event){event.setCancelled(true);}
    default boolean cancelPickup(PlayerPickupItemEvent event){return true;}
}
