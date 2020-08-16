package me.zach.DesertMC.GameMechanics.ClassEvents.WizardEvents;

import de.tr7zw.nbtapi.NBTItem;
import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.Utils.Config.ConfigUtils;
import me.zach.DesertMC.Utils.Particle.ParticleEffect;
import me.zach.DesertMC.Utils.nbt.NBTUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;


import java.util.Random;


public class EventsForWizard implements Listener {

    public static final EventsForWizard INSTANCE = new EventsForWizard();


//  Invisibility
    public void wizardt1(EntityDamageByEntityEvent event) {

        if (event.getDamager() instanceof Player) {
            if (((Player) event.getDamager()).getHealth() - event.getDamage() < 0.1) {
                Player killer = (Player) event.getDamager();
                if (ConfigUtils.findClass(killer).equals("wizard") && ConfigUtils.getLevel("wizard", killer) > 1) {
                    for (Player player : event.getDamager().getWorld().getPlayers()) {


                        if (player.getInventory().getHelmet() != null) {
                            NBTItem helmet = new NBTItem(player.getInventory().getHelmet());
                            if (!helmet.getCompound("CustomAttributes").getString("ID").equalsIgnoreCase("SCOUT_GOGGLES")) {
                                player.hidePlayer(killer);
                            }
                        } else {
                            player.hidePlayer(killer);
                        }
                    }

                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            for (Player player : event.getDamager().getWorld().getPlayers()) {

                                player.showPlayer(killer);

                            }
                        }
                    }.runTaskLater(DesertMain.getInstance, 40);
                }
            } else if (event.getDamager() instanceof Arrow) {
                Arrow arrow = (Arrow) event.getDamager();
                Player killer = (Player) arrow.getShooter();
                if (ConfigUtils.findClass(killer).equals("wizard") && ConfigUtils.getLevel("wizard", killer) > 1) {
                    for (Player player : event.getDamager().getWorld().getPlayers()) {


                        if (player.getInventory().getHelmet() != null) {
                            NBTItem helmet = new NBTItem(player.getInventory().getHelmet());
                            if (!helmet.getCompound("CustomAttributes").getString("ID").equalsIgnoreCase("SCOUT_GOGGLES")) {
                                player.hidePlayer(killer);
                            }
                        } else {
                            player.hidePlayer(killer);
                        }
                    }

                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            for (Player player : event.getDamager().getWorld().getPlayers()) {

                                player.showPlayer(killer);

                            }
                        }
                    }.runTaskLater(DesertMain.getInstance, 40);
                }
            }
        }

    }
//  MW Hit
    public void magicWandHit(Player damaged, Player damager) {
        if (!damager.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
            if (NBTUtil.INSTANCE.getCustomAttr(damager.getInventory().getItemInMainHand(), "ID").equals("MAGIC_WAND")) {
                ItemStack boots = damaged.getInventory().getBoots();
                if(boots != null){
                    if(NBTUtil.INSTANCE.getCustomAttr(boots,"ID").equals("STUBBORN_BOOTS")){
                        return;
                    }
                }
                if(DesertMain.mwcd.contains(damager)) {
                    return;
                }

                PotionEffect strength = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100,0);
                PotionEffect res = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100,0);
                PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, 100,0);
                PotionEffect instantheal = new PotionEffect(PotionEffectType.HEAL, 100,0);
                PotionEffect instdmg = new PotionEffect(PotionEffectType.HARM, 100,0);
                PotionEffect slow = new PotionEffect(PotionEffectType.SLOW, 100,0);
                PotionEffect weakness = new PotionEffect(PotionEffectType.WEAKNESS, 100,0);
                PotionEffect poison = new PotionEffect(PotionEffectType.POISON, 50,0);
                PotionEffect blind = new PotionEffect(PotionEffectType.BLINDNESS, 100,0);
                PotionEffect wither = new PotionEffect(PotionEffectType.WITHER, 50,0);

                Random rgen = new Random();

                PotionEffect[] allpotionEffects = {strength,res,speed,instantheal,instdmg,slow,weakness,poison,blind,wither};
                PotionEffect[] badpoteff = {instdmg,slow,weakness,poison,blind,wither};
                int randint = rgen.nextInt(allpotionEffects.length);
                int randint1 = rgen.nextInt(badpoteff.length);

                if(ConfigUtils.findClass(damager).equals("wizard")){
                    if(ConfigUtils.getLevel("wizard",damager) > 3 && ConfigUtils.getLevel("wizard",damager) <= 7){
                        if(randint <= 3){
                            damager.sendMessage(ChatColor.RED + "You gave " + damaged.getName() + " " + allpotionEffects[randint]);
                            ParticleEffect.VILLAGER_HAPPY.display(0.5f,0.5f,0.5f,0,30,damaged.getLocation().add(0,0.5,0), 5);
                        }else {
                            ParticleEffect.REDSTONE.display(0.5f,0.5f,0.5f,0,30,damaged.getLocation().add(0,0.5,0),5);
                        }
                        damaged.addPotionEffect(allpotionEffects[randint]);
                    }else if(ConfigUtils.getLevel("wizard",damager) > 7){
                        ParticleEffect.REDSTONE.display(0.5f,0.5f,0.5f,0,30,damaged.getLocation().add(0,0.5,0),5);
                        damaged.addPotionEffect(badpoteff[randint1]);
                    }else{
                        damager.sendMessage(ChatColor.RED + "You can't use this ability!");
                    }
                    DesertMain.mwcd.add(damager);
                    new BukkitRunnable(){

                        @Override
                        public void run() {
                            DesertMain.mwcd.remove(damager);
                        }
                    }.runTaskLater(DesertMain.getInstance, 100);
                }


            }
        }




    }

//  Stun
    public void wizardt4(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Player){
            Player killed = (Player) event.getEntity();
            if(killed.getHealth() - event.getDamage() < 0.1){
                if(event.getDamager() instanceof Arrow){
                    Arrow arrow = (Arrow) event.getDamager();
                    Player killer = (Player) arrow.getShooter();
                    if(ConfigUtils.findClass(killed).equalsIgnoreCase("wizard") && ConfigUtils.getLevel("wizard",killed) > 4){
                        double random = (Math.random() * 100) + 1;
                        if(random <= 10){
                            double walkspeedbefore = killer.getWalkSpeed();
                            killer.setWalkSpeed(0);
                            killer.playSound(killer.getLocation(), Sound.BLOCK_ANVIL_BREAK,1f,2f);
                            killer.sendTitle(ChatColor.YELLOW + "You were Stunned", ChatColor.DARK_GRAY + "By " + killed.getName());
                            PotionEffect poison = new PotionEffect(PotionEffectType.POISON, 60, 1, false, false);
                            killer.addPotionEffect(poison);
                            new BukkitRunnable(){

                                @Override
                                public void run() {
                                    killer.setWalkSpeed((float) walkspeedbefore);
                                }

                            }.runTaskLater(DesertMain.getPlugin(DesertMain.class), 60);
                        }
                    }
                }else if(event.getDamager() instanceof Player){
                    Player killer = (Player) event.getDamager();
                    if(ConfigUtils.findClass(killed).equalsIgnoreCase("wizard") && ConfigUtils.getLevel("wizard",killed) > 4){
                        double random = (Math.random() * 100) + 1;
                        if(random <= 10){
                            double walkspeedbefore = killer.getWalkSpeed();
                            killer.setWalkSpeed(0);
                            killer.playSound(killer.getLocation(), Sound.BLOCK_ANVIL_BREAK,1f,2f);
                            killer.sendTitle(ChatColor.YELLOW + "You were Stunned", ChatColor.DARK_GRAY + "By " + killed.getName());
                            PotionEffect poison = new PotionEffect(PotionEffectType.POISON, 60, 1, false, false);
                            killer.addPotionEffect(poison);
                            new BukkitRunnable(){

                                @Override
                                public void run() {
                                    killer.setWalkSpeed((float) walkspeedbefore);
                                }

                            }.runTaskLater(DesertMain.getPlugin(DesertMain.class), 60);
                        }
                    }
                }
            }
        }




    }


//  Last Stand
    public void wizardt8(EntityDamageByEntityEvent event){

        if(event.getEntity() instanceof Player){
            if(DesertMain.laststandcd.contains((Player)event.getEntity())) return;
            Player damaged = (Player) event.getEntity();
            if(ConfigUtils.getLevel("wizard", damaged) > 8 && ConfigUtils.findClass(damaged).equals("wizard")){
                if(damaged.getHealth() - event.getDamage() <= 2){
                    PotionEffect res = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,60,1,true,false);
                    PotionEffect speed = new PotionEffect(PotionEffectType.SPEED,60,1,true,false);
                    damaged.addPotionEffect(res);
                    damaged.addPotionEffect(speed);
                    DesertMain.laststandcd.add(damaged);
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            DesertMain.laststandcd.remove(damaged);
                        }
                    }.runTaskLater(DesertMain.getInstance, 200);
                }

            }
        }



    }


}

