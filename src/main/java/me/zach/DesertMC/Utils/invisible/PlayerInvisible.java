package me.zach.DesertMC.Utils.invisible;

import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

import static org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack.asNMSCopy;

public class PlayerInvisible implements Listener {
    public PlayerInvisible(Player player, Plugin plugin){
        this.uuid = player.getUniqueId();
        this.plugin = plugin;
    }

    Plugin plugin;
    UUID uuid;
    boolean invisible = false;

    public void setInvisible(boolean invisible){
        Player player = getPlayer();
        if(this.invisible != invisible){
            this.invisible = invisible;
            if(invisible){
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 255, false, false));
                Bukkit.getPluginManager().registerEvents(this, plugin);
            }else{
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
                HandlerList.unregisterAll(this);
            }
            PacketPlayOutEntityEquipment[] packets = equipmentPackets(player, invisible);
            for(Player otherPlayer : Bukkit.getOnlinePlayers()){
                sendPackets(packets, otherPlayer);
            }
        }
    }

    @EventHandler
    public void invisOnJoin(PlayerJoinEvent event){
        Player joined = event.getPlayer();
        if(!joined.getUniqueId().equals(uuid) && invisible){
            sendPackets(equipmentPackets(this.getPlayer(), true), joined);
        }
    }

    public boolean isInvisible(){
        return invisible;
    }

    public Player getPlayer(){
        return Bukkit.getPlayer(uuid);
    }

    public UUID getPlayerUUID(){
        return uuid;
    }

    private void sendPackets(Packet<?>[] packets, Player player){
        for(Player otherPlayer : Bukkit.getOnlinePlayers()){
            for(Packet<?> packet : packets){
                ((CraftPlayer) otherPlayer).getHandle().playerConnection.sendPacket(packet);
            }
        }
    }

    private PacketPlayOutEntityEquipment[] equipmentPackets(Player player, boolean clear){
        PlayerInventory inv = player.getInventory();
        //used constructor PacketPlayOutEntityEquipment(entity id, equipment slot (0 - held, 1 - boots, 2 - leggings, 3 - chestplate, 4 - helmet), item)
        //learn more about 1.8 minecraft protocol @ https://wiki.vg/index.php?title=Protocol&oldid=7368
        int eid = player.getEntityId();
        return new PacketPlayOutEntityEquipment[]{
                new PacketPlayOutEntityEquipment(eid, 1, clear ? null : asNMSCopy(inv.getBoots())),
                new PacketPlayOutEntityEquipment(eid, 2, clear ? null : asNMSCopy(inv.getLeggings())),
                new PacketPlayOutEntityEquipment(eid, 3, clear ? null : asNMSCopy(inv.getChestplate())),
                new PacketPlayOutEntityEquipment(eid, 4, clear ? null : asNMSCopy(inv.getHelmet()))
        };
    }
}
