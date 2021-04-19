package me.zach.DesertMC.ClassManager.CoruManager;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTEntity;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtinjector.NBTInjector;
import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.GameMechanics.Events;
import me.zach.DesertMC.Utils.Config.ConfigUtils;
import me.zach.DesertMC.Utils.nbt.NBTUtil;
import me.zach.DesertMC.Utils.Particle.ParticleEffect;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;


public class EventsForCorruptor implements Listener {
    public static final EventsForCorruptor INSTANCE = new EventsForCorruptor();
    public static final ArrayList<UUID> hf = new ArrayList<>();

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
                if (!killer.getInventory().getItemInHand().getType().equals(Material.AIR)) {
                    ItemStack item = killer.getInventory().getItemInHand();
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

    private void hf(Entity e, long duration){
        if(hf.contains(e.getUniqueId())) return;
        hf.add(e.getUniqueId());
        Location playerLocation = e.getLocation();
        Material typeBefore = playerLocation.getBlock().getType();
        playerLocation.getBlock().setType(Material.FIRE);
        new BukkitRunnable(){
            public void run(){
                playerLocation.getBlock().setType(typeBefore);
                hf.remove(e.getUniqueId());
            }
        }.runTaskLater(Bukkit.getPluginManager().getPlugin("Fallen"), duration);
    }

    public void corruptedSword(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player){
            Player hitter = (Player) event.getDamager();
            if(hitter.getInventory().getItemInHand() != null){
                if(NBTUtil.INSTANCE.getCustomAttr(hitter.getInventory().getItemInHand(),"ID").equals("CORRUPTED_SWORD")){
                    Random rgen = new Random();
                    final int RANDOM_INTEGER = rgen.nextInt(10);
                    if(RANDOM_INTEGER + 1 < 2){
                        hf(event.getEntity(), 100);
                    }
                }
            }
        }
    }
    public void corrupterLeggings(Player killer, Player killed){
        Random rgen = new Random();
        int r = rgen.nextInt(5);
        if(r == 0 && ConfigUtils.getLevel("corrupter", killed) > 5){
            Entity hfa = killer.getWorld().spawnEntity(killer.getLocation().subtract(0, 10, 0), EntityType.ARMOR_STAND);
            hfa = NBTInjector.patchEntity(hfa);
            NBTCompound hfanbt = NBTInjector.getNbtData(hfa);
            hfanbt.mergeCompound(new NBTContainer("{Marker:1b,Hellfire:1b,Unbreakable:1b,Invisible:0b,Owner:" + killer.getUniqueId().toString() + "}"));
            killer.getLocation().getBlock().setType(Material.FIRE);
        }
    }
}
