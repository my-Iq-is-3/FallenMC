package me.zach.DesertMC.Utils.nbt;


import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import me.zach.DesertMC.CommandsPackage.ItemCommand;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class EnchantmentUtil {

    public static EnchantmentUtil getInstance(){
        return new EnchantmentUtil();
    }

    public void addEnchantment(String enchantment, int lvl, ItemStack item, Player player){
        if(player.getInventory().getItemInMainHand() != null){

            ItemStack heldItem = player.getInventory().getItemInMainHand();
            ItemMeta im = heldItem.getItemMeta();

            im.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,1,true);
            heldItem.setItemMeta(im);

            String elore = ItemCommand.enchs.get(enchantment + "_" + lvl);
            if(elore == null) return;

            NBTItem nbti = new NBTItem(heldItem);

            if(nbti.getCompound("CustomAttributes").getCompound("enchantments") != null){
                NBTCompound ench = nbti.getCompound("CustomAttributes").getCompound("enchantments");
                ench.setInteger(enchantment,lvl);
            }else{
                NBTCompound ench = nbti.getCompound("CustomAttributes").addCompound("enchantments");
                ench.setInteger(enchantment,lvl);
            }

            ItemStack ritem = nbti.getItem();
            ItemMeta itemMeta = item.getItemMeta();
            List<String> lore = itemMeta.getLore();

            lore.add(" ");

            for(String s1 : elore.split("\\{br}")){
                lore.add(s1);
            }

            heldItem = nbti.getItem();
            itemMeta.setLore(lore);
            ritem.setItemMeta(itemMeta);
            heldItem = ritem;
            player.getInventory().setItemInMainHand(ritem);

        }else{

            player.sendMessage(ChatColor.RED + "Please hold an item");

        }
    }

    private EnchantmentUtil(){}
}
