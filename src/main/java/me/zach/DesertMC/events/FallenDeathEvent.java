package me.zach.DesertMC.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class FallenDeathEvent extends Event implements Cancellable {
    Player player;

    public Player getPlayer(){
        return player;
    }

    public Player getKiller(){
        return killer;
    }

    public ItemStack getItemUsed(){
        return used;
    }

    Player killer;
    ItemStack used;
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

    public FallenDeathEvent(Player died, Player killer, ItemStack used, EntityDamageEvent.DamageCause cause){
        this.player = died;
        this.killer = killer;
        this.used = used;
        this.damageCause = cause;
    }

    public boolean isCancelled(){
        return cancelled;
    }

    public void setCancelled(boolean cancelled){
        this.cancelled = cancelled;
    }
}
