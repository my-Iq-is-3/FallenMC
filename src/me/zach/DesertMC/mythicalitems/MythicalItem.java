package me.zach.DesertMC.mythicalitems;

import org.bukkit.inventory.ItemStack;

public interface MythicalItem {
    int price = 0;

    ItemStack getItem();

    int getPrice();

    void setPrice(int price);

    int getID();


}
