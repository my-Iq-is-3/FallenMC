package me.zach.DesertMC.Utils.nbt;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

public class NBTUtil {
    public static NBTUtil INSTANCE = new NBTUtil();

    private NBTUtil(){

    }

    public String getCustomAttr(ItemStack item, String key) throws NullPointerException{

        NBTItem nbti = new NBTItem(item);
        if(nbti.getCompound("CustomAttributes") == null)return "null";


        NBTCompound nbtCompound = nbti.getCompound("CustomAttributes");
        if(nbtCompound.getString(key) != null) return nbtCompound.getString(key);
        else throw new NullPointerException("Cannot get string from CustomAttributes if key is null");
    }

}
