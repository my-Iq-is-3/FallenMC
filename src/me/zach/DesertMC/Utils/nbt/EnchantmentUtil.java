package me.zach.DesertMC.Utils.nbt;


import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import me.zach.DesertMC.CommandsPackage.ItemCommand;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class EnchantmentUtil {
    public static EnchantmentUtil getInstance(){
        return new EnchantmentUtil();
    }

    public ItemStack addEnchantment(String enchantment, int lvl, ItemStack item, Player player){
        if(player.getInventory().getItemInMainHand() != null){
            ItemMeta im = item.getItemMeta();
            im.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,1,true);
            item.setItemMeta(im);
            NBTItem nbti = new NBTItem(item);

            if(nbti.getCompound("CustomAttributes").getCompound("enchantments") != null){

                NBTCompound ench = nbti.getCompound("CustomAttributes").getCompound("enchantments");
                int clvl = getELevel(enchantment,nbti.getItem());

                if(clvl >=1 ){
                    ItemStack ritem = nbti.getItem();
                    ItemMeta ritemMeta = ritem.getItemMeta();
                    List<String> lore = ritemMeta.getLore();



                    ritemMeta.setLore(lore);
                    ritem.setItemMeta(ritemMeta);

                    return ritem;

                }else{




                    ItemStack ritem = nbti.getItem();
                    ItemMeta ritemMeta = ritem.getItemMeta();
                    List<String> lore = ritemMeta.getLore();



                    ritemMeta.setLore(lore);
                    ritem.setItemMeta(ritemMeta);

                    return ritem;

                }

            }else{
                NBTCompound ench = nbti.getCompound("CustomAttributes").addCompound("enchantments"); // create because it doesn't exist
//              yet

                ench.setInteger(enchantment,lvl); // set the enchantment




                ItemStack ritem = nbti.getItem(); // get the finished nbt item
                ItemMeta ritemMeta = ritem.getItemMeta(); // get the item meta
                List<String> lore = ritemMeta.getLore(); // get the lore for the item


                lore.add(" "); // add a blank space for beautifying the desc

                lore.add(ChatColor.BLUE + "Enchantments: "); // add the first enchantment [colon thing].



                ritemMeta.setLore(lore); // set all the stuff
                ritem.setItemMeta(ritemMeta);


                return ritem;
            }



        }else{

            return null;
        }
    }


    public int getELevel(String ench,ItemStack item) {
        NBTItem hnbt = new NBTItem(item);
        if (hnbt.getCompound("CustomAttributes").getCompound("enchantments") != null) {
            NBTCompound hnbtc = hnbt.getCompound("CustomAttributes").getCompound("enchantments");
            return hnbtc.getInteger(ench);
        }else return 0;
    }

    private EnchantmentUtil(){}
}
