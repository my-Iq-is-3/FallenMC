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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class NPCSuper implements Listener {
    String name;
    int id;
    String clickMsg;
    Sound clickSnd;
    int clickwait;
    ArrayList<String> npctext;
    public ArrayList<UUID> cantClick = new ArrayList<>();
    NPCDataPasser passer;
    public NPCSuper(String npcName, int skinID, String clickMessage, Sound clickSound, int clickWaitTime, NPCDataPasser dataPasser, String... npcTextExcludingName){
        name = npcName;
        id = skinID;
        clickMsg = clickMessage;
        clickSnd = clickSound;
        clickwait = clickWaitTime;
        npctext = (ArrayList<String>) Arrays.asList(npcTextExcludingName);
        passer = dataPasser;
    }

    public void npcMessage(Player p, String message){
        p.sendMessage(Prefix.NPC + ChatColor.DARK_GRAY.toString() + " | " + name + ChatColor.GRAY + ": " + ChatColor.WHITE + message);
    }

    @EventHandler
    public void pickupOnInv(PlayerPickupItemEvent e){
        try {
            if (cantClick.contains(e.getPlayer().getUniqueId())) {
                e.setCancelled(true);
            }
        }catch(NullPointerException ignored){ }
    }

    @EventHandler
    public void moveOnInv(PlayerMoveEvent ev){
        try{
            if(cantClick.contains(ev.getPlayer().getUniqueId())) ev.setCancelled(true);
        }catch(NullPointerException ignored){}
    }

    public void createNPC(Location loc){
        NPCLib library = DesertMain.getNPCLib();
        List<String> text = npctext;
        text.add(0, name);
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
                Bukkit.getConsoleSender().sendMessage("Skin fetch failed! NPC: " + name);
            }
        });
    }

    @EventHandler
    public void NPCClick(NPCInteractEvent event){
        try {
            if (event.getNPC().getText().get(0).equals(name) && !cantClick.contains(event.getWhoClicked().getUniqueId())){
                npcMessage(event.getWhoClicked(), clickMsg);
                event.getWhoClicked().playSound(event.getWhoClicked().getLocation(), clickSnd, 10, 1);
                cantClick.add(event.getWhoClicked().getUniqueId());
                new BukkitRunnable(){
                    public void run(){
                        event.getWhoClicked().openInventory(passer.getStartInventory(event));
                    }
                }.runTaskLater(Bukkit.getPluginManager().getPlugin("Fallen"), clickwait);
            }
        }catch(NullPointerException ignored){}
    }

    @EventHandler
    public void closeInv(InventoryCloseEvent event){
        try{
            cantClick.remove(event.getPlayer().getUniqueId());
        }catch(NullPointerException ignored){}
    }

    @EventHandler
    public void disconnectOnInv(PlayerQuitEvent e){
        cantClick.remove(e.getPlayer().getUniqueId());
    }
    @EventHandler
    public void kickOnInv(PlayerKickEvent e){cantClick.remove(e.getPlayer().getUniqueId());}
    @EventHandler
    public void dropOnInv(PlayerDropItemEvent e){e.setCancelled(true);}
}
