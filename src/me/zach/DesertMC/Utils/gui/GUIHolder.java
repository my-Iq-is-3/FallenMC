package me.zach.DesertMC.Utils.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public interface GUIHolder extends InventoryHolder {

    void inventoryOpen(Player player, Inventory inventory, InventoryOpenEvent event);
    void inventoryClose(Player player, Inventory inventory, InventoryCloseEvent event);

    void inventoryClick(Player player, int slot, ItemStack clickedItem, ClickType clickType, InventoryClickEvent event);
}
