package me.zach.DesertMC.GameMechanics.ClassEvents.ScoutEvents;

import me.zach.DesertMC.GameMechanics.ClassEvents.PlayerManager.Events;
import me.zach.DesertMC.Utils.Config.ConfigUtils;
import org.bukkit.entity.Entity;
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
                        PotionEffect speedpot = new PotionEffect(type, 60, 0);
                        damager.addPotionEffect(speedpot,true);

                    }
                }
            }
        }

    }

}
