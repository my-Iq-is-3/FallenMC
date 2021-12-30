package me.zach.DesertMC.Utils.ench;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.Utils.MiscUtils;
import me.zach.DesertMC.Utils.Particle.ParticleEffect;
import me.zach.DesertMC.Utils.PlayerUtils;
import me.zach.databank.DBCore;
import me.zach.databank.saver.Key;
import me.zach.databank.saver.PlayerData;
import me.zach.databank.saver.SaveManager;
import net.minecraft.server.v1_8_R3.Vec3D;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.Material.AIR;
import static org.bukkit.Material.GRASS;

public enum CustomEnch implements Listener {
    TURTLE("Turtle", "turtle") {

        @Override
        public void onHit(EntityDamageByEntityEvent event) {
            super.onHit(event);
            Bukkit.broadcastMessage("turtle1");
            if(event.getEntity() instanceof Player){
                Player player = (Player) event.getEntity();
                if(!CustomEnch.validatePlayer(player, Key.TANK,4)) return;
                Bukkit.broadcastMessage(player.toString());
                Bukkit.broadcastMessage("turtle2");
                int lvl = getTotalArmorLevel(player);
                Bukkit.broadcastMessage("found level");
                Bukkit.broadcastMessage("turtle lvl = " + lvl);
                if(lvl < 1) return;
                new BukkitRunnable(){
                    public void run(){
                        // 100
                        // 100-(100*0.1)
                        player.setVelocity(player.getVelocity().subtract(player.getVelocity().multiply(lvl/100)));
                    }
                }.runTaskLater(DesertMain.getInstance,2);
            }
        }
    },
    CRUEL_BLOW("Cruel Blow", "cruel_blow"){
        @Override
        public void onHit(EntityDamageByEntityEvent event) {
            super.onHit(event);
            if(event.getDamager() instanceof Player){
                if(!CustomEnch.validatePlayer(event.getEntity(), Key.TANK,7)) return;
                if(event.getEntity() instanceof LivingEntity && ((LivingEntity) event.getEntity()).getMaxHealth()/3 < event.getDamage()) {
                    event.setDamage(event.getDamage() + getLevel(((Player) event.getDamager()).getItemInHand()));
                }
            }
        }
    },
    EXTRAVERT{
        @Override
        public void onHit(EntityDamageByEntityEvent event) {
            super.onHit(event);
            if(event.getEntity() instanceof Player){
                Player player = (Player) event.getEntity();
                if(!CustomEnch.validatePlayer(player,Key.CORRUPTER,4)) return;
                int lvl = getTotalArmorLevel(player);
                if(lvl > 0){
                    event.setDamage(event.getDamage()*(1-(MiscUtils.getNearbyEntities(Player.class,player,5,5,5).size()*lvl*0.01)));
                }
            }
        }
    },
    NO_MERCY("No Mercy", "no_mercy"){
//        @Override
//        public void onHit(EntityDamageByEntityEvent event) {
//            super.onHit(event);
//        }
        // already coded in EventsForCorrupter
    },
    ANTI_FOCUS("Anti-Focus", "anti_focus"){
        @Override
        public void onHit(EntityDamageByEntityEvent event) {
            super.onHit(event);
            if(event.getDamager() instanceof Player && event.getEntity() instanceof Player){
                Player player = (Player) event.getDamager();

                if(!CustomEnch.validatePlayer(player,Key.SCOUT,4)) return;
                // current dmg = 10
                // for a 10% dmg reduction
                // 10*(1-(1/10) = 9
                if(player.isSprinting()) event.setDamage(event.getDamage()*(1-(getTotalArmorLevel(player)*0.01)));
            }
        }
    },
    SPIRIT_GUARD("Spirit Guard","spirit_guard"){
        @Override
        public void onHit(EntityDamageByEntityEvent event) {
            super.onHit(event);
            if(event.getEntity() instanceof Player) {
                Player p = (Player) event.getEntity();
                if(!CustomEnch.validatePlayer(p,Key.SCOUT,7)) return;
                int lvl = getLevel(p.getItemInHand());
                if (ThreadLocalRandom.current().nextInt(100) < 8*lvl){
                    p.setHealth(Math.min(p.getHealth() + 0.3 * lvl, p.getMaxHealth()));
                }
            }
        }
    },
    ALERT /*defined in EventsForScout*/,
    ETHEREAL{
        @Override
        public void onShoot(ProjectileLaunchEvent event) {
            super.onShoot(event);
            Bukkit.broadcastMessage("e1");
            if(event.getEntity() instanceof Arrow && event.getEntity().getShooter() instanceof Player){
                Bukkit.broadcastMessage("e2");
                Player shooter = (Player) event.getEntity().getShooter();
                if(!CustomEnch.validatePlayer(shooter,Key.WIZARD,8)) return;
                if(getLevel(shooter.getItemInHand()) > 0){
                    Bukkit.broadcastMessage("e3");
                    int lvl = getLevel(shooter.getItemInHand());
                    int range = lvl*7;
                    Bukkit.broadcastMessage("elvl " + lvl);
                    Location current = shooter.getEyeLocation();
                    for(int i=0;i<range;i++){
                        Bukkit.broadcastMessage("e4l,i=" + i);
                        Vector dir = current.getDirection();
                        current = current.add(dir);
                        event.getEntity().teleport(current);
                        if(current.getBlock().getType() != GRASS && current.getBlock().getType() != AIR){
                            Bukkit.broadcastMessage("e5");
                            event.getEntity().teleport(current);
                            spawnEtherealFW(current);
                            break;
                        }else{
                            Bukkit.broadcastMessage("e6");
                            List<Zombie> nearby = MiscUtils.getNearbyEntities(Zombie.class,event.getEntity(),0.5);
                            Bukkit.broadcastMessage("e6n: " + nearby);
                            if(!nearby.isEmpty() && nearby.get(0) != null /*&& nearby != shooter*/){
                                Bukkit.broadcastMessage("e6.1a = " + event.getEntity().getLocation());
                                event.getEntity().teleport(nearby.get(0).getLocation());
                                spawnEtherealFW(current);
                                break;
                            }
                        }

                        Bukkit.broadcastMessage("e7");
                        ParticleEffect.FIREWORKS_SPARK.display(0f,0f,0f,0,1,current,300);
                    }
                    final Location cc = current;
                    new BukkitRunnable(){
                        public void run(){
                            event.getEntity().teleport(cc);
                        }
                    }.runTaskLater(DesertMain.getInstance,2);
                    spawnEtherealFW(current);
                    Bukkit.broadcastMessage("e8l " + event.getEntity().getLocation());
                }
            }
        }
    };

    public static final String DOT = "\u25CF";
    public final String name;
    public final String id;

    CustomEnch(String name, String id){

        this.name = name;
        this.id = id;
    }

    CustomEnch(){

        this.name = capitalizeString(this.name()).replace("_"," ");
        this.id = this.name().toLowerCase();

    }

    public int getLevel(ItemStack i){
        if(i != null && i.getType() != AIR)
        return getEnch(new NBTItem(i)).getInteger(id);
        else return 0;
    }

    public int getTotalArmorLevel(Player p){
        Bukkit.broadcastMessage("turtle 3");
        ItemStack[] armor = p.getInventory().getArmorContents();
        int lv = 0;
        Bukkit.broadcastMessage("turtle 4");
        for (ItemStack itemStack : armor) {
            Bukkit.broadcastMessage("lv=" + lv + " i=" + itemStack);
            if(itemStack.getType() != AIR) lv += getLevel(itemStack);
        }
        return lv;
    }

    public ItemStack apply(ItemStack i,int level){
        ItemStack item = i.clone();
        ItemMeta m = item.getItemMeta();
        List<String> lore = m.getLore() == null?new ArrayList<>():m.getLore();
        NBTItem nbti = new NBTItem(item);
        NBTCompound enchs;
        if(nbti.getCompound("CustomAttributes") != null && nbti.getCompound("CustomAttributes").getCompound("enchantments") != null){
            enchs = nbti.getCompound("CustomAttributes").getCompound("enchantments");
            /* enchant lore exists already
                use lore.replace function
             */
            if(enchs.getInteger(id) > 0) {
                lore.replaceAll(s -> {
                    if (s.equals(ChatColor.BLUE + DOT + " " + name + " " + enchs.getInteger(id))) {
                        return ChatColor.BLUE + DOT + " " + name + " " + level;
                    } else {
                        return s;
                    }
                });
            }else{
                lore.add(ChatColor.BLUE + DOT + " " + name + " " + level);
            }
        }else{
            enchs = nbti.addCompound("CustomAttributes").addCompound("enchantments");
            lore.add(" ");
            lore.add(ChatColor.BLUE + DOT + " " + name + " " + level);
            /* add ench lore

             */
        }


        enchs.setInteger(id,level);
        item = nbti.getItem();
        m = item.getItemMeta();
        m.setLore(lore);
        item.setItemMeta(m);
        Bukkit.broadcastMessage(new NBTItem(item).toString());
        return item;
    }

    public static CustomEnch fromID(String id){
        for(CustomEnch ce : CustomEnch.values()){
            if(ce.id.equals(id)){
                return ce;
            }
        }
        throw new NullPointerException("no id found");
    }

    public static NBTCompound getEnch(NBTItem nbti){
        Bukkit.broadcastMessage(nbti.toString());
        NBTCompound enchs;
        if(nbti.getCompound("CustomAttributes") != null && nbti.getCompound("CustomAttributes").getCompound("enchantments") != null){
            Bukkit.broadcastMessage("ench1");
            enchs = nbti.getCompound("CustomAttributes").getCompound("enchantments");
        }else{
            Bukkit.broadcastMessage("ench2");
            enchs = nbti.addCompound("CustomAttributes").addCompound("enchantments");
        }
        Bukkit.broadcastMessage("enchs = " + enchs);
        return enchs;
    }

    public static int getLevel(ItemStack i,String id){
        return fromID(id).getLevel(i);
    }

    private static String capitalizeString(String str) {
        String retStr = str.toLowerCase();
        try {
            char[] a = retStr.toCharArray();
            a[0] = Character.toUpperCase(a[0]);
            retStr = new String(a);
        }catch (Exception e){e.printStackTrace();}
        return retStr;
    }

    public static void spawnEtherealFW(Location loc){
        Firework firework = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);

        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffect(FireworkEffect.builder().withColor(Color.BLUE).withColor(Color.WHITE).withFade(Color.WHITE).build());
        firework.setFireworkMeta(meta);
        new BukkitRunnable(){
            public void run(){
                firework.detonate();
            }
        }.runTaskLater(DesertMain.getInstance,3);
    }

    private static boolean validatePlayer(Entity p, String clazz, int lv){
        if(!(p instanceof Player)) return false;
        PlayerData data = DBCore.getInstance().getSaveManager().getData(p.getUniqueId());
        return data.getClassLevel(clazz) > lv && data.getCurrentClass().equals(clazz);
    }
    public void onHit(EntityDamageByEntityEvent event){}

    public void onKill(EntityDamageByEntityEvent event){}

    public void onShoot(ProjectileLaunchEvent event){}


}
