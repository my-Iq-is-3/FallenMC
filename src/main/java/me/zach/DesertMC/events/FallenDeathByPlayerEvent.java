package me.zach.DesertMC.events;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Called when a player is killed by another player.
 */
public class FallenDeathByPlayerEvent extends FallenDeathEvent {
    Player killer;
    ItemStack used;

    public ItemStack getItemUsed(){
        return used;
    }

    public Player getKiller(){
        return killer;
    }

    public FallenDeathByPlayerEvent(Player died, Player killer, ItemStack used, EntityDamageEvent.DamageCause cause){
        super(died, cause);
        this.killer = killer;
        this.used = used;
    }
}
