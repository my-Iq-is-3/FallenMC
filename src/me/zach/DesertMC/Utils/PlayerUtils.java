package me.zach.DesertMC.Utils;

import me.zach.DesertMC.Utils.Config.ConfigUtils;
import me.zach.artifacts.events.ArtifactEvents;
import me.zach.artifacts.gui.helpers.ArtifactUtils;
import me.zach.artifacts.gui.inv.ArtifactData;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.UUID;


public class PlayerUtils implements Listener {
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

    public static void addAbsorption(Player player, float amount){
        CraftPlayer craftPlayer = (CraftPlayer) player;
        EntityLiving playerLiving = craftPlayer.getHandle();
        playerLiving.setAbsorptionHearts(playerLiving.getAbsorptionHearts() + amount);
    }

    public static void trueDamage(Damageable victim, double dmg, Entity damager){
        if(victim instanceof Player){
            Player victimPlayer = (Player) victim;
            ArtifactData vad = ConfigUtils.getAD(victimPlayer);
            if(vad.getSelected().contains(10)){
                double dmgsub = 15 * vad.rarities()[9].mult * 5; // 30
                dmgsub /= 100; // 0.3
                dmg -= dmgsub * dmg; // if its 10 its ~7
            }
        }
        if(victim.getHealth() <= dmg){
            victim.damage(999, damager);
        }else{
            victim.damage(dmg, damager);
        }
    }
}