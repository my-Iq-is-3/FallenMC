package me.zach.DesertMC.GameMechanics.ClassEvents.CorrupterEvents;

import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.PlayerManager.Events;
import me.zach.DesertMC.Prefix;
import me.zach.DesertMC.Utils.Config.ConfigUtils;
import me.zach.DesertMC.Utils.NBTUtil;
import me.zach.DesertMC.Utils.Particle.ParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class EventsForCorruptor {
    public static final EventsForCorruptor INSTANCE = new EventsForCorruptor();


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
                if (killer.getInventory().getItemInMainHand() != null) {
                    ItemStack item = killer.getInventory().getItemInMainHand();
                    if (NBTUtil.INSTANCE.getCustomAttr(item, "ID").equals("VOLCANIC_SWORD")) {
                        if(ConfigUtils.findClass(killer).equals("corrupter") && ConfigUtils.getLevel("corrupter", killer) > 3){
                            if ((Events.ks.get(killer.getUniqueId()) % 5) == 0) {
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
}
