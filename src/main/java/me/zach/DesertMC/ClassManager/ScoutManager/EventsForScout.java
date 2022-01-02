package me.zach.DesertMC.ClassManager.ScoutManager;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.Utils.Config.ConfigUtils;
import me.zach.DesertMC.Utils.PlayerUtils;
import me.zach.DesertMC.Utils.ench.CustomEnch;
import me.zach.DesertMC.Utils.nbt.NBTUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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
    
    public void t1Event(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player){
            Player damager = (Player) event.getDamager();
            if(ConfigUtils.findClass(damager).equals("scout") && ConfigUtils.getLevel("scout",damager) > 1){
                if(event.getEntity() instanceof Player){
                    Player damagedEntity = (Player) event.getEntity();
                    if(damagedEntity.getHealth() - event.getDamage() < 0.1){
                        PotionEffectType type = PotionEffectType.SPEED;
                        PotionEffect speedpot = new PotionEffect(type, 80, 0);
                        damager.addPotionEffect(speedpot,true);

                    }
                }
            }
        }

    }

    public void t4Event(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player && event.getEntity() instanceof Player){
            Player damager = (Player) event.getDamager();
            Player damaged = (Player) event.getEntity();
            if(damaged.getHealth() - event.getDamage() < 0.1){

                if(ConfigUtils.getLevel("scout",damager) > 4 && ConfigUtils.findClass(damager).equals("scout")){
                    PotionEffect absorb = new PotionEffect(PotionEffectType.ABSORPTION,10000,0,true,false);
                    damager.addPotionEffect(absorb);
                }

            }
        }
    }

    public void daggerHit(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player && event.getEntity() instanceof LivingEntity){
            Player damager = (Player) event.getDamager();
            LivingEntity damaged = (LivingEntity) event.getEntity();
            if(ConfigUtils.getLevel("scout",damager) > 4 && ConfigUtils.findClass(damager).equals("scout")){
                if(NBTUtil.getCustomAttrString(damager.getInventory().getItemInHand(),"ID").equals("SCOUT_DAGGER")){
                    if(damager.getLocation().distance(damaged.getLocation()) > 1){
                        event.setCancelled(true);
                        event.setDamage(8.5);
                    }
                }
            }
        }
    }

    public void t8Event(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player){
            Player damager = (Player) event.getDamager();
            if(ConfigUtils.findClass(damager).equals("scout") && ConfigUtils.getLevel("scout",damager) > 8){
                if(damager.isSprinting()) event.setDamage(event.getDamage() * 1.05);
            }
        }
    }

    public void scoutBlade(Player hitter, Player hit){
        try{
            if(!NBTUtil.getCustomAttrString(hitter.getItemInHand(), "ID").equals("SCOUT_BLADE")) return;
        }catch(NullPointerException ignored){}
        if(ConfigUtils.getLevel("scout", hitter) > 6 && ConfigUtils.findClass(hitter).equals("scout")) {
            if (hit.canSee(hitter) && !(DesertMain.scoutBladeCD.contains(hitter.getUniqueId()))) {
                hit.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50, 2));
                DesertMain.scoutBladeCD.add(hitter.getUniqueId());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        DesertMain.scoutBladeCD.remove(hitter.getUniqueId());
                    }
                }.runTaskLater(DesertMain.getInstance, 340);
            }
        }

    }

    public void alert(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Player && event.getDamager() instanceof Player){

            Player damager = (Player) event.getDamager();
            Player damaged = (Player) event.getEntity();
            int level = CustomEnch.ALERT.getTotalArmorLevel(damaged);


            if(ConfigUtils.findClass(damaged).equals("wizard") && ConfigUtils.getLevel("wizard",damaged) > 4){
                try{
                    if(!DesertMain.alertEnchantment.get(damaged.getUniqueId()).contains(damager.getUniqueId())){
                        if(DesertMain.alertEnchantment.get(damaged.getUniqueId()) == null){
                            DesertMain.alertEnchantment.put(damaged.getUniqueId(), Collections.singletonList(damager.getUniqueId()));
                        }else{
                            DesertMain.alertEnchantment.get(damaged.getUniqueId()).add(damager.getUniqueId());
                        }

                        event.setDamage(event.getDamage() - event.getDamage()*(0.03*level));
                        new BukkitRunnable(){
                            @Override
                            public void run(){
                                DesertMain.alertEnchantment.get(damaged.getUniqueId()).remove(damager.getUniqueId());
                            }

                        }.runTaskLater(DesertMain.getInstance,300);
                    }
                }catch(NullPointerException ignored){}


            }
        }
    }

}
