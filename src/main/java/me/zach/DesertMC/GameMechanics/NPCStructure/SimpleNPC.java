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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.fallenmc.risenboss.main.RisenMain;
import xyz.fallenmc.risenboss.main.utils.RisenUtils;

import java.util.*;

public class SimpleNPC implements Listener {
    public final String name;
    int id;
    public String clickMsg;
    private Location queueReload;
    public final boolean moveHead;
    Sound clickSnd;
    public NPC npc;
    ArrayList<String> npctext = new ArrayList<>();
    public SimpleNPC(String npcName, int skinID, String clickMessage, Sound clickSound, boolean moveHead, String... npcTextExcludingName){
        this.moveHead = moveHead;
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
        SimpleNPC.this.npc = npc;
        MineSkinFetcher.fetchSkinFromIdSync(id, new MineSkinFetcher.Callback() {
            @Override
            public void call(Skin skin) {
                npc.setSkin(skin);
                npc.create();
                Bukkit.getLogger().info("Created npc " + SimpleNPC.this.getClass().getSimpleName() + " at (" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")");
                if(moveHead){
                    new BukkitRunnable() {
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
                }
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
    public void showOnJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if(npc != null){
            if(!npc.isShown(player)) npc.show(player);
        }else if(queueReload != null){
            createNPC(queueReload);
            queueReload = null;
        }
    }

    @EventHandler
    public void interact(NPCInteractEvent event){
        if(this.npc != null && event.getNPC().getUniqueId().equals(npc.getUniqueId())){
            if(!RisenUtils.isBoss(event.getWhoClicked().getUniqueId())){
                ClickResponse response = clickResponse(event);
                if(!response.absorb){
                    Player player = event.getWhoClicked();
                    npcMessage(player, response.message);
                    player.playSound(player.getLocation(), clickSnd, 10, 1);
                }
            }
        }
    }

    //override with parameters needed to contruct your subclass (if any)
    public List<?> params(){
        return new ArrayList<>();
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
        return new ClickResponse(clickMsg);
    }

    public static class ClickResponse {
        public final String message;
        public final boolean absorb;

        public ClickResponse(String message, boolean absorb){
            this.message = message;
            this.absorb = absorb;
        }

        public ClickResponse(String message){
            this(message,false);
        }

        public String toString(){
            return "ClickResponse{" +
                    "message='" + message + '\'' +
                    ", absord=" + absorb +
                    '}';
        }
    }
}
