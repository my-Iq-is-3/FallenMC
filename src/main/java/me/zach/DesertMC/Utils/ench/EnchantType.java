package me.zach.DesertMC.Utils.ench;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum EnchantType {
    BOW("bows"){
        public boolean isOfType(ItemStack item){
            return item.getType() == Material.BOW;
        }
    },
    MELEE("melee weapons"){
        public boolean isOfType(ItemStack item){
            String name = item.getType().name();
            return name.endsWith("SWORD") || name.endsWith("AXE");
        }
    },
    ARMOR("armor"){
        public boolean isOfType(ItemStack item){
            String name = item.getType().name();
            return name.endsWith("HELMET") || name.endsWith("CHESTPLATE") || name.endsWith("LEGGINGS") || name.endsWith("BOOTS");
        }
    };

    public String getName(){
        return name;
    }

    public abstract boolean isOfType(ItemStack item);

    public String toString(){
        return name;
    }

    private final String name;

    /**
     * @param name The name of the type of item this enchantment can be applied to, plural.
     */
    EnchantType(String name){
        this.name = name;
    }
}
