package me.zach.DesertMC.GameMechanics;

import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.Prefix;
import net.jitse.npclib.NPCLib;
import net.jitse.npclib.api.NPC;
import net.jitse.npclib.api.events.NPCInteractEvent;
import net.jitse.npclib.api.skin.MineSkinFetcher;
import net.jitse.npclib.api.skin.Skin;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import sun.jvm.hotspot.ui.ObjectHistogramPanel;

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
    Inventory startInventory;
    ArrayList<String> npctext;
    ArrayList<UUID> cantClick = new ArrayList<>();
    public NPCSuper(String npcName, int skinID, String clickMessage, Sound clickSound, int clickWaitTime, Inventory startInv, String... npcTextExcludingName){
        name = npcName;
        id = skinID;
        clickMsg = clickMessage;
        clickSnd = clickSound;
        clickwait = clickWaitTime;
        startInventory = startInv;
        npctext = (ArrayList<String>) Arrays.asList(npcTextExcludingName);

    }

    @EventHandler
    public void pickupOnInv(PlayerPickupItemEvent e){
        try {
            if (e.getPlayer().getOpenInventory().getTopInventory().getName().equals("Recover Seized Items")) {
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
                event.getWhoClicked().sendMessage(Prefix.NPC + ChatColor.DARK_GRAY.toString() + " | " + name + ChatColor.GRAY + ": " + ChatColor.WHITE + clickMsg);
                event.getWhoClicked().playSound(event.getWhoClicked().getLocation(), clickSnd, 10, 1);
                cantClick.add(event.getWhoClicked().getUniqueId());
                new BukkitRunnable(){
                    public void run(){
                        event.getWhoClicked().openInventory(startInventory);
                    }
                }.runTaskLater(Bukkit.getPluginManager().getPlugin("Fallen"), clickwait);
            }
        }catch(NullPointerException ignored){}
    }

    @EventHandler
    public void closeInv(InventoryCloseEvent event){
        try{
            if(event.getInventory().getName().equals(startInventory.getTitle())){
                if(cantClick.contains(event.getPlayer().getUniqueId())) {
                    cantClick.remove(event.getPlayer().getUniqueId());
                }else{
                    Bukkit.getConsoleSender().sendMessage("Player " + event.getPlayer().getUniqueId() + " had the " + name + " NPC inventory open, but cantClick didn't contain their UUID! Something fishy's going down...");
                }
            }
        }catch(NullPointerException ignored){}
    }

    @EventHandler
    public void disconnectOnInv(PlayerQuitEvent e){
        cantClick.remove(e.getPlayer().getUniqueId());
    }
    @EventHandler
    public void kickOnInv(PlayerKickEvent e){cantClick.remove(e.getPlayer().getUniqueId());}
}
