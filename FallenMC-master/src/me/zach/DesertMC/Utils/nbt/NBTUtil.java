package me.zach.DesertMC.Utils.nbt;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

public class NBTUtil {
    public static NBTUtil INSTANCE = new NBTUtil();

    private NBTUtil(){

    }

    public String getCustomAttr(ItemStack item, String key){
        NBTItem nbti = new NBTItem(item);
        NBTCompound nbtCompound = nbti.getCompound("CustomAttributes");
        return nbtCompound.getString(key);
    }

}
