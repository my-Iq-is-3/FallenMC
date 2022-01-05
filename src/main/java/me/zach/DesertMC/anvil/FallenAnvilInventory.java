package me.zach.DesertMC.anvil;

import me.zach.DesertMC.Utils.MiscUtils;
import me.zach.DesertMC.Utils.StringUtils.StringUtil;
import me.zach.DesertMC.Utils.ench.CustomEnch;
import me.zach.DesertMC.Utils.gui.GUIHolder;
import me.zach.DesertMC.Utils.nbt.NBTUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class FallenAnvilInventory implements GUIHolder {
    Inventory inventory = Bukkit.getServer().createInventory(this, 36, "Apply Enchantments");
    ItemStack greenPane = MiscUtils.getEmptyPane(DyeColor.LIME.getData());
    ItemStack redPane = MiscUtils.getEmptyPane(DyeColor.RED.getData());
    final int buttonSlot = 22;
    ItemStack trueItem = MiscUtils.generateItem(Material.STAINED_GLASS, ChatColor.GREEN + "Click to apply!", Collections.emptyList(), DyeColor.LIME.getData(), 1);
    ItemStack falseItem = MiscUtils.generateItem(Material.STAINED_GLASS, ChatColor.RED + "Insert necessary items!", StringUtil.wrapLore(ChatColor.RED + "To apply enchantments, first insert an item and an enchanted book!"), DyeColor.RED.getData(), 1);

    ItemStack playerItem = null;
    ItemStack book = null;

    public FallenAnvilInventory(){
        for(int i = 0; i<inventory.getSize(); i++){
            inventory.setItem(i, MiscUtils.getEmptyPane());
        }
        setEdges(redPane);
        inventory.clear(12);
        inventory.clear(14);
        inventory.setItem(22, falseItem);
    }

    private void setEdges(ItemStack item){
        int size = inventory.getSize();
        for(int i = 0; i<size; i+=9) inventory.setItem(i, item);
        for(int i = 8; i<size; i+=9) inventory.setItem(i, item);
    }

    public void inventoryClose(Player player, Inventory inventory, InventoryCloseEvent event){
        Inventory playerInv = player.getInventory();
        if(playerItem != null){
            inventory.remove(playerItem);
            playerInv.addItem(playerItem);
        }
        if(book != null){
            inventory.remove(book);
            playerInv.addItem(book);
        }
    }

    public void inventoryClick(Player player, int slot, ItemStack clickedItem, ClickType clickType, InventoryClickEvent event){
        event.setCancelled(true);
        Inventory playerInv = player.getInventory();
        if(clickedItem == playerItem){
            inventory.clear(slot);
            playerInv.addItem(playerItem);
            playerItem = null;
            setEdges(redPane);
        }else if(clickedItem == book){
            inventory.clear(slot);
            playerInv.addItem(book);
            book = null;
            setEdges(redPane);
        }else if(slot == buttonSlot){
            if(book != null && playerItem != null){
                ItemStack newItem = combine();
                player.playSound(player.getLocation(), Sound.ANVIL_USE, 10, 1);
                player.getInventory().addItem(newItem);
            }
        }
    }

    public void bottomInventoryClick(Player player, Inventory playerInventory, int slot, ItemStack clickedItem, ClickType clickType, InventoryClickEvent event){
        event.setCancelled(true);
        boolean isBook = NBTUtil.getCustomAttrBoolean(clickedItem, "CAN_ENCHANT");
        boolean isPlayerItem = !isBook && NBTUtil.getCustomAttrString(clickedItem, "ID").equals("ENCHANTED_BOOK");
        if(isBook || isPlayerItem){
            playerInventory.clear(slot);
            ItemStack storedCorresponding = isBook ? book : playerItem;
            if(storedCorresponding != null){
                this.inventory.remove(storedCorresponding);
                playerInventory.addItem(storedCorresponding);
            }
            if(isBook) book = clickedItem;
            else playerItem = clickedItem;
            this.inventory.setItem(isBook ? 14 : 12, clickedItem);
            if(book != null && playerItem != null){
                this.inventory.setItem(buttonSlot, trueItem);
                setEdges(greenPane);
            }else{
                this.inventory.setItem(buttonSlot, falseItem);
                setEdges(redPane);
            }
        }
    }

    private ItemStack combine(){
        String id = NBTUtil.getCustomAttrString(book, "ENCH_ID");
        CustomEnch ench = CustomEnch.fromID(id);
        if(ench != null){
            int level = NBTUtil.getCustomAttr(book, "ENCH_LEVEL", int.class);
            ItemStack combined = ench.apply(playerItem, level);
            inventory.remove(playerItem);
            playerItem = null;
            inventory.remove(book);
            book = null;
            return combined;
        }else throw new NullPointerException("Could not parse CustomEnch '" + id + "'");
    }

    public Inventory getInventory(){
        return inventory;
    }
}
