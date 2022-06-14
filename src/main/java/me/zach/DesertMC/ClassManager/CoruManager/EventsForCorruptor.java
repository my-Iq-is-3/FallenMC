package me.zach.DesertMC.ClassManager.CoruManager;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.GameMechanics.Events;
import me.zach.DesertMC.Utils.Config.ConfigUtils;
import me.zach.DesertMC.Utils.MiscUtils;
import me.zach.DesertMC.Utils.nbt.NBTUtil;
import me.zach.DesertMC.Utils.Particle.ParticleEffect;
import me.zach.DesertMC.Utils.structs.Pair;
import me.zach.DesertMC.events.FallenDeathEvent;
import org.bukkit.*;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


public class EventsForCorruptor implements Listener {
    public static final EventsForCorruptor INSTANCE = new EventsForCorruptor();
    public static final Set<UUID> hf = new HashSet<>();
    public static final HashMap<UUID, Pair<UUID, Double>> combo = new HashMap<>();

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
    @EventHandler(priority = EventPriority.MONITOR)
    public void t1Event(FallenDeathEvent event) {
        Player damager = event.getKiller();
        if (!event.isCancelled() && ConfigUtils.getLevel("corrupter", damager) > 1 && ConfigUtils.findClass(damager).equals("corrupter")) {
            DesertMain.ct1players.add(damager.getUniqueId());
            new BukkitRunnable() {

                @Override
                public void run() {
                    DesertMain.ct1players.remove(damager.getUniqueId());
                }

            }.runTaskLater(DesertMain.getInstance, 60);
        }
    }
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void volcanicSword(FallenDeathEvent event) {
        Player killer = event.getKiller();
        ItemStack item = killer.getInventory().getItemInHand();
        if (NBTUtil.getCustomAttrString(item, "ID").equals("VOLCANIC_SWORD")) {
            if(ConfigUtils.findClass(killer).equals("corrupter") && ConfigUtils.getLevel("corrupter", killer) > 3){
                if (((Events.ks.get(killer.getUniqueId()) + 1) % 5) == 0) {
                    Bukkit.getScheduler().runTask(DesertMain.getInstance, () -> { //avoids infinite loop
                        Location eLoc = killer.getLocation();
                        for (Damageable near : MiscUtils.getNearbyDamageables(killer, 15)) {
                            near.damage(15, killer);
                            Location nearloc = near.getLocation();
                            Location newLoc = nearloc.subtract(eLoc);
                            Vector newV = newLoc.toVector().normalize().multiply(1.4);
                            newV.setY(newV.getY() + 1.7);
                            near.setVelocity(newV);
                        }
                        ParticleEffect.FLAME.display(0.5f,0.5f,0.5f,0.3f,100,killer.getLocation(),100);
                        killer.getWorld().playSound(killer.getLocation(), Sound.EXPLODE, 15, 1);
                    });
                }
            }else{
                killer.sendMessage(ChatColor.RED + "You must have the corrupter class selected and past level 3 to use this item!");
            }
        }
    }

    public void noMercy(EntityDamageByEntityEvent event){
        if(!event.isCancelled() && event.getDamager() instanceof Player && event.getEntity() instanceof Player){
            Player damager = (Player) event.getDamager();
            Player damaged = (Player) event.getEntity();
            if(damager.getInventory().getItemInHand() != null && damager.getInventory().getItemInHand().getType() != Material.AIR) {
                if (ConfigUtils.getLevel("corrupter", damager) > 6 && ConfigUtils.findClass(damager).equals("corrupter")) {
                    ItemStack heldItemStack = damager.getInventory().getItemInHand();
                    NBTItem hnbt = new NBTItem(heldItemStack);
                    if (hnbt.getCompound("CustomAttributes").getCompound("enchantments") != null) {
                        NBTCompound hnbtc = hnbt.getCompound("CustomAttributes").getCompound("enchantments");
                        int nomercylvl = hnbtc.getInteger("no_mercy");
                        if (nomercylvl > 0) {
                            event.setDamage(damaged.getHealth()/damaged.getMaxHealth()<=0.5 ? (1 + 0.03 * nomercylvl) * event.getDamage() : event.getDamage());
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
                event.setDamage(event.getDamage() * 1.15);
            }
        }
    }


    public void hf(Entity e, int duration){
        if(hf.contains(e.getUniqueId())) return;
        hf.add(e.getUniqueId());
        e.setFireTicks(duration);
        new BukkitRunnable(){
            public void run(){
                hf.remove(e.getUniqueId());
            }
        }.runTaskLater(DesertMain.getInstance, duration);
    }

    @EventHandler
    public void hFire(EntityDamageEvent e){
        if(EventsForCorruptor.hf.contains(e.getEntity().getUniqueId()) && e.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK))
            e.setDamage(e.getDamage() * 2);
    }

    public void corruptedSword(EntityDamageByEntityEvent event){
        if(event.isCancelled()) return;
        if(event.getDamager() instanceof Player){
            Player hitter = (Player) event.getDamager();
            if(hitter.getInventory().getItemInHand() != null){
                if(NBTUtil.getCustomAttrString(hitter.getInventory().getItemInHand(),"ID").equals("CORRUPTED_SWORD")){
                    if(ConfigUtils.getLevel("corrupter", hitter.getUniqueId()) > 6 && ConfigUtils.findClass(hitter.getUniqueId()).equals("corrupter")){
                        UUID uuid = hitter.getUniqueId();
                        UUID hitUUID = event.getEntity().getUniqueId();
                        if(combo.containsKey(uuid)){
                            Pair<UUID, Double> entry = combo.get(uuid);
                            if(entry.first.equals(hitUUID)){
                                entry.second += 0.1;
                                event.setDamage(event.getDamage() * entry.second);
                                hitter.playSound(hitter.getLocation(), Sound.NOTE_BASS_GUITAR, 10, (float) ((entry.second - 1) / 2) + 1);
                            }else combo.put(uuid, new Pair<>(hitUUID, 1d));
                        }else combo.put(uuid, new Pair<>(hitUUID, 1d));
                    }else hitter.sendMessage(ChatColor.RED + "You must have the corrupter class selected and past level 6 to fully use this item!");
                }
            }
            combo.remove(event.getEntity().getUniqueId());
        }
    }
    public void corrupterLeggings(Player killer, Player killed){
        Random rgen = ThreadLocalRandom.current();
        int r = rgen.nextInt(5);
        if(r == 0 && ConfigUtils.getLevel("corrupter", killed) > 5){
            hf(killer, 100);
        }
    }
}
