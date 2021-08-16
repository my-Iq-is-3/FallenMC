package me.zach.DesertMC.Utils;

import me.zach.artifacts.events.ArtifactEvents;
import me.zach.artifacts.gui.helpers.ArtifactUtils;
import me.zach.artifacts.gui.inv.ArtifactData;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;


public class PlayerUtils {
    public static HashMap<UUID,Integer> fighting = new HashMap<>();


    public static void setFighting(Player p){
        if(isIdle(p)) ArtifactEvents.enterCombat(p);
        fighting.put(p.getUniqueId(),10);
    }

    public static void setIdle(Player p){fighting.put(p.getUniqueId(),0);}

    public static boolean isIdle(Player p){
        if(!fighting.containsKey(p.getUniqueId())) return true;
        else return fighting.get(p.getUniqueId()) == 0;
    }
    /**
     * @deprecated because <code>Player#getArmorContents()</code> can be used to retrieve a player's armor in the form of an <code>ItemStack[]</code>.
     */
    @Deprecated
    public static ItemStack[] getArmor(Player player){
        ItemStack helmet;
        ItemStack chest;
        ItemStack legs;
        ItemStack boots;
        helmet = player.getInventory().getHelmet();
        chest = player.getInventory().getChestplate();
        legs = player.getInventory().getLeggings();
        boots = player.getInventory().getBoots();


        return new ItemStack[]{boots,legs,chest,helmet};
    }

    public static void addAbsorption(Player player, float amount){
        CraftPlayer craftPlayer = (CraftPlayer) player;
        EntityLiving playerLiving = craftPlayer.getHandle();
        playerLiving.setAbsorptionHearts(playerLiving.getAbsorptionHearts() + amount);
    }

    public static void trueDamage(Player victim, double dmg, Player damager){
        ArtifactData vad = new ArtifactData(victim);
        if(ArtifactUtils.contains(vad.getSelected(), (byte) 10)){
            double dmgsub = 15 * vad.rarities()[9].mult * 5; // 30
            dmgsub /= 100; // 0.3
            dmg -= dmgsub * dmg; // if its 10 its ~7
        }
        if(victim.getHealth() <= dmg){
            victim.damage(999, damager);
        }else{
            victim.damage(0, damager);
            victim.setHealth(victim.getHealth()-dmg);
        }
    }
}