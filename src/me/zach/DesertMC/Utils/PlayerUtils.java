package me.zach.DesertMC.Utils;

import net.minecraft.server.v1_9_R1.EntityLiving;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerUtils {

    public static ItemStack[] getArmor(Player player){
        ItemStack helmet = null;
        ItemStack chest = null;
        ItemStack legs = null;
        ItemStack boots = null;
        helmet = player.getInventory().getHelmet();
        chest = player.getInventory().getChestplate();
        legs = player.getInventory().getLeggings();
        boots = player.getInventory().getBoots();


        return new ItemStack[]{boots,legs,chest,helmet};
    }

    public static void addAbsorption(Player player, float amount){
        CraftPlayer craftPlayer = (CraftPlayer) player;
        EntityLiving playerLiving = craftPlayer.getHandle();
        playerLiving.setAbsorptionHearts(amount);
    }

}
