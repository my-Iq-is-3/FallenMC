package me.zach.DesertMC.shops;

import de.tr7zw.nbtapi.NBTItem;
import me.zach.DesertMC.Utils.nbt.NBTUtil;
import me.zach.databank.saver.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class ShopItem {
    private ItemStack item = get();
    public int price;

    public ShopItem(int price){
        this.price = price;
    }

    public int hashCode(){
        return Objects.hash(item, price);
    }

    private ItemStack nextItem(int amount){
        ItemStack item = this.item;
        this.item = get();
        item.setAmount(amount);
        return item;
    }

    public ItemStack getStorefront(PlayerData data, int amount){
        PurchaseResponse response = checkPurchase(data, amount);
        ItemStack newItem = item.clone();
        ItemMeta meta = newItem.getItemMeta();
        List<String> lore = meta.getLore();
        lore.add("");
        lore.add(ChatColor.GRAY + "Price: " + ChatColor.GREEN + price * amount + " Gems");
        lore.add(response.message);
        meta.setLore(lore);
        newItem.setItemMeta(meta);
        NBTItem nbt = new NBTItem(newItem);
        nbt.removeKey("CustomAttributes");
        return nbt.getItem();
    }

    public int grantSingle(Inventory inventory, int stackSize){
        ItemStack item = nextItem(stackSize);
        int totalPrice = price * stackSize;
        HashMap<Integer, ItemStack> didntFit = inventory.addItem(item);
        if(!didntFit.isEmpty())
            for(Map.Entry<Integer, ItemStack> entry : didntFit.entrySet()) totalPrice -= entry.getKey() * this.price;
        return totalPrice;
    }

    public PurchaseResponse checkPurchase(PlayerData data, int amount){
        boolean success = data.getGems() >= price * amount;
        return new PurchaseResponse(success ? "Click to purchase!" : "Not enough gems!", success);
    }

    public int maxStackSize(){
        return NBTUtil.maxStackSize(item);
    }

    protected abstract ItemStack get();

    public static class PurchaseResponse {
        public final String message;
        public final boolean success;

        public PurchaseResponse(String message, boolean success){
            this.message = (success ? ChatColor.GREEN : ChatColor.RED) + message;
            this.success = success;
        }
    }
}
