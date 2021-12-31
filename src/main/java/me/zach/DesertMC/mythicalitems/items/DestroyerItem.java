package me.zach.DesertMC.mythicalitems.items;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import me.zach.DesertMC.mythicalitems.MythicalItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Properties;
import java.util.UUID;

public class DestroyerItem implements MythicalItem {
    private int price = 50;

    @Override
    public ItemStack getItem() {
        ItemStack item = new ItemStack(Material.STONE_SWORD);
        ItemMeta im = item.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();

        im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        im.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        im.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,1,false);
        im.setDisplayName(ChatColor.RED + "Destroyer");

        lore.add(" ");
        lore.add(ChatColor.RED + "Mythical Ability: Destroyer");
        lore.add(ChatColor.DARK_GRAY + "Right click to deal " + ChatColor.AQUA + "3" + ChatColor.DARK_GRAY + " hearts of damage to all entities");
        lore.add(ChatColor.DARK_GRAY + "within " + ChatColor.AQUA + "6" + ChatColor.DARK_GRAY + " blocks,");
        lore.add(ChatColor.DARK_GRAY + "but for every player there is, you take " + ChatColor.AQUA + "1" + ChatColor.DARK_GRAY + " heart of damage.");
        lore.add(" ");
        lore.add(ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "You cannot get rewards for kills with this ability");
        lore.add(ChatColor.RED + "§lWARNING§c: This ability §lCAN §ckill you!");
        lore.add(" ");
        lore.add(ChatColor.GRAY + "Cooldown: " + ChatColor.RED + "10 Seconds");
        lore.add(ChatColor.DARK_GRAY + "Created by its me, rtbob#9910");

        im.setLore(lore);
        item.setItemMeta(im);

        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setByte("Unbreakable", (byte) 1);
        NBTCompound customAttributes = nbtItem.addCompound("CustomAttributes");
        customAttributes.setString("ID","MYTHICAL_ITEM");
        customAttributes.setInteger("MYTHICAL_ID", 1);
        customAttributes.setBoolean("CAN_ENCHANT",false);
        customAttributes.setString("UUID", UUID.randomUUID().toString());

        return nbtItem.getItem();
    }

    @Override
    public int getPrice() {
        return price;
    }

    @Override
    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public int getID() {
        return 1;
    }
}
