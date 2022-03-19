package me.zach.DesertMC.ClassManager.WizardManager;

import de.tr7zw.nbtapi.NBTItem;
import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.Utils.Config.ConfigUtils;
import me.zach.DesertMC.Utils.MiscUtils;
import me.zach.DesertMC.Utils.Particle.ParticleEffect;
import me.zach.DesertMC.Utils.PlayerUtils;
import me.zach.DesertMC.Utils.nbt.NBTUtil;
import me.zach.DesertMC.events.FallenDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


public class EventsForWizard implements Listener {
    public static final EventsForWizard INSTANCE = new EventsForWizard();
    public static void addBladeCharge(Player player){
        NBTItem bladeNbt = new NBTItem(player.getItemInHand());
        if(bladeNbt.getCompound("CustomAttributes").getInteger("CHARGE") < 10){
            bladeNbt.getCompound("CustomAttributes").setInteger("CHARGE", bladeNbt.getCompound("CustomAttributes").getInteger("CHARGE") + 2);
            ItemStack newBladeItem = bladeNbt.getItem();
            ItemMeta newBladeMeta = newBladeItem.getItemMeta();
            newBladeMeta.setDisplayName(newBladeMeta.getDisplayName().replaceAll((bladeNbt.getCompound("CustomAttributes").getInteger("CHARGE") - 2) + "", (bladeNbt.getCompound("CustomAttributes").getInteger("CHARGE")) + ""));
            newBladeItem.setItemMeta(newBladeMeta);
            player.setItemInHand(newBladeItem);

            if(bladeNbt.getCompound("CustomAttributes").getInteger("CHARGE") == 10) player.sendMessage(ChatColor.DARK_AQUA + "Wizard Blade charge just maxed out!");
        }else{
            player.sendMessage(ChatColor.YELLOW + "Wizard Blade charge was already maxed out, right click to release it!");
        }

    }

//  Invisibility
    public void wizardt1(Player killer) {
        if (ConfigUtils.findClass(killer).equals("wizard") && ConfigUtils.getLevel("wizard", killer) > 1) {
            for (Player player : killer.getWorld().getPlayers()) {
                if(!NBTUtil.getCustomAttrString(player.getInventory().getHelmet(), "ID").equals("SCOUT_GOGGLES"))
                    player.hidePlayer(killer, false);
            }
            new BukkitRunnable() {

                @Override
                public void run() {
                    for (Player player : killer.getWorld().getPlayers()) {
                        if(!player.canSee(killer)) player.showPlayer(killer);
                    }
                }
            }.runTaskLater(DesertMain.getInstance, 40);
        }
    }
//  MW Hit
    public void magicWandHit(Player damaged, Player damager) {
        if (!damager.getInventory().getItemInHand().getType().equals(Material.AIR)) {
            if (NBTUtil.getCustomAttrString(damager.getInventory().getItemInHand(), "ID").equals("MAGIC_WAND")) {

                ItemStack boots = damaged.getInventory().getBoots();
                if(boots != null){
                    if(NBTUtil.getCustomAttrString(boots,"ID").equals("STUBBORN_BOOTS") && ConfigUtils.getLevel("tank", damaged) > 5){
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

                Random rgen = ThreadLocalRandom.current();

                PotionEffect[] allpotionEffects = {strength,res,speed,instantheal,instdmg,slow,weakness,poison,blind,wither};
                PotionEffect[] badpoteff = {instdmg,slow,weakness,poison,blind,wither};
                int randint = rgen.nextInt(allpotionEffects.length);
                int randint1 = rgen.nextInt(badpoteff.length);

                if(ConfigUtils.findClass(damager).equals("wizard")){
                    if(ConfigUtils.getLevel("wizard",damager) > 3 && ConfigUtils.getLevel("wizard",damager) <= 7){
                        if(randint <= 3){
                            damager.sendMessage(ChatColor.RED + "You gave " + damaged.getName() + " " + ChatColor.GREEN + "" + MiscUtils.potionEffectToString(allpotionEffects[randint]));
                            ParticleEffect.VILLAGER_HAPPY.display(0.5f,0.5f,0.5f,0,30,damaged.getLocation().add(0,0.5,0), 5);
                        }else {
                            ParticleEffect.REDSTONE.display(0.5f,0.5f,0.5f,0,30,damaged.getLocation().add(0,0.5,0),5);
                            damager.sendMessage(ChatColor.RED + "You gave " + damaged.getName() + " " + ChatColor.RED + "" + MiscUtils.potionEffectToString(allpotionEffects[randint]));
                        }
                        damaged.addPotionEffect(allpotionEffects[randint]);
                    }else if(ConfigUtils.getLevel("wizard",damager) > 7){
                        ParticleEffect.REDSTONE.display(0.5f,0.5f,0.5f,0,30,damaged.getLocation().add(0,0.5,0),5);
                        damaged.addPotionEffect(badpoteff[randint1]);
                        damager.sendMessage(ChatColor.RED + "You gave " + damaged.getName() + " " + ChatColor.RED + "" + MiscUtils.potionEffectToString(allpotionEffects[randint1]));
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
    @EventHandler
    public void wizardBlade(PlayerInteractAtEntityEvent e){
        try{
            if(NBTUtil.getCustomAttrString(e.getPlayer().getItemInHand(), "ID").equals("WIZARD_BLADE")){
                NBTItem bladeNbt = new NBTItem(e.getPlayer().getItemInHand());
                if(e.getRightClicked() instanceof Player) {
                    if (bladeNbt.getCompound("CustomAttributes").getInteger("CHARGE") != 0) {
                        if (MiscUtils.canDamage(e.getPlayer()) != null) {
//                            if (bladeNbt.getCompound("CustomAttributes").getInteger("CHARGE") >= ((Player) e.getRightClicked()).getHealth()) {
                            ItemMeta bladeMeta = bladeNbt.getItem().getItemMeta();
                            bladeMeta.setDisplayName(bladeMeta.getDisplayName().replaceAll((bladeNbt.getCompound("CustomAttributes").getInteger("CHARGE")) + "", "0"));
                            bladeNbt.getItem().setItemMeta(bladeMeta);
                            PlayerUtils.trueDamage((Player) e.getRightClicked(), (double) bladeNbt.getCompound("CustomAttributes").getInteger("CHARGE"), e.getPlayer());
                            bladeNbt.getCompound("CustomAttributes").setInteger("CHARGE", 0);
                            e.getPlayer().setItemInHand(bladeNbt.getItem());
                            e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENDERMAN_HIT, 10, 1.2f);

//                            }else{
//                                ItemMeta bladeMeta = bladeNbt.getItem().getItemMeta();
//                                bladeMeta.setDisplayName(bladeMeta.getDisplayName().replaceAll((bladeNbt.getCompound("CustomAttributes").getInteger("CHARGE")) + "", "0"));
//                                bladeNbt.getItem().setItemMeta(bladeMeta);
//                                bladeNbt.getCompound("CustomAttributes").setInteger("CHARGE", 0);
//                                e.getPlayer().setItemInHand(bladeNbt.getItem());
//                                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENDERMAN_HIT, 10, 1.2f);
//                            }
                        }
                    } else {
                        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.VILLAGER_NO, 10, 1);
                    }
                }
            }
        }catch(Exception ex){
            if(!(ex instanceof NullPointerException)) {
                Bukkit.getConsoleSender().sendMessage("Error with wizard blade event:\n" + ex);

            }

        }
    }

//  Stun
    @EventHandler(priority = EventPriority.MONITOR)
    public void wizardt4(FallenDeathEvent event){
        if(!event.isCancelled()){
            Player killed = event.getPlayer();
            Player killer = event.getKiller();
            if(ConfigUtils.findClass(killed).equalsIgnoreCase("wizard") && ConfigUtils.getLevel("wizard",killed) > 4){
                if(Math.random() <= 0.1){
                    killer.playSound(killer.getLocation(), Sound.ANVIL_BREAK,1f,2f);
                    PotionEffect poison = new PotionEffect(PotionEffectType.POISON, 60, 1, false, false);
                    PotionEffect slowness = new PotionEffect(PotionEffectType.SLOW, 100, 255, false, false);
                    killer.addPotionEffect(poison);
                    killer.addPotionEffect(slowness, true);
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

