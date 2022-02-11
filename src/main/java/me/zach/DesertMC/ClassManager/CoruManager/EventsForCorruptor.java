package me.zach.DesertMC.ClassManager.CoruManager;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.GameMechanics.Events;
import me.zach.DesertMC.Utils.Config.ConfigUtils;
import me.zach.DesertMC.Utils.nbt.NBTUtil;
import me.zach.DesertMC.Utils.Particle.ParticleEffect;
import me.zach.DesertMC.events.FallenDeathEvent;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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
    public static final HashMap<UUID, Double> combo = new HashMap<>();

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
    @EventHandler
    public void t1Event(FallenDeathEvent event) {
        Player damager = event.getKiller();
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

    public void volcanicSword(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player killer = (Player) event.getDamager();
            Player killed = (Player) event.getEntity();
            if (killed.getHealth() - event.getDamage() < 0.1) {
                if (!killer.getInventory().getItemInHand().getType().equals(Material.AIR)) {
                    ItemStack item = killer.getInventory().getItemInHand();
                    if (NBTUtil.getCustomAttrString(item, "ID").equals("VOLCANIC_SWORD")) {
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

    public void noMercy(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player && event.getEntity() instanceof Player){
            Player damager = (Player) event.getDamager();
            Player damaged = (Player) event.getEntity();
            if(damaged.getHealth() - event.getDamage() < 0.1){
                if(damager.getInventory().getItemInHand().getType() != Material.AIR) {
                    if (ConfigUtils.getLevel("corrupter", damager) > 6 && ConfigUtils.findClass(damager).equals("corrupter")) {
                        ItemStack heldItemStack = damager.getInventory().getItemInHand();
                        NBTItem hnbt = new NBTItem(heldItemStack);

                        if (hnbt.getCompound("CustomAttributes").getCompound("enchantments") != null) {
                            NBTCompound hnbtc = hnbt.getCompound("CustomAttributes").getCompound("enchantments");
                            int nomercylvl = hnbtc.getInteger("no_mercy");
                            if (nomercylvl > 0) {
                                event.setDamage(damaged.getHealth()/damaged.getMaxHealth()<=0.5 ? ((2*nomercylvl)/100f)*event.getDamage() : event.getDamage());
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


    public void hf(Entity e, int duration){
        if(hf.contains(e.getUniqueId())) return;
        hf.add(e.getUniqueId());
        e.setFireTicks(duration);
        new BukkitRunnable(){
            public void run(){
                hf.remove(e.getUniqueId());
                e.setFireTicks(0);
            }
        }.runTaskLater(DesertMain.getInstance, duration);
    }

    @EventHandler
    public void hFire(EntityDamageEvent e){
        if(EventsForCorruptor.hf.contains(e.getEntity().getUniqueId()) && e.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK))
            e.setDamage(e.getDamage() * 2);
    }

    public void corruptedSword(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player){
            Player hitter = (Player) event.getDamager();
            if(hitter.getInventory().getItemInHand() != null){
                if(NBTUtil.getCustomAttrString(hitter.getInventory().getItemInHand(),"ID").equals("CORRUPTED_SWORD")){
                    UUID uuid = hitter.getUniqueId();
                    if(combo.containsKey(uuid)) {
                        event.setDamage(event.getDamage() * (1 + combo.get(uuid)));
                        combo.put(uuid, combo.get(uuid) + 0.25);
                        hitter.playSound(hitter.getLocation(), Sound.NOTE_PLING, 10, 1 + (float) (combo.get(uuid) / 2));
                    }else combo.put(uuid, 0.25);
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
