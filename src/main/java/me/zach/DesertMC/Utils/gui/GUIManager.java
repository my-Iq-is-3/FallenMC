package me.zach.DesertMC.Utils.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class GUIManager implements Listener {
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event){
        Inventory inventory = event.getInventory();
        InventoryHolder holder = event.getInventory().getHolder();
        if(holder instanceof GUIHolder) ((GUIHolder) holder).inventoryOpen((Player) event.getPlayer(), inventory, event);
    }
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event){
        Inventory inventory = event.getInventory();
        InventoryHolder holder = event.getInventory().getHolder();
        if(holder instanceof GUIHolder) ((GUIHolder) holder).inventoryClose((Player) event.getPlayer(), inventory, event);
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        Inventory inventory = event.getClickedInventory();
        if(inventory != null){
            InventoryHolder holder = inventory.getHolder();
            if(holder instanceof GUIHolder){
                ((GUIHolder) holder).inventoryClick((Player) event.getWhoClicked(),
                        event.getSlot(),
                        event.getCurrentItem(),
                        event.getClick(),
                        event);
            }else{
                Player player = (Player) event.getWhoClicked();
                Inventory topInventory = player.getOpenInventory().getTopInventory();
                if(topInventory == null) return;
                InventoryHolder topInventoryHolder = topInventory.getHolder();
                if(topInventoryHolder instanceof GUIHolder){
                    ((GUIHolder) topInventoryHolder).bottomInventoryClick((Player) event.getWhoClicked(),
                            player.getInventory(),
                            event.getSlot(),
                            event.getCurrentItem(),
                            event.getClick(),
                            event);
                }
            }
        }
    }
    @EventHandler
    public void onPickup(PlayerPickupItemEvent event){
        Player player = event.getPlayer();
        Inventory inventory = player.getOpenInventory().getTopInventory();
        if(inventory != null){
            InventoryHolder holder = inventory.getHolder();
            if(holder instanceof GUIHolder){
                event.setCancelled(((GUIHolder) holder).cancelPickup(event));
            }
        }
    }
}
