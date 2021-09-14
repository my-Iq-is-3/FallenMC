package me.zach.DesertMC.Utils.nbt;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class NBTUtil {
    public static String getCustomAttrString(ItemStack item, String key){
        if(item == null || item.getType().equals(Material.AIR)) return "null";
        NBTItem nbt = new NBTItem(item);
        NBTCompound customAttributes = nbt.getCompound("CustomAttributes");
        if (customAttributes == null) return "null";

        if (customAttributes.getString(key) != null) return customAttributes.getString(key);
        else return "null";
    }


    public static boolean hasCustomKey(ItemStack item, String key){
        if(item == null || item.getType().equals(Material.AIR)) return false;
        NBTItem nbt = new NBTItem(item);
        NBTCompound customAttributes = nbt.getCompound("CustomAttributes");
        if(customAttributes == null) return false;
        return customAttributes.hasKey(key);
    }

    public static <T> T getCustomAttr(ItemStack item, String key, Class<T> type){
        if(item == null || item.getType().equals(Material.AIR)) return null;
        NBTItem nbt = new NBTItem(item);
        NBTCompound customAttributes = nbt.getCompound("CustomAttributes");
        if(customAttributes == null) return null;
        return customAttributes.getObject(key, type);
    }
}
