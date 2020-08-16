package me.zach.DesertMC.GameMechanics.ClassEvents.ScoutEvents;

import me.zach.DesertMC.Utils.Config.ConfigUtils;
import me.zach.DesertMC.Utils.nbt.NBTUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EventsForScout {

    private static final EventsForScout INSTANCE = new EventsForScout();

    public static EventsForScout getInstance() {
        return INSTANCE;
    }

    public void t1Event(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player){
            Player damager = (Player) event.getDamager();
            if(ConfigUtils.findClass(damager).equals("scout") && ConfigUtils.getLevel("scout",damager) > 1){
                if(event.getEntity() instanceof Player){
                    Player damagedEntity = (Player) event.getEntity();
                    if(damagedEntity.getHealth() - event.getDamage() < 0.1){
                        PotionEffectType type = PotionEffectType.SPEED;
                        PotionEffect speedpot = new PotionEffect(type, 80, 0);
                        damager.addPotionEffect(speedpot,true);

                    }
                }
            }
        }

    }

    public void t4Event(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player && event.getEntity() instanceof Player){
            Player damager = (Player) event.getDamager();
            Player damaged = (Player) event.getEntity();
            if(damaged.getHealth() - event.getDamage() < 0.1){

                if(ConfigUtils.getLevel("scout",damager) > 4 && ConfigUtils.findClass(damager).equals("scout")){
                    PotionEffect absorb = new PotionEffect(PotionEffectType.ABSORPTION,10000,0,true,false);
                    damager.addPotionEffect(absorb);
                }

            }
        }
    }

    public void daggerHit(EntityDamageByEntityEvent event){
        
        if(event.getDamager() instanceof Player && event.getEntity() instanceof LivingEntity){
            Player damager = (Player) event.getDamager();
            LivingEntity damaged = (LivingEntity) event.getEntity();
            if(ConfigUtils.getLevel("scout",damager) > 6 && ConfigUtils.findClass(damager).equals("scout")){
                if(NBTUtil.INSTANCE.getCustomAttr(damager.getInventory().getItemInMainHand(),"ID").equals("SCOUT_DAGGER")){
                    if(damager.getLocation().distance(damaged.getLocation()) > 2){
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    public void t8Event(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player){
            Player damager = (Player) event.getDamager();
            if(ConfigUtils.findClass(damager).equals("scout") && ConfigUtils.getLevel("scout",damager) > 8){
                if(damager.isSprinting()) event.setDamage(event.getDamage() * 1.05);
            }
        }
    }


}
