package me.zach.DesertMC.Utils.ench;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.Utils.MiscUtils;
import me.zach.DesertMC.Utils.Particle.ParticleEffect;
import me.zach.DesertMC.Utils.StringUtils.StringUtil;
import me.zach.databank.DBCore;
import me.zach.databank.saver.Key;
import me.zach.databank.saver.PlayerData;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.Material.AIR;

public enum CustomEnch implements Listener {
    TURTLE("Turtle", "turtle") {
        String getDescription(int level){
            return level + "% knockback reduction when attacked";
        }

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
        String getDescription(int level){
            return "Deal " + level + " additional damage if the attack deals more than 30% of your opponents max health";
        }

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
        String getDescription(int level){
            return "Take " + 0.1*level + "% less damage per person within a 5 block radius";
        }

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
        String getDescription(int level){
            return "Deal " + 3*level + "% more damage if your opponent is under half health";
        }
    },
    ANTI_FOCUS("Anti-Focus", "anti_focus"){
        String getDescription(int level){
            return "Take " + 0.3*level + "% less dmg if your opponent is sprinting";
        }

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
        String getDescription(int level){
            return 8*level + "% chance to heal " + 0.3*level + " hp when blocking hits";
        }

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
    ALERT{
        String getDescription(int level){
            return "Take " + level + "% less damage when someone first hits you";
        }
    } /*defined in EventsForScout*/,
    ETHEREAL{
        String getDescription(int level){
            return "Transforms arrows into hyper-accurate laser beams, carrying them up to " + level * 7 + " blocks with zero drop-off.";
        }

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
                        if(!MiscUtils.trueEmpty(current.getBlock())){
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

        void spawnEtherealFW(Location loc){
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
    };

    abstract String getDescription(int level);

    public static final String DOT = "\u25CF";
    public final String name;
    public final String id;

    CustomEnch(String name, String id){
        this.name = name;
        this.id = id;
    }

    CustomEnch(){
        this.name = capitalizeEnum(this.name()).replace("_"," ");
        this.id = this.name().toLowerCase();
    }

    public ItemStack getBook(int level){
        ItemStack book = MiscUtils.generateItem(Material.ENCHANTED_BOOK,
                ChatColor.BLUE + name + " " + level,
                StringUtil.wrapLore(ChatColor.GRAY + getDescription(level)),
                (byte) -1,
                1,
                "ENCHANTED_BOOK");
        NBTItem nbt = new NBTItem(book);
        nbt.setInteger("BASE_LEVEL", level);
        nbt.setInteger("REAL_LEVEL", level);
        return nbt.getItem();
    }

    public int getLevel(ItemStack i){
        if(i != null && i.getType() != AIR)
        return getEnch(new NBTItem(i)).getInteger(id);
        else return 0;
    }

    public int getTotalArmorLevel(Player p){
        ItemStack[] armor = p.getInventory().getArmorContents();
        int lv = 0;
        for (ItemStack itemStack : armor) {
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
                    if (s.contains(name + " " + enchs.getInteger(id))) {
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
        return null;
    }

    public static NBTCompound getEnch(NBTItem nbti){
        NBTCompound enchs;
        if(nbti.getCompound("CustomAttributes") != null && nbti.getCompound("CustomAttributes").getCompound("enchantments") != null){
            enchs = nbti.getCompound("CustomAttributes").getCompound("enchantments");
        }else{
            enchs = nbti.addCompound("CustomAttributes").addCompound("enchantments");
        }
        return enchs;
    }

    public static int getLevel(ItemStack i,String id){
        return fromID(id).getLevel(i);
    }

    private static String capitalizeEnum(String str) {
        String[] words = str.toLowerCase().split("_");
        for(int i = 0; i< words.length; i++){
            String capital = StringUtil.capitalizeFirst(words[i]);
            words[i] = capital;
        }
        return String.join(" ", words);
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
