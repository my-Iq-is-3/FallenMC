package me.zach.DesertMC.Utils.invisible;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.Utils.packet.wrappers.WrapperPlayServerEntityEquipment;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class PlayerInvisible implements Listener {
    public PlayerInvisible(Player player, Plugin plugin){
        this.uuid = player.getUniqueId();
        this.plugin = plugin;
    }

    Plugin plugin;
    UUID uuid;
    int entityId = getPlayer().getEntityId();
    boolean invisible = false;
    PacketListener listener = new PacketListener() {
        final ListeningWhitelist whitelist = ListeningWhitelist.newBuilder().types(WrapperPlayServerEntityEquipment.TYPE).build();
        public void onPacketSending(PacketEvent event){
            PacketContainer packet = event.getPacket();
            if(invisible && packet.getType().equals(WrapperPlayServerEntityEquipment.TYPE)){
                WrapperPlayServerEntityEquipment wrapper = new WrapperPlayServerEntityEquipment(packet);
                if(wrapper.getEntityID() == entityId){
                    wrapper.setItem(null);
                    event.setPacket(wrapper.getHandle());
                }
            }
        }

        public void onPacketReceiving(PacketEvent event){

        }

        public ListeningWhitelist getSendingWhitelist(){
            return whitelist;
        }

        public ListeningWhitelist getReceivingWhitelist(){
            return null;
        }

        public Plugin getPlugin(){
            return DesertMain.getInstance;
        }
    };

    public void setInvisible(boolean invisible){
        Player player = getPlayer();
        if(this.invisible != invisible){
            this.invisible = invisible;
            WrapperPlayServerEntityEquipment[] packets = equipmentPackets(player, invisible);
            if(invisible){
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE - 1000, 255, false, false));
                Bukkit.getPluginManager().registerEvents(this, plugin);
                sendPackets(packets);
                ProtocolLibrary.getProtocolManager().addPacketListener(listener);
            }else{
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
                HandlerList.unregisterAll(this);
                ProtocolLibrary.getProtocolManager().removePacketListener(listener);
                sendPackets(packets);
            }
        }
    }

    @EventHandler
    public void removeRenew(PlayerQuitEvent event){
        setInvisible(false);
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

    private void sendPackets(WrapperPlayServerEntityEquipment[] packets, Player player){
        for(WrapperPlayServerEntityEquipment packet : packets){
            packet.sendPacket(player);
        }
    }

    private void sendPackets(WrapperPlayServerEntityEquipment[] packets){
        for(Player otherPlayer : Bukkit.getOnlinePlayers()){
            if(!otherPlayer.getUniqueId().equals(uuid)){
                sendPackets(packets, otherPlayer);
            }
        }
    }

    private WrapperPlayServerEntityEquipment[] equipmentPackets(Player player, boolean clear){
        PlayerInventory inv = player.getInventory();
        return new WrapperPlayServerEntityEquipment[]{
                WrapperPlayServerEntityEquipment.create(entityId, EnumWrappers.ItemSlot.MAINHAND, clear ? null : inv.getItemInHand()),
                WrapperPlayServerEntityEquipment.create(entityId, EnumWrappers.ItemSlot.FEET, clear ? null : inv.getBoots()),
                WrapperPlayServerEntityEquipment.create(entityId, EnumWrappers.ItemSlot.LEGS, clear ? null : inv.getLeggings()),
                WrapperPlayServerEntityEquipment.create(entityId, EnumWrappers.ItemSlot.CHEST, clear ? null : inv.getChestplate()),
                WrapperPlayServerEntityEquipment.create(entityId, EnumWrappers.ItemSlot.HEAD, clear ? null : inv.getHelmet())
        };
    }
}
