package me.zach.DesertMC.GameMechanics.NPCStructure;

import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.Prefix;
import me.zach.DesertMC.Utils.MiscUtils;
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
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public abstract class NPCSuper implements Listener {
    public final String name;
    int id;
    public String clickMsg;
    private Location queueReload;
    Sound clickSnd;
    public NPC npc;
    ArrayList<String> npctext = new ArrayList<>();
    public Set<UUID> cantClick = new HashSet<>();
    public Set<UUID> openInv = new HashSet<>();
    public NPCSuper(String npcName, int skinID, String clickMessage, Sound clickSound, String... npcTextExcludingName){
        name = npcName;
        id = skinID;
        clickMsg = clickMessage;
        clickSnd = clickSound;
        npctext.addAll(Arrays.asList(npcTextExcludingName));
        Bukkit.getPluginManager().registerEvents(this, DesertMain.getInstance);
    }

    public final void npcMessage(Player p, String message){
        p.sendMessage(Prefix.NPC + ChatColor.DARK_GRAY.toString() + " | " + name + ChatColor.GRAY + ": " + ChatColor.WHITE + message);
    }

    @EventHandler
    public final void pickupOnInv(PlayerPickupItemEvent e){
        if(openInv.contains(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
        }
    }

    public final void createNPC(Location loc){
        if(Bukkit.getOnlinePlayers().isEmpty()){
            //DON'T ask. This is the pinnacle of hours upon hours of debugging and I'm not about to spend more time finding a better solution.
            queueReload = loc;
            return;
        }
        NPCLib library = DesertMain.getNPCLib();
        List<String> text = npctext;
        if(!text.get(0).equals(name)) text.add(0, name);
        NPC npc = library.createNPC(text);
        npc.setLocation(loc);
        NPCSuper.this.npc = npc;
        MineSkinFetcher.fetchSkinFromIdSync(id, new MineSkinFetcher.Callback() {
            @Override
            public void call(Skin skin) {
                npc.setSkin(skin);
                npc.create();
                Bukkit.getLogger().info("Created npc " + NPCSuper.this.getClass().getSimpleName() + " at (" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")");
                new BukkitRunnable(){
                    public void run(){
                        if(npc.isCreated()){
                            if(Bukkit.getOnlinePlayers().size() > 0){
                                Player closest = MiscUtils.getClosest(npc.getLocation());
                                npc.lookAt(closest.getLocation());
                            }
                        }else{
                            cancel();
                        }
                    }
                }.runTaskTimer(DesertMain.getInstance, 0, 2);
                for(Player player : Bukkit.getServer().getOnlinePlayers()){
                    npc.show(player);
                }
            }
            @Override
            public void failed(){
                Bukkit.getLogger().warning("Skin fetch failed! NPC: " + getClass().getSimpleName());
            }
        });
    }

    @EventHandler
    public final void npcClick(NPCInteractEvent event){
        Player player = event.getWhoClicked();
        UUID uuid = player.getUniqueId();
        ClickResponse response = clickResponse(event);
        if (npc != null && npc.getUniqueId().equals(event.getNPC().getUniqueId())){
            Bukkit.getLogger().info("NPC click registered. Player: " + player.getName() + ", NPC: " + name + "\nResponse: " + response);
            if(response.success){
                if(!cantClick.contains(uuid) && !player.isSneaking()){
                    npcMessage(player, response.message);
                    player.playSound(event.getWhoClicked().getLocation(), clickSnd, 10, 1);
                    cantClick.add(player.getUniqueId());
                    Bukkit.getScheduler().runTaskLater(DesertMain.getInstance, () -> player.sendMessage(ChatColor.DARK_GRAY + "Click me again to open the " + name + ChatColor.DARK_GRAY + " menu"), 30);
                    Bukkit.getScheduler().runTaskLater(DesertMain.getInstance, () -> cantClick.remove(uuid), 300);
                }else{
                    player.openInventory(getStartInventory(event));
                    openInv.add(player.getUniqueId());
                    player.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 10, 1);
                    cantClick.remove(player.getUniqueId());
                }
            }else{
                npcMessage(player, response.message);
                player.playSound(player.getLocation(), clickSnd, 10, 0.95f);
            }
        }
    }

    public void destroyNPC(){
        npc.destroy();
        npc = null;
    }

    public void reloadNPC(){
        Location location = npc.getLocation();
        destroyNPC();
        createNPC(location);
    }

    //override this method to control if a player opens an npc's inventory, and what message they get depending on your own code.
    public ClickResponse clickResponse(NPCInteractEvent event){
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

    @EventHandler
    public void showOnJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if(npc != null){
            npc.show(player);
        }else if(queueReload != null){
            createNPC(queueReload);
            npc.show(player);
            queueReload = null;
        }
    }

    public void saveCurrent(Plugin plugin){
        List<SavedNPC> savedNPCs = stored(plugin);
        SavedNPC saved = new SavedNPC(this); //if this throws an exception, you have to run .createNPC() on this NPCSuper object first.
        savedNPCs.add(saved);
        plugin.getConfig().set(SavedNPC.PATH, savedNPCs);
        plugin.saveConfig();
    }

    public static List<SavedNPC> stored(Plugin plugin){
        return MiscUtils.ensureDefault(SavedNPC.PATH, new ArrayList<>(), plugin);
    }

    public static class ClickResponse {
        public final String message;
        public final boolean success;

        public ClickResponse(String message, boolean success){
            this.message = message;
            this.success = success;
        }

        public String toString(){
            return "ClickResponse{" +
                    "message='" + message + '\'' +
                    ", success=" + success +
                    '}';
        }
    }
}