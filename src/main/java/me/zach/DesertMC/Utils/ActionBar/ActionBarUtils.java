package me.zach.DesertMC.Utils.ActionBar;

import me.zach.DesertMC.DesertMain;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class ActionBarUtils {
    static HashMap<UUID, ActionBar> actionBars = new HashMap<>();
    public static void sendActionBar(Player player, String message){
        if(!actionBars.containsKey(player.getUniqueId())){
            sendActionBar0(player, message);
        }
    }

    public static void sendActionBar(Player player, String message, int duration){
        UUID uuid = player.getUniqueId();
        if(!actionBars.containsKey(uuid)){
            SimpleActionBar actionBar = new SimpleActionBar(message);
            setActionBar(player, actionBar);
            Bukkit.getScheduler().runTaskLater(DesertMain.getInstance, () -> {
                if(getActionBar(uuid) == actionBar)
                    clearActionBar(player);
            }, duration);
        }
    }

    public static void setActionBar(Player player, ActionBar actionBar){
        UUID uuid = player.getUniqueId();
        actionBars.put(uuid, actionBar);
        if(task == -1) init();
        sendActionBar0(player, actionBar.getMessage());
    }

    public static ActionBar getActionBar(UUID uuid){
        return actionBars.get(uuid);
    }

    public static void clearActionBar(Player player){
        actionBars.remove(player.getUniqueId());
        sendActionBar0(player, "");
    }

    static int task = -1;

    private static void init(){
        task = Bukkit.getScheduler().runTaskTimer(DesertMain.getInstance, () -> {
            for(Map.Entry<UUID, ActionBar> bar : actionBars.entrySet()){
                sendActionBar0(Bukkit.getPlayer(bar.getKey()), bar.getValue().getMessage());
            }
        }, 0, 40).getTaskId();
    }

    protected static void sendActionBar0(Player player, String message){
        PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(message), (byte) 2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
}