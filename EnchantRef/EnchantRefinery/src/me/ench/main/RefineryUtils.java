package me.ench.main;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;

public class RefineryUtils {
    public static HashMap<String,Boolean> isRefineryOpen = new HashMap<>();
    public static boolean isHammer(ItemStack item) {
        String name = item.getItemMeta().getDisplayName();
        if(name == null){
            return false;
        }
        return name.equalsIgnoreCase(ChatColor.AQUA + "Wood Hammer") || name.equalsIgnoreCase(ChatColor.AQUA + "Stone Hammer") || name.equalsIgnoreCase(ChatColor.AQUA + "Iron Hammer") || name.equalsIgnoreCase(ChatColor.AQUA + "Diamond Hammer") || name.equalsIgnoreCase(ChatColor.LIGHT_PURPLE + "Special Hammer");
    }

    public static boolean isBook(ItemStack item) {
        if(new NBTItem(item).getCompound("CustomAttributes") == null) {
            return false;
        }else return new NBTItem(item).getCompound("CustomAttributes").getInteger("BASE_LEVEL") != null;
    }

    public static int random(int min, int max) {
        return (int)(Math.random()*((max-min)+1))+min;
    }


    public static void addSpecialEnch(ItemStack book) {
        if(isBook(book)) {
            switch (random(1, 3)) {
                case 1:
                    ItemMeta boMeta = book.getItemMeta();
                    ArrayList<String> specialLore = new ArrayList<>();
                    specialLore.add("");
                    specialLore.add(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Self Destruct");
                    specialLore.add(ChatColor.LIGHT_PURPLE + "If you are 5hp or under, you can hold down right click");
                    specialLore.add(ChatColor.LIGHT_PURPLE + "to explode, which will kill you, and will deal 10hp of");
                    specialLore.add(ChatColor.LIGHT_PURPLE + "damage (armor ignored) to anyone within 12 blocks.");
                    ArrayList<String> bookLore = (ArrayList<String>) book.getItemMeta().getLore();
                    bookLore.addAll(specialLore);
                    boMeta.setLore(bookLore);
                    NBTItem nbtI = new NBTItem(book);
                    nbtI.getCompound("CustomAttributes").addCompound("Special").setString("ID", "SELF_DESTRUCT");
                    nbtI.getCompound("CustomAttributes").getCompound("Special").setString("NAME", ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Self Destruct");
                    book = nbtI.getItem();


                case 2:
                    ItemMeta bmeta = book.getItemMeta();
                    ArrayList<String> specialL = new ArrayList<>();
                    specialL.add("");
                    specialL.add(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Grand Escape");
                    specialL.add(ChatColor.LIGHT_PURPLE + "Crouch, jump, and right click at the same time to render");
                    specialL.add(ChatColor.LIGHT_PURPLE + "yourself invisible until you attack or get attacked. Used once per life.");
                    ArrayList<String> bookL = (ArrayList<String>) book.getItemMeta().getLore();
                    bookL.addAll(specialL);
                    bmeta.setLore(bookL);
                    book.setItemMeta(bmeta);
                    NBTItem bNBT = new NBTItem(book);
                    bNBT.getCompound("CustomAttributes").addCompound("Special").setString("ID", "GRAND_ESCAPE");
                    bNBT.getCompound("CustomAttributes").getCompound("Special").setString("NAME", ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Grand Escape");
                    book = bNBT.getItem();
                case 3:
                    ItemMeta meta = book.getItemMeta();
                    ArrayList<String> speciallor = new ArrayList<>();
                    speciallor.add("");
                    speciallor.add(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Booster");
                    speciallor.add(ChatColor.LIGHT_PURPLE + "Crouch repeatedly and quickly to propel yourself in the");
                    speciallor.add(ChatColor.LIGHT_PURPLE + "direction you are looking. Can be used once every 3 kills.");
                    ArrayList<String> booklor = (ArrayList<String>) book.getItemMeta().getLore();
                    booklor.addAll(speciallor);
                    meta.setLore(booklor);
                    book.setItemMeta(meta);
                    NBTItem bookNBT = new NBTItem(book);
                    bookNBT.getCompound("CustomAttributes").addCompound("Special").setString("ID", "BOOSTER");
                    bookNBT.getCompound("CustomAttributes").getCompound("Special").setString("NAME", ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Booster");
                    book = bookNBT.getItem();
            }
        }
    }

    public static ItemStack refine(ItemStack book, ItemStack hammer, boolean specialGuaranteed){
        NBTCompound bookCompound = new NBTItem(book).getCompound("CustomAttributes");
        NBTCompound hammerCompound = new NBTItem(hammer).getCompound("CustomAttributes");
        int realLevel = bookCompound.getInteger("REAL_LEVEL");
        int baseLevel = bookCompound.getInteger("BASE_LEVEL");
        int minLevel = hammerCompound.getInteger("MIN_LEVELS_TO_UPGRADE");
        int maxLevel = hammerCompound.getInteger("MAX_LEVELS_TO_UPGRADE");
        int remainingLevels = maxLevel - (realLevel - baseLevel);
        ItemStack newBook = book.clone();
        ItemMeta newMeta = newBook.getItemMeta();
        int randomP = random(1, 100);
        NBTItem newNBT = new NBTItem(newBook);

        if(randomP >= 85 && hammer.getType().equals(Material.GOLD_HOE)) {
           specialGuaranteed = true;
        }

        if(specialGuaranteed){
            addSpecialEnch(newNBT.getItem());
            return newNBT.getItem();
        }
        else if(randomP <= hammerCompound.getInteger("DOWNGRADE_CHANCE") && realLevel != 1) {
            newNBT.getCompound("CustomAttributes").setInteger("REAL_LEVEL", (newNBT.getCompound("CustomAttributes").getInteger("REAL_LEVEL") - 1));

            newMeta.setDisplayName(newMeta.getDisplayName().replaceAll("" + realLevel,  "" + (realLevel - 1)));
            newNBT.getItem().setItemMeta(newMeta);
            return newNBT.getItem();
        }else {
            int newLevel = 1;
            if(minLevel >= remainingLevels){
                newLevel = realLevel + remainingLevels;
            }else{
                newLevel = realLevel + random(minLevel, remainingLevels);
            }
            newNBT.getCompound("CustomAttributes").setInteger("REAL_LEVEL", newLevel);
            newMeta.setDisplayName(newMeta.getDisplayName().replaceAll("" + realLevel, "" + newLevel));
            book = newNBT.getItem();
            book.setItemMeta(newMeta);
            newNBT = new NBTItem(book);
            newNBT.getCompound("CustomAttributes").setInteger("REAL_LEVEL", newLevel);

            return newNBT.getItem();

        }


    }

}
