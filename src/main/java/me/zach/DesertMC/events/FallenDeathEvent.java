package me.zach.DesertMC.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Called when a player is killed by any means.
 * @see FallenDeathByPlayerEvent
 */
public class FallenDeathEvent extends Event implements Cancellable {

    public Player getPlayer(){
        return player;
    }

    Player player;
    boolean cancelled = false;

    public EntityDamageEvent.DamageCause getDamageCause(){
        return damageCause;
    }

    EntityDamageEvent.DamageCause damageCause;

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers(){
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

    public FallenDeathEvent(Player died, EntityDamageEvent.DamageCause cause){
        this.player = died;
        this.damageCause = cause;
    }

    public boolean isCancelled(){
        return cancelled;
    }

    public void setCancelled(boolean cancelled){
        this.cancelled = cancelled;
    }
}
