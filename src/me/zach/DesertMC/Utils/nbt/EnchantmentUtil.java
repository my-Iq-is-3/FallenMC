package me.zach.DesertMC.Utils.nbt;


import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import me.zach.DesertMC.CommandsPackage.ItemCommand;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

public class EnchantmentUtil {
    public static EnchantmentUtil getInstance(){
        return new EnchantmentUtil();
    }

    public ItemStack addEnchantment(String enchantment, int lvl, ItemStack item, Player player){
        if(player.getInventory().getItemInMainHand() != null){

            item.getItemMeta().addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,1,true);

            NBTItem nbti = new NBTItem(item);

            if(nbti.getCompound("CustomAttributes").getCompound("enchantments") != null){

                NBTCompound ench = nbti.getCompound("CustomAttributes").getCompound("enchantments");
                int clvl = getELevel(enchantment,nbti.getItem());

                if(clvl >=1 ){
                    String elore = ItemCommand.enchs.get(enchantment + "_" + lvl);
                    if(elore == null) return null;
                    String val0 = elore.split("\\{br}")[0];
                    ItemStack ritem = nbti.getItem();
                    ItemMeta ritemMeta = ritem.getItemMeta();
                    List<String> lore = ritemMeta.getLore();

                    lore.replaceAll(s -> {

                        s.replace(val0, val0.replace(clvl + "",lvl + ""));
                        return s;

                    });

                    lore.add(" ");

                    ritemMeta.setLore(lore);
                    ritem.setItemMeta(ritemMeta);

                    return ritem;

                }else{

                    String elore = ItemCommand.enchs.get(enchantment + "_" + lvl);
                    if(elore == null) return null;

                    ItemStack ritem = nbti.getItem();
                    ItemMeta ritemMeta = ritem.getItemMeta();
                    List<String> lore = ritemMeta.getLore();

                    lore.add(" ");

                    Collections.addAll(lore, elore.split("\\{br}"));

                    ritemMeta.setLore(lore);
                    ritem.setItemMeta(ritemMeta);

                    return ritem;

                }

            }else{
                NBTCompound ench = nbti.getCompound("CustomAttributes").addCompound("enchantments"); // create because it doesn't exist
//              yet

                ench.setInteger(enchantment,lvl); // set the enchantment


                String elore = ItemCommand.enchs.get(enchantment + "_" + lvl); // get the specified lore for the enchantment

                if(elore == null) return null; // return if the enchantment does not exist


                ItemStack ritem = nbti.getItem(); // get the finished nbt item
                ItemMeta ritemMeta = ritem.getItemMeta(); // get the item meta
                List<String> lore = ritemMeta.getLore(); // get the lore for the item


                lore.add(" "); // add a blank space for beautifying the desc

                Collections.addAll(lore, elore.split("\\{br}")); // add the lore I mentioned earlier (elore)


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
