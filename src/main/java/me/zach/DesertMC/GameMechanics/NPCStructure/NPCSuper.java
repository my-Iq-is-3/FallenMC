package me.zach.DesertMC.GameMechanics.NPCStructure;

import me.zach.DesertMC.DesertMain;
import net.jitse.npclib.api.events.NPCInteractEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class NPCSuper extends SimpleNPC {
    public Set<UUID> cantClick = new HashSet<>();
    public Set<UUID> openInv = new HashSet<>();
    public NPCSuper(String npcName, int skinID, String clickMessage, Sound clickSound, String... npcTextExcludingName){
        super(npcName, skinID, clickMessage, clickSound, true, npcTextExcludingName);
    }

    @EventHandler
    public final void pickupOnInv(PlayerPickupItemEvent e){
        if(openInv.contains(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
        }
    }

    public final SimpleNPC.ClickResponse clickResponse(NPCInteractEvent event){
        Player player = event.getWhoClicked();
        UUID uuid = player.getUniqueId();
        ClickResponse response = guiNpcClickResponse(event);
        boolean absorb = false;
        if(response.success){
            if(!cantClick.contains(uuid) && !player.isSneaking()){
                player.playSound(event.getWhoClicked().getLocation(), clickSnd, 10, 1);
                cantClick.add(player.getUniqueId());
                Bukkit.getScheduler().runTaskLater(DesertMain.getInstance, () -> player.sendMessage(ChatColor.DARK_GRAY + "Click me again to open the " + name + ChatColor.DARK_GRAY + " menu"), 30);
                Bukkit.getScheduler().runTaskLater(DesertMain.getInstance, () -> cantClick.remove(uuid), 330);
            }else{
                absorb = true;
                player.openInventory(getStartInventory(event));
                openInv.add(player.getUniqueId());
                player.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 10, 1);
                cantClick.remove(player.getUniqueId());
            }
        }
        return new SimpleNPC.ClickResponse(response.message, absorb);
    }

    //override this method to control if a player opens an npc's inventory, and what message they get depending on your own code.
    public NPCSuper.ClickResponse guiNpcClickResponse(NPCInteractEvent event){
        return new ClickResponse(clickMsg, true);
    }

    @EventHandler
    public final void closeInv(InventoryCloseEvent event){
        openInv.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public final void disconnectOnInv(PlayerQuitEvent e){
        openInv.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public final void kickOnInv(PlayerKickEvent e){openInv.remove(e.getPlayer().getUniqueId());}

    @EventHandler
    public final void dropOnInv(PlayerDropItemEvent e){if(openInv.contains(e.getPlayer().getUniqueId())) e.setCancelled(true);}

    public abstract Inventory getStartInventory(NPCInteractEvent event);

    public static class ClickResponse {
        public final boolean success;
        public final String message;
        public ClickResponse(String message, boolean success){
            this.message = message;
            this.success = success;
        }
    }
}