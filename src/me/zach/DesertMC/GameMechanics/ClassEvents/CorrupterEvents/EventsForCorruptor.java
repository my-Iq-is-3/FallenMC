package me.zach.DesertMC.GameMechanics.ClassEvents.CorrupterEvents;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.PlayerManager.Events;
import me.zach.DesertMC.Utils.Config.ConfigUtils;
import me.zach.DesertMC.Utils.nbt.NBTUtil;
import me.zach.DesertMC.Utils.Particle.ParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;


public class EventsForCorruptor {
    public static final EventsForCorruptor INSTANCE = new EventsForCorruptor();

    public void fort4(EntityDamageEvent event){
        if(event.getCause().equals(EntityDamageEvent.DamageCause.LAVA) || event.getCause().equals(EntityDamageEvent.DamageCause.FIRE) || event.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK)){
            if(event.getEntity() instanceof Player){
                Player player = (Player) event.getEntity();
                if(ConfigUtils.findClass(player).equals("corrupter") && ConfigUtils.getLevel("corrupter",player) > 4){
                    event.setCancelled(true);
                }
            }
        }
    }

    public void t1Event(EntityDamageByEntityEvent event) {
        PlayerInteractEvent event1;
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player damaged = (Player) event.getEntity();
            Player damager = (Player) event.getDamager();
            if (damaged.getHealth() - event.getDamage() < 0.1) {

                if (ConfigUtils.getLevel("corrupter", damager) > 1 && ConfigUtils.findClass(damager).equals("corrupter")) {
                    DesertMain.ct1players.add(damager.getUniqueId());
                    new BukkitRunnable() {

                        @Override
                        public void run() {

                            DesertMain.ct1players.remove(damager.getUniqueId());
                        }

                    }.runTaskLater(DesertMain.getInstance, 60);
                }

            }
        }

    }

    public void volcanicSword(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player killer = (Player) event.getDamager();
            Player killed = (Player) event.getEntity();
            if (killed.getHealth() - event.getDamage() < 0.1) {
                if (!killer.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
                    ItemStack item = killer.getInventory().getItemInMainHand();
                    if (NBTUtil.INSTANCE.getCustomAttr(item, "ID").equals("VOLCANIC_SWORD")) {
                        if(ConfigUtils.findClass(killer).equals("corrupter") && ConfigUtils.getLevel("corrupter", killer) > 3){
                            if (((Events.ks.get(killer.getUniqueId()) + 1) % 5) == 0) {
                                for (Entity near : Bukkit.getOnlinePlayers()) {
                                    if (near.getLocation().distance(killer.getLocation()) <= 5 && !near.equals(killer)) {
                                        Location nearloc = near.getLocation();
                                        Location eLoc = killer.getLocation();
                                        Location newLoc = nearloc.subtract(eLoc);
                                        Vector newV = newLoc.toVector().normalize().multiply(1.4);
                                        newV.setY(2);
                                        near.setVelocity(newV);
                                        ParticleEffect.FLAME.display(0.5f,0.5f,0.5f,0.3f,100,killer.getLocation(),100);
                                    }
                                }
                            }
                        }else{
                            killer.sendMessage(ChatColor.RED + "You are not high enough level to use this!");
                        }
                    }
                }

            }
        }
    }
// at me.zach.DesertMC.GameMechanics.ClassEvents.CorrupterEvents.EventsForCorruptor.noMercy(EventsForCorruptor.java:127) ~[?:?]
    public void noMercy(EntityDamageByEntityEvent event){


        if(event.getDamager() instanceof Player && event.getEntity() instanceof Player){
            Player damager = (Player) event.getDamager();
            Player damaged = (Player) event.getEntity();
            if(damaged.getHealth() - event.getDamage() < 0.1){
                if(damager.getInventory().getItemInMainHand().getType() != Material.AIR) {
                    if (ConfigUtils.getLevel("corrupter", damager) > 6 && ConfigUtils.findClass(damager).equals("corrupter")) {
                        ItemStack heldItemStack = damager.getInventory().getItemInMainHand();
                        NBTItem hnbt = new NBTItem(heldItemStack);
                        if (hnbt.getCompound("CustomAttributes").getCompound("enchantments") != null) {

                            NBTCompound hnbtc = hnbt.getCompound("CustomAttributes").getCompound("enchantments");
                            int nomercylvl = hnbtc.getInteger("no_mercy");
                            if (nomercylvl > 0) {

                                if ((Events.ks.get(damager.getUniqueId()) + 1) % 2 == 0) {

                                    for (Player player : Bukkit.getOnlinePlayers()) {
                                        if (!player.equals(damager)) {
                                            ParticleEffect.SMOKE_NORMAL.display(0,0,0,0,10,player.getLocation().clone().add(0,2,0));
                                            new BukkitRunnable(){
                                                @Override
                                                public void run(){
                                                    if (player.getLocation().distance(damager.getLocation()) <= 6) {

                                                        player.damage(nomercylvl * 0.5, damager);
                                                        ParticleEffect.SMOKE_NORMAL.display(0.3f,0.3f,0.3f,0.3f,30,player.getLocation().clone().add(0,2,0));
                                                    }
                                                }
                                            }.runTaskLater(DesertMain.getInstance,10);

                                        }
                                    }
                                }

                            }
                        }

                    }
                }

            }

        }
    }

    public void t8Event(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player){
            Player damager = (Player) event.getDamager();
            if(ConfigUtils.getLevel("corrupter",damager) > 8 && ConfigUtils.findClass(damager).equals("corrupter")){
                event.setDamage(event.getDamage() * 1.05);
            }
        }
    }
}
