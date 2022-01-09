package me.zach.DesertMC.GameMechanics.hitbox;

import me.zach.DesertMC.Utils.structs.Pair;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class HitboxListener implements Listener {//
    @EventHandler
    public void onClick(PlayerInteractEvent event){
        for(Pair<String, Hitbox> h : HitboxManager.getAll()){
            if(h.second.isInside(event.getPlayer().getLocation())){
                event.getPlayer().sendMessage(ChatColor.GREEN + "You are inside hitbox " + ChatColor.BOLD + h.first + ChatColor.GREEN + " of type " + h.second.getClass().getSimpleName());
            }
        }
    }
}
