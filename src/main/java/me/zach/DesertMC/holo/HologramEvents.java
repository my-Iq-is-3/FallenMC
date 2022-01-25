package me.zach.DesertMC.holo;

import de.tr7zw.nbtapi.NBTItem;
import me.zach.DesertMC.Utils.nbt.NBTUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class HologramEvents implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        ItemStack item = event.getItem();
        if(item != null){
            NBTItem nbt = new NBTItem(item);
            if(NBTUtil.getCustomAttrString(nbt, "ID").equals("HOLOGRAM_WAND")){
                String name = getName(NBTUtil.getCustomAttrString(nbt, "HOLOGRAM_NAME"));
                Action action = event.getAction();
                Player player = event.getPlayer();
                if(action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR){
                    new Hologram(name, player.getLocation().clone().add(0, 1.4, 0)).create();
                }else if(action == Action.LEFT_CLICK_BLOCK){
                    new Hologram(name, event.getClickedBlock().getLocation().add(0.5, 1.5, 0.5)).create();
                }
            }
        }
    }

    @EventHandler
    public void spawnRiding(PlayerInteractAtEntityEvent event){
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();
        if(item != null){
            Entity entity = event.getRightClicked();
            NBTItem nbt = new NBTItem(item);
            if(NBTUtil.getCustomAttrString(nbt, "ID").equals("HOLOGRAM_WAND")){
                String name = getName(NBTUtil.getCustomAttrString(nbt, "HOLOGRAM_NAME"));
                new Hologram(name, entity).create();
            }
        }
    }

    private String getName(String nbt){
        return nbt.replaceAll("_", " ");
    }
}
