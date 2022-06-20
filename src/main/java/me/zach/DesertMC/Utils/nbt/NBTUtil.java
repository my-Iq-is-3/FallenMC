package me.zach.DesertMC.Utils.nbt;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NBTUtil {
    public static String getCustomAttrString(ItemStack item, String key){
        if(item == null || item.getType() == Material.AIR) return "null";
        return getCustomAttrString(new NBTItem(item), key);
    }

    public static int maxStackSize(ItemStack item){
        return hasCustomKey(item, "UUID") ? 1 : item.getMaxStackSize();
    }

    public static boolean hasCustomKey(ItemStack item, String key){
        if(item == null || item.getType().equals(Material.AIR)) return false;
        else return hasCustomKey(new NBTItem(item), key);
    }

    public static String getCustomAttrString(NBTCompound nbt, String key){
        return getCustomAttr(nbt, key, String.class, "null");
    }

    public static float getCustomAttrFloat(ItemStack item, String key, float defaultValue){
        if(item == null || item.getType() == Material.AIR) return defaultValue;
        return getCustomAttrFloat(new NBTItem(item), key, defaultValue);
    }

    public static float getCustomAttrFloat(NBTCompound nbt, String key, float defaultValue){
        return getCustomAttr(nbt, key, float.class, defaultValue);
    }

    public static boolean getCustomAttrBoolean(ItemStack item, String key){
        if(item == null || item.getType() == Material.AIR) return false;
        else return getCustomAttrBoolean(new NBTItem(item), key);
    }

    public static boolean getCustomAttrBoolean(NBTCompound nbt, String key){
        return getCustomAttr(nbt, key, boolean.class, false);
    }

    public static boolean hasCustomKey(NBTCompound nbt, String key){
        NBTCompound customAttributes = nbt.getCompound("CustomAttributes");
        if(customAttributes == null) return false;
        return customAttributes.hasKey(key);
    }

    public static <T> T getCustomAttr(NBTCompound nbt, String key, Class<T> type){
        return getCustomAttr(nbt, key, type, null);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getCustomAttr(NBTCompound nbt, String key, Class<T> type, T defaultValue){
        NBTCompound customAttributes = nbt.getCompound("CustomAttributes");
        if(customAttributes == null) return defaultValue;
        if(!customAttributes.hasKey(key)){
            return defaultValue;
        }
        Object result;
        if(type == boolean.class || type == Boolean.class) result = customAttributes.getBoolean(key);
        else if(type == byte.class || type == Byte.class) result = customAttributes.getByte(key);
        else if(type == short.class || type == Short.class) result = customAttributes.getShort(key);
        else if(type == int.class || type == Integer.class) result = customAttributes.getInteger(key);
        else if(type == float.class || type == Float.class) result = customAttributes.getFloat(key);
        else if(type == double.class || type == Double.class) result = customAttributes.getDouble(key);
        else if(type == long.class || type == Long.class) result = customAttributes.getLong(key);
        else if(type == String.class) result = customAttributes.getString(key);
        else if(type == byte[].class) result = customAttributes.getByteArray(key);
        else if(type == int[].class) result = customAttributes.getIntArray(key);
        else if(type == NBTCompound.class) result = customAttributes.getCompound(key);
        else if(type == UUID.class) result = customAttributes.getUUID(key);
        else if(type == ItemStack.class) result = customAttributes.getItemStack(key);
        //whew, gotta catch my breath...
        else result = customAttributes.getObject(key, type);
        return result == null ? defaultValue : (T) result;
    }

    public static <T> T getCustomAttr(ItemStack item, String key, Class<T> type, T defaultValue){
        if(item == null || item.getType().equals(Material.AIR)) return defaultValue;
        else return getCustomAttr(new NBTItem(item), key, type, defaultValue);
    }

    public static <T> T getCustomAttr(ItemStack item, String key, Class<T> type){
        return getCustomAttr(item, key, type, null);
    }

    public static ItemStack setLives(ItemStack item, int lives){
        if(item != null && item.getType() != Material.AIR){
            NBTItem nbt = new NBTItem(item);
            NBTCompound customAttr = checkCustomAttr(nbt);
            customAttr.setInteger("LIVES", lives);
            item = nbt.getItem();
            ItemMeta meta = item.getItemMeta();
            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
            for(int i = 0, size = lore.size(); i < size; i++){
                String line = lore.get(i);
                if(line.contains("Lives remaining")){
                    lore.set(i, ChatColor.GRAY + "Lives remaining: " + ChatColor.RED + lives);
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                    return item;
                }
            }
            lore.add(ChatColor.GRAY + "Lives remaining: " + ChatColor.RED + lives);
            meta.setLore(lore);
            item.setItemMeta(meta);
            return item;
        }else return item;
    }

    public static NBTCompound checkCustomAttr(ItemStack item){
        return checkCustomAttr(new NBTItem(item));
    }

    public static NBTCompound checkCustomAttr(NBTCompound nbt){
        NBTCompound customAttributes = nbt.getCompound("CustomAttributes");
        return customAttributes == null ? nbt.addCompound("CustomAttributes") : customAttributes;
    }
}
