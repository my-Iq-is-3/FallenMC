package me.zach.DesertMC.GameMechanics.ClassEvents.TankEvents;

import com.sun.org.apache.xerces.internal.impl.dv.dtd.ENTITYDatatypeValidator;
import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.Utils.Config.ConfigUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EventsForTank {
    public static EventsForTank getInstance(){
        return new EventsForTank();
    }


    public void t1Event(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player){
            Player damager = (Player) event.getDamager();
            if(ConfigUtils.findClass(damager).equals("tank") && ConfigUtils.getLevel("tank",damager) > 1){
                if(event.getEntity() instanceof Player){
                    Player damagedEntity = (Player) event.getEntity();
                    if(damagedEntity.getHealth() - event.getDamage() < 0.1){
                        PotionEffectType type = PotionEffectType.DAMAGE_RESISTANCE;
                        PotionEffect respot = new PotionEffect(type, 80, 0);
                        damager.addPotionEffect(respot,true);
                    }
                }
            }
        }

    }

    public void t5Event(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player && event.getEntity() instanceof Player){
            Player damager = (Player) event.getDamager();
            Player damaged = (Player) event.getEntity();
            if(damaged.getHealth() - event.getDamage() < 0.1){
                if(ConfigUtils.findClass(damager).equals("tank") && ConfigUtils.getLevel("tank",damaged) > 4){
                    PotionEffect vengance = new PotionEffect(PotionEffectType.WEAKNESS,40 ,3 );
                    PotionEffect slow = new PotionEffect(PotionEffectType.SLOW,40 , 0);
                    damager.addPotionEffect(vengance);
                    damager.addPotionEffect(slow);
                }
            }
        }
    }
    public void t8Event(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player && event.getEntity() instanceof Player){
            Player damager = (Player) event.getDamager();
            if(ConfigUtils.getLevel("tank",damager) > 7 && ConfigUtils.findClass(damager).equals("tank")){
                if(DesertMain.crouchers.get(damager.getUniqueId())){
                    event.setDamage(event.getDamage() * 1.05);
                }
            }
        }
    }


}
