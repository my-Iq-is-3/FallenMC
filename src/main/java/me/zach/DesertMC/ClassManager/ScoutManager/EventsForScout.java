package me.zach.DesertMC.ClassManager.ScoutManager;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.Utils.Config.ConfigUtils;
import me.zach.DesertMC.Utils.PlayerUtils;
import me.zach.DesertMC.Utils.ench.CustomEnch;
import me.zach.DesertMC.Utils.nbt.NBTUtil;
import me.zach.DesertMC.events.FallenDeathEvent;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
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

import java.util.*;

public class EventsForScout implements Listener {
    private static final EventsForScout INSTANCE = new EventsForScout();
    public static EventsForScout getInstance() {
        return INSTANCE;
    }

    public void medKit(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(player.getInventory().getItemInHand() != null){
            ItemStack handItem = player.getInventory().getItemInHand();
            if(NBTUtil.getCustomAttrString(handItem,"ID").equals("FIRST_AID_KIT")){
                if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
                    if(ConfigUtils.findClass(player).equals("scout")){
                        List<String> lore = handItem.getItemMeta().getLore();
                        NBTCompound nbtCompound = new NBTItem(handItem).getCompound("CustomAttributes");
                        int usesLeft = nbtCompound.getInteger("UsesLeft");
                        if(usesLeft == 1){
                            player.getInventory().remove(handItem);
                            return;
                        }
                        usesLeft--;
                        lore.set(5,ChatColor.DARK_GRAY + "Uses Left:" + ChatColor.YELLOW + usesLeft + ChatColor.GRAY + "/" + ChatColor.GREEN + "3");
                        if(player.getHealth() < player.getMaxHealth()){
                            player.setHealth(player.getHealth() + 4);
                            PlayerUtils.addAbsorption(player,4);
                        }
                    }
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void t1Event(FallenDeathEvent event){
        Player damager = event.getKiller();
        if(!event.isCancelled()){
            if(ConfigUtils.findClass(damager).equals("scout") && ConfigUtils.getLevel("scout", damager) > 1){
                PotionEffectType type = PotionEffectType.SPEED;
                PotionEffect speedpot = new PotionEffect(type, 80, 0);
                damager.addPotionEffect(speedpot, true);
            }
        }
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void t4Event(FallenDeathEvent event){
        if(!event.isCancelled()){
            Player damager = event.getKiller();
            if(damager != null && ConfigUtils.getLevel("scout", damager) > 4 && ConfigUtils.findClass(damager).equals("scout")){
                PlayerUtils.addAbsorption(damager, 4);
            }
        }
    }

    public void daggerHit(EntityDamageByEntityEvent event){
        if(!event.isCancelled() && event.getDamager() instanceof Player && event.getEntity() instanceof LivingEntity){
            Player damager = (Player) event.getDamager();
            LivingEntity damaged = (LivingEntity) event.getEntity();
            if(NBTUtil.getCustomAttrString(damaged.getEquipment().getItemInHand(), "ID").equals("SCOUT_DAGGER")){
                event.setCancelled(true);
                if(ConfigUtils.findClass(damager).equals("scout") && ConfigUtils.getLevel("scout", damager) > 4){
                    if(damager.getLocation().distanceSquared(damaged.getLocation()) <= 1){
                        event.setCancelled(false);
                        //damage bonus is handled by the ATTACK nbt tag
                    }
                }else{
                    damager.sendMessage(ChatColor.RED + "You must have the Scout class selected and past level 4 to use this item!");
                    damager.playSound(damager.getLocation(), Sound.NOTE_BASS, 10, 1);
                }
            }
        }
    }

    public void t8Event(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player){
            Player damager = (Player) event.getDamager();
            if(ConfigUtils.findClass(damager).equals("scout") && ConfigUtils.getLevel("scout",damager) > 8){
                if(damager.isSprinting()) event.setDamage(event.getDamage() * 1.2);
            }
        }
    }

    public void scoutBlade(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player && event.getEntity() instanceof LivingEntity){
            Player hitter = (Player) event.getDamager();
            LivingEntity hit = (LivingEntity) event.getEntity();
            try{
                if(!NBTUtil.getCustomAttrString(hitter.getItemInHand(), "ID").equals("SCOUT_BLADE")) return;
            }catch(NullPointerException ex){
                return;
            }
            if(ConfigUtils.getLevel("scout", hitter) > 6 && ConfigUtils.findClass(hitter).equals("scout")){
                if(!PlayerUtils.canSeeTarget(hitter.getEyeLocation(), hit.getLocation()) && !(DesertMain.scoutBladeCD.contains(hitter.getUniqueId()))){
                    hit.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50, 255), true);
                    event.setDamage(event.getDamage() + 2);
                    UUID uuid = hitter.getUniqueId();
                    DesertMain.scoutBladeCD.add(uuid);
                    new BukkitRunnable() {
                        @Override
                        public void run(){
                            DesertMain.scoutBladeCD.remove(uuid);
                        }
                    }.runTaskLater(DesertMain.getInstance, 340);
                }
            }else{
                hitter.sendMessage(ChatColor.RED + "You must have the scout class selected and past level 6 to fully use this item!");
                hitter.playSound(hitter.getLocation(), Sound.NOTE_BASS, 10, 1);
            }
        }
    }

    public void alert(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Player && event.getDamager() instanceof Player){
            Player damaged = (Player) event.getEntity();
            int level = CustomEnch.ALERT.getTotalArmorLevel(damaged);
            if(ConfigUtils.findClass(damaged).equals("wizard") && ConfigUtils.getLevel("wizard",damaged) > 4){
                if(level > 0 && PlayerUtils.isIdle(damaged)){
                    event.setDamage(event.getDamage() * (1 - 0.01 * level));
                }
            }
        }
    }
}
