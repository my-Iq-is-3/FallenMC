package me.zach.DesertMC.Utils.nbt;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class NBTUtil {
    public static String getCustomAttrString(ItemStack item, String key){
        if(item == null || item.getType() == Material.AIR) return "null";
        return getCustomAttrString(new NBTItem(item), key);
    }

    public static double getCustomAttrDouble(ItemStack item, String key){
        return getCustomAttr(item, key, double.class);
    }

    public static int maxStackSize(ItemStack item){
        return hasCustomKey(item, "UUID") ? 1 : item.getMaxStackSize();
    }

    public static boolean hasCustomKey(ItemStack item, String key){
        if(item == null || item.getType().equals(Material.AIR)) return false;
        else return hasCustomKey(new NBTItem(item), key);
    }

    public static String getCustomAttrString(NBTCompound nbt, String key){
        String result = getCustomAttr(nbt, key, String.class);
        return result != null ? result : "null";
    }

    public static float getCustomAttrFloat(ItemStack item, String key, float defaultValue){
        if(item == null || item.getType() == Material.AIR) return defaultValue;
        return getCustomAttrFloat(new NBTItem(item), key, defaultValue);
    }

    public static float getCustomAttrFloat(NBTCompound nbt, String key, float defaultValue){
        Float result = getCustomAttr(nbt, key, float.class);
        return result == null ? defaultValue : result;
    }

    public static boolean hasCustomKey(NBTCompound nbt, String key){
        NBTCompound customAttributes = nbt.getCompound("CustomAttributes");
        if(customAttributes == null) return false;
        return customAttributes.hasKey(key);
    }

    public static <T> T getCustomAttr(NBTCompound nbt, String key, Class<T> type){
        NBTCompound customAttributes = nbt.getCompound("CustomAttributes");
        if(customAttributes == null) return null;
        return customAttributes.getObject(key, type);
    }

    public static <T> T getCustomAttr(ItemStack item, String key, Class<T> type){
        if(item == null || item.getType().equals(Material.AIR)) return null;
        else return getCustomAttr(new NBTItem(item), key, type);
    }

    public NBTCompound checkCustomAttr(ItemStack item){
        return checkCustomAttr(new NBTItem(item));
    }

    public NBTCompound checkCustomAttr(NBTCompound nbt){
        NBTCompound customAttributes = nbt.getCompound("CustomAttributes");
        return customAttributes == null ? nbt.addCompound("CustomAttributes") : customAttributes;
    }
}
