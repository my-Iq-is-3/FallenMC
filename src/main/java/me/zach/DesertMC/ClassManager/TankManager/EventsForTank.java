package me.zach.DesertMC.ClassManager.TankManager;

import de.tr7zw.nbtapi.NBTItem;
import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.GameMechanics.Events;
import me.zach.DesertMC.Utils.Config.ConfigUtils;
import me.zach.DesertMC.Utils.MiscUtils;
import me.zach.DesertMC.Utils.PlayerUtils;
import me.zach.DesertMC.Utils.ench.CustomEnch;
import me.zach.DesertMC.Utils.nbt.NBTUtil;
import me.zach.DesertMC.events.FallenDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class EventsForTank implements Listener {
    public static EventsForTank getInstance(){
        return new EventsForTank();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void t1Event(FallenDeathEvent event){
        if(!event.isCancelled()){
            Player damager = event.getKiller();
            if(ConfigUtils.findClass(damager).equals("tank") && ConfigUtils.getLevel("tank",damager) > 1){
                PotionEffectType type = PotionEffectType.DAMAGE_RESISTANCE;
                PotionEffect respot = new PotionEffect(type, 80, 0);
                damager.addPotionEffect(respot,true);
            }
        }

    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void t5Event(FallenDeathEvent event){
        if(!event.isCancelled()){
            Player damager = event.getKiller();
            Player damaged = event.getPlayer();
            if(ConfigUtils.findClass(damaged).equals("tank") && ConfigUtils.getLevel("tank",damaged) > 5){
                PotionEffect vengance = new PotionEffect(PotionEffectType.WEAKNESS,40 ,3);
                PotionEffect slow = new PotionEffect(PotionEffectType.SLOW,40, 0);
                damager.addPotionEffect(vengance, true);
                damager.addPotionEffect(slow);
            }
        }
    }
    public void t8Event(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player && event.getEntity() instanceof Player){
            Player damager = (Player) event.getDamager();
            if(ConfigUtils.getLevel("tank",damager) > 7 && ConfigUtils.findClass(damager).equals("tank")){
                if(damager.isSneaking()){
                    event.setDamage(event.getDamage() * 1.05);
                }
            }
        }
    }

    /**
     *
     * @deprecated No longer in use. Tank already has 2 enchantments
     */
    @Deprecated
    private void fortify(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player && event.getEntity() instanceof Player){
            Player damaged = (Player) event.getEntity();

            ItemStack[] armor = damaged.getInventory().getArmorContents();
            int level = 0;


            event.setDamage(event.getDamage() - event.getDamage()*(0.01*level));
            if(!DesertMain.slowed.contains(damaged.getUniqueId())){
                DesertMain.slowed.add(damaged.getUniqueId());
                float priorSpeed = damaged.getWalkSpeed();
                damaged.setWalkSpeed((float) (damaged.getWalkSpeed() - damaged.getWalkSpeed()*(level*0.02)));
                new BukkitRunnable(){
                    public void run(){
                        damaged.setWalkSpeed(priorSpeed);
                    }
                };
            }
        }
    }
    public void bludgeon(EntityDamageByEntityEvent e){
        if(e.getDamager() instanceof Player && e.getEntity() instanceof Player){
            Player damager = (Player) e.getDamager();
            Player damaged = (Player) e.getEntity();
            try{
                if(!NBTUtil.getCustomAttrString(damager.getItemInHand(), "ID").equals("BLUDGEON")) return;
            }catch(NullPointerException ignored){}
            int extradamage = 0;
            if(damager.getFallDistance() > 0.0f) {
                if (ConfigUtils.getLevel("tank", damager) > 3 && ConfigUtils.findClass(damager).equals("tank")) {
                    for (ItemStack item : damaged.getInventory().getArmorContents()) {
                        if (item != null) {
                            if (!item.getType().equals(Material.AIR) && !item.getType().toString().startsWith("CHAINMAIL")) {
                                extradamage += 1;
                            }
                        }
                    }
                }
                e.setDamage(e.getDamage() + extradamage);
            }
        }
    }
    @EventHandler
    public void stomper(PlayerInteractEvent e){
        if(e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            Player clicker = e.getPlayer();
            try{
                if(!NBTUtil.getCustomAttrString(clicker.getItemInHand(), "ID").equals("STOMPER")) return;
            }catch(NullPointerException ignored){}

            if (ConfigUtils.getLevel("tank", clicker) > 6 && ConfigUtils.findClass(clicker).equals("tank") && !DesertMain.stomperCD.contains(clicker.getUniqueId())) {
                if(!DesertMain.stomperStage.containsKey(clicker.getUniqueId())){
                    DesertMain.stomperStage.put(clicker.getUniqueId(), e.getClickedBlock());
                }else if(DesertMain.stomperStage.get(clicker.getUniqueId()).equals(e.getClickedBlock())){
                    Damageable closest = null;
                    for(Player player : e.getClickedBlock().getWorld().getPlayers()){
                        if(closest == null){
                            if(clicker.getLocation().distance(player.getLocation()) <= 7 && !player.getUniqueId().equals(clicker.getUniqueId())){
                                closest = player;
                            }
                        }else{
                            if(clicker.getLocation().distance(player.getLocation()) < clicker.getLocation().distance(closest.getLocation()) && clicker.getLocation().distance(player.getLocation()) <= 7 && !player.getUniqueId().equals(clicker.getUniqueId())){
                                closest = MiscUtils.canDamage(player);
                            }
                        }
                    }
                    if(closest != null) {
                        DesertMain.stomperCD.add(clicker.getUniqueId());
                        DesertMain.stomperStage.remove(clicker.getUniqueId());
                        clicker.sendMessage(ChatColor.GOLD + "Damaged " + ChatColor.RED + closest.getName() + ChatColor.GOLD + " for 4 true damage!");
                        clicker.playSound(clicker.getLocation(), Sound.ENDERDRAGON_HIT, 10, 1);
                        PlayerUtils.trueDamage(closest, 4, clicker);
                        Vector velocity = closest.getVelocity();
                        closest.setVelocity(velocity.setY(velocity.getY() + 2));
                        new BukkitRunnable() {
                            public void run() {
                                DesertMain.stomperCD.remove(clicker.getUniqueId());
                            }
                        }.runTaskLater(Bukkit.getPluginManager().getPlugin("Fallen"), 140);
                    }else{
                        clicker.sendMessage(ChatColor.RED + "No players nearby!");
                        clicker.playSound(clicker.getLocation(), Sound.GLASS, 10, 1);
                    }
                }else{
                    DesertMain.stomperStage.remove(clicker.getUniqueId());
                }
            }
        }
    }


}
