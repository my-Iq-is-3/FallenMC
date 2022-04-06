package me.zach.DesertMC.Utils;

import me.zach.DesertMC.GameMechanics.Events;
import me.zach.DesertMC.Utils.Config.ConfigUtils;
import me.zach.artifacts.events.ArtifactEvents;
import me.zach.artifacts.gui.inv.ArtifactData;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;
import xyz.fallenmc.risenboss.main.RisenMain;
import xyz.fallenmc.risenboss.main.utils.RisenUtils;

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
        return isIdle(p.getUniqueId());
    }

    public static boolean isIdle(UUID uuid){
        if(!fighting.containsKey(uuid)) return true;
        else return fighting.get(uuid) == 0;
    }

    public static void addAbsorption(LivingEntity entity, float amount){
        setAbsorption(entity, getAbsorption(entity) + amount);
    }

    public static void setAbsorption(LivingEntity entity, float amount){
        CraftLivingEntity craftEntity = (CraftLivingEntity) entity;
        EntityLiving entityLiving = craftEntity.getHandle();
        entityLiving.setAbsorptionHearts(amount);
    }

    public static float getAbsorption(LivingEntity entity){
        CraftLivingEntity craftEntity = (CraftLivingEntity) entity;
        EntityLiving entityLiving = craftEntity.getHandle();
        return entityLiving.getAbsorptionHearts();
    }

    public static double getTotalHealth(LivingEntity entity){
        return entity.getHealth() + getAbsorption(entity);
    }

    /**
     * Removes health accounting for absorption.
     * @param entity The entity to remove the health from.
     * @param health The amount of health to remove.
     */
    public static void removeHealthFromTotal(LivingEntity entity, double health){
        float absorption = getAbsorption(entity);
        if(absorption > 0){
            float removed;
            setAbsorption(entity, removed = (float) Math.min(absorption, health));
            health -= removed;
        }
        if(health > 0) entity.setHealth(entity.getHealth() - health);
    }

    public static void trueDamage(Damageable victim, double dmg, Entity damager){
        victim = MiscUtils.canDamage(victim);
        if(victim != null){
            if(victim instanceof Player){
                Player victimPlayer = (Player) victim;
                ArtifactData vad = ConfigUtils.getAD(victimPlayer);
                if(vad.getSelected().contains(10)){
                    double dmgsub = vad.rarities()[9].mult * 7; // 30
                    dmgsub /= 100; // 0.3
                    dmg -= dmgsub * dmg; // if its 10 its ~7
                }
            }
            if(getTotalHealth((LivingEntity) victim) <= dmg){
                if(victim instanceof Player)
                    Events.executeKill((Player) victim, damager instanceof Player ? (Player) damager : null);
                else victim.setHealth(0);
            }else{
                removeHealthFromTotal((LivingEntity) victim, dmg);
            }
            MiscUtils.damageIndicator(dmg, damager, victim);
            if(RisenUtils.isBoss(victim.getUniqueId())){
                if(damager instanceof Player) RisenMain.currentBoss.bossDamage(damager.getUniqueId(), dmg);
                else RisenMain.currentBoss.bossDamage(dmg);
            }else if(damager != null && RisenUtils.isBoss(damager.getUniqueId())){
                RisenMain.currentBoss.bossAttack(dmg);
            }
        }
    }

    public static final double MIN_FOV = Math.PI / 2;

    public static boolean canSeeTarget(LivingEntity looking, Location target){
        return canSeeTarget(looking.getEyeLocation(), target);
    }

    public static boolean canSeeTarget(Location eyeLocation, Location targetInQuestion){
        Vector line = eyeLocation.toVector().subtract(targetInQuestion.toVector());
        double angle = eyeLocation.getDirection().angle(line);
        return angle < MIN_FOV;
    }
}