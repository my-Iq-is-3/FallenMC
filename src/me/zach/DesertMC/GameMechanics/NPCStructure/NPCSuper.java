package me.zach.DesertMC.GameMechanics.NPCStructure;

import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.Prefix;
import net.jitse.npclib.NPCLib;
import net.jitse.npclib.api.NPC;
import net.jitse.npclib.api.events.NPCInteractEvent;
import net.jitse.npclib.api.skin.MineSkinFetcher;
import net.jitse.npclib.api.skin.Skin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public abstract class NPCSuper implements Listener {
    String name;
    int id;
    String clickMsg;
    Sound clickSnd;
    int clickwait;
    ArrayList<String> npctext = new ArrayList<>();
    public Set<UUID> cantClick = new HashSet<>();
    public NPCSuper(String npcName, int skinID, String clickMessage, Sound clickSound, int clickWaitTime, String... npcTextExcludingName){
        name = npcName;
        id = skinID;
        clickMsg = clickMessage;
        clickSnd = clickSound;
        clickwait = clickWaitTime;
        npctext.addAll(Arrays.asList(npcTextExcludingName));
    }

    public final void npcMessage(Player p, String message){
        p.sendMessage(Prefix.NPC + ChatColor.DARK_GRAY.toString() + " | " + name + ChatColor.GRAY + ": " + ChatColor.WHITE + message);
    }

    @EventHandler
    public final void pickupOnInv(PlayerPickupItemEvent e){
        if(cantClick.contains(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public final void moveOnInv(PlayerMoveEvent ev){
        try{
            if(cantClick.contains(ev.getPlayer().getUniqueId())) ev.setTo(ev.getFrom());
        }catch(NullPointerException ignored){}
    }

    public final void createNPC(Location loc){
        NPCLib library = DesertMain.getNPCLib();
        List<String> text = npctext;
        if(!text.get(0).equals(name)) text.add(0, name);
        NPC npc = library.createNPC(text);
        npc.setLocation(loc);
        MineSkinFetcher.fetchSkinFromIdAsync(id, new MineSkinFetcher.Callback() {
            @Override
            public void call(Skin skin) {
                npc.setSkin(skin);
                npc.create();
                for(Player player : Bukkit.getServer().getOnlinePlayers()){
                    npc.show(player);
                }
            }
            @Override
            public void failed(){
                Bukkit.getLogger().warning(ChatColor.RED + "Skin fetch failed! NPC: " + name);
            }
        });
    }

    @EventHandler
    public final void NPCClick(NPCInteractEvent event){
        try {
            if (event.getNPC().getUniqueId().equals(name) && !cantClick.contains(event.getWhoClicked().getUniqueId())){
                Bukkit.getConsoleSender().sendMessage("NPC click registered. Player: " + event.getWhoClicked().getName() + ", NPC: " + name);
                npcMessage(event.getWhoClicked(), clickMsg);
                event.getWhoClicked().playSound(event.getWhoClicked().getLocation(), clickSnd, 10, 1);
                cantClick.add(event.getWhoClicked().getUniqueId());
                new BukkitRunnable(){
                    public void run(){
                        event.getWhoClicked().openInventory(getStartInventory(event));
                    }
                }.runTaskLater(DesertMain.getInstance, clickwait);
            }
        }catch(NullPointerException ignored){}
    }

    @EventHandler
    public final void closeInv(InventoryCloseEvent event){
        try{
            cantClick.remove(event.getPlayer().getUniqueId());
        }catch(NullPointerException ignored){}
    }

    @EventHandler
    public final void disconnectOnInv(PlayerQuitEvent e){
        cantClick.remove(e.getPlayer().getUniqueId());
    }
    @EventHandler
    public final void kickOnInv(PlayerKickEvent e){cantClick.remove(e.getPlayer().getUniqueId());}
    @EventHandler
    public final void dropOnInv(PlayerDropItemEvent e){if(cantClick.contains(e.getPlayer().getUniqueId())) e.setCancelled(true);}
    public abstract Inventory getStartInventory(NPCInteractEvent event);
}
