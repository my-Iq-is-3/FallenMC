package me.zach.DesertMC.shops;
import me.zach.DesertMC.Utils.Config.ConfigUtils;
import me.zach.DesertMC.Utils.MiscUtils;
import me.zach.DesertMC.Utils.gui.GUIHolder;
import me.zach.databank.saver.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ShopInventory implements GUIHolder {
    public static final int INV_SIZE = 54;
    Inventory inventory;
    String shopName;
    public final PlayerData data;
    final ItemStack gemsItem;
    HashMap<Integer, ShopItem> storefront = new HashMap<>(); //slot x shopitem map

    public ShopInventory(String shopName, Iterable<? extends ShopItem> items, Player player, byte glassColor, ItemStack bottomLeft){
        this.shopName = shopName;
        inventory = Bukkit.getServer().createInventory(this, INV_SIZE, shopName);
        this.data = ConfigUtils.getData(player);
        if(glassColor > -1){
            ItemStack pane = MiscUtils.getEmptyPane(glassColor);
            for(int i = INV_SIZE - 18; i<INV_SIZE - 9; i++){ //set second to bottom row to specified color (unless -1)
                inventory.setItem(i, pane);
            }
        }
        ItemStack emptyPane = MiscUtils.getEmptyPane();
        for(int i = INV_SIZE - 9; i < INV_SIZE; i++){ //set bottom row to gray
            inventory.setItem(i, emptyPane);
        }
        inventory.setItem(INV_SIZE - 1, gemsItem = MiscUtils.getGemsItem(player));
        if(bottomLeft != null) inventory.setItem(INV_SIZE - 9, bottomLeft);
        {
            int slot = 0;
            Iterator<? extends ShopItem> itemIterator = items.iterator();
            if(!itemIterator.hasNext()) throw new IllegalArgumentException("ShopInventory '" + shopName + "' was initialized with an empty items list.");
            while(itemIterator.hasNext()){
                ShopItem item = itemIterator.next();
                ItemStack storefrontItem = item.getStorefront(data, 1);
                if(inventory.getItem(slot) == null){
                    inventory.setItem(slot, storefrontItem);
                    storefront.put(slot, item);
                    slot++;
                }else throw new IllegalArgumentException("ShopItems overflowed to occupied slot when attempting to open ShopInventory " + shopName + ", slot: " + slot);
            }
        }
    }

    private void updateShopItems(){
        for(Map.Entry<Integer, ShopItem> storefrontEntry : storefront.entrySet()){
            ItemStack newStorefrontItem = storefrontEntry.getValue().getStorefront(data, 1);
            inventory.setItem(storefrontEntry.getKey(), newStorefrontItem);
        }
    }

    public void inventoryClick(Player player, int slot, ItemStack clickedItem, ClickType clickType, InventoryClickEvent event){
        event.setCancelled(true);
        ShopItem correspondingShopItem = storefront.get(slot);
        int amount = 1;
        if(purchase(correspondingShopItem, amount)){ //maybe I'll make custom amount buying in the future; I have the framework for it, so it should be pretty easy
            updateShopItems();
            inventory.setItem(INV_SIZE - 1, MiscUtils.getGemsItem(data.getGems()));
        }
    }

    public final boolean purchase(ShopItem shopItem, int amount){
        ShopItem.PurchaseResponse response = shopItem.checkPurchase(data, amount);
        Player player = data.getPlayer();
        if(response.success){
            if(amount > 0){
                PlayerData data = ConfigUtils.getData(player);
                int gems = data.getGems();
                Inventory inv = player.getInventory();
                int stackSize = Math.min(amount, shopItem.maxStackSize()); //amount or max stack size, whichever is less
                int slotsUsed = Math.floorDiv(amount, stackSize); //amount divided by stack size
                if(inv.firstEmpty() == -1){
                    player.sendMessage(ChatColor.RED + "Full inventory!");
                    player.playSound(player.getLocation(), Sound.NOTE_BASS, 10, 1.05f);
                    return false;
                }else{
                    int totalPrice = 0;
                    boolean fits = true;
                    for(int i = 0; i < slotsUsed && fits; i++){
                        int partialPrice = shopItem.grantSingle(inv, stackSize);
                        totalPrice += partialPrice;
                        fits = partialPrice >= shopItem.price;
                    }
                    int leftover = amount % stackSize;
                    if(leftover > 0 && fits) shopItem.grantSingle(inv, leftover);
                    data.setGems(gems - totalPrice);
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 1.5f);
                    return true;
                }
            }else{
                player.sendMessage(ChatColor.RED + "Amount must be more than 0!");
                player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 10, 1);
                Bukkit.getLogger().warning("Player " + player.getUniqueId() + " attempted to purchase items with amount less or equal to than 0.");
                return false;
            }
        }else{
            player.sendMessage(response.message);
            player.playSound(player.getLocation(), Sound.NOTE_BASS, 10, 1);
            return false;
        }
    }

    public Inventory getInventory(){
        return inventory;
    }
}
