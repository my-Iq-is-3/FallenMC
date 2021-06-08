package me.zach.DesertMC.Utils.nbt;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

public class NBTUtil {
    public static String getCustomAttr(ItemStack item, String key){
        if(item == null) return "null";
        NBTItem nbt = new NBTItem(item);
        NBTCompound customAttributes = nbt.getCompound("CustomAttributes");
        if (customAttributes == null) return "null";

        if (customAttributes.getString(key) != null) return customAttributes.getString(key);
        else return "null";
    }

    public static boolean hasCustomKey(ItemStack item, String key){
        if(item == null ) return false;
        NBTItem nbt = new NBTItem(item);
        NBTCompound customAttributes = nbt.getCompound("CustomAttributes");
        if(customAttributes == null) return false;
        return customAttributes.hasKey(key);
    }
}
