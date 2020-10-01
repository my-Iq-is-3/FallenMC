package me.zach.DesertMC.ClassManager.TankManager;

import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.Utils.Config.ConfigUtils;
import me.zach.DesertMC.Utils.PlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class EventsForTank {
    public static EventsForTank getInstance(){
        return new EventsForTank();
    }


    public void t1Event(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player){
            Player damager = (Player) event.getDamager();
            if(ConfigUtils.findClass(damager).equals("tank") && ConfigUtils.getLevel("tank",damager) > 1){
                if(event.getEntity() instanceof Player){
                    Player damagedEntity = (Player) event.getEntity();
                    if(damagedEntity.getHealth() - event.getDamage() < 0.1){
                        PotionEffectType type = PotionEffectType.DAMAGE_RESISTANCE;
                        PotionEffect respot = new PotionEffect(type, 80, 0);
                        damager.addPotionEffect(respot,true);
                    }
                }
            }
        }

    }

    public void t5Event(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player && event.getEntity() instanceof Player){
            Player damager = (Player) event.getDamager();
            Player damaged = (Player) event.getEntity();
            if(damaged.getHealth() - event.getDamage() < 0.1){
                if(ConfigUtils.findClass(damager).equals("tank") && ConfigUtils.getLevel("tank",damaged) > 4){
                    PotionEffect vengance = new PotionEffect(PotionEffectType.WEAKNESS,40 ,3 );
                    PotionEffect slow = new PotionEffect(PotionEffectType.SLOW,40 , 0);
                    damager.addPotionEffect(vengance);
                    damager.addPotionEffect(slow);
                }
            }
        }
    }
    public void t8Event(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player && event.getEntity() instanceof Player){
            Player damager = (Player) event.getDamager();
            if(ConfigUtils.getLevel("tank",damager) > 7 && ConfigUtils.findClass(damager).equals("tank")){
                if(DesertMain.crouchers.get(damager.getUniqueId())){
                    event.setDamage(event.getDamage() * 1.05);
                }
            }
        }
    }

    public void fortify(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player && event.getEntity() instanceof Player){
            Player damager = (Player) event.getDamager();
            Player damaged = (Player) event.getEntity();

            ItemStack[] armor = PlayerUtils.getArmor(damaged);
            int level = 0;

            for(int i=0;i<5;i++){
                if(armor[i] != null){
                    NBTItem nbtarmor = new NBTItem(armor[i]);
                    try{
                        level += nbtarmor.getCompound("CustomAttributes").getCompound("enchantments").getInteger("fortify");
                    }catch(NullPointerException ignored){}

                }
            }

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
}
