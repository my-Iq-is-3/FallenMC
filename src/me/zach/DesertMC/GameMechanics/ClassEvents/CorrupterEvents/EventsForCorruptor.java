package me.zach.DesertMC.GameMechanics.ClassEvents.CorrupterEvents;

import me.zach.DesertMC.DesertMain;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class EventsForCorruptor {
    public static final EventsForCorruptor INSTANCE = new EventsForCorruptor();


    public void t1Event(EntityDamageByEntityEvent event){
        PlayerInteractEvent event1;
        if(event.getEntity() instanceof Player && event.getDamager() instanceof Player){
            Player damaged = (Player) event.getEntity();
            Player damager = (Player) event.getDamager();
            if(damaged.getHealth() - event.getDamage() < 0.1){
                DesertMain.ct1players.add(damager.getUniqueId());
            }
        }

    }
}
