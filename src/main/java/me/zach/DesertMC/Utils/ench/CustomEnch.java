package me.zach.DesertMC.Utils.ench;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.GameMechanics.Events;
import me.zach.DesertMC.Utils.MiscUtils;
import me.zach.DesertMC.Utils.Particle.ParticleEffect;
import me.zach.DesertMC.Utils.StringUtils.StringUtil;
import me.zach.DesertMC.events.FallenDeathEvent;
import me.zach.databank.DBCore;
import me.zach.databank.saver.Key;
import me.zach.databank.saver.PlayerData;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.Material.AIR;

public enum CustomEnch implements Listener {
    TURTLE("Turtle", "turtle", EnchantType.ARMOR) {
        String getDescription(int level){
            return "Grants a " + level + "% knockback reduction when attacked.";
        }

        @Override
        public void onHit(EntityDamageByEntityEvent event) {
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
                        player.setVelocity(player.getVelocity().multiply(1 - (lvl * 0.01)));
                    }
                }.runTaskLater(DesertMain.getInstance,2);
            }
        }
    },
    CRUEL_BLOW("Cruel Blow", "cruel_blow", EnchantType.MELEE, EnchantType.BOW){
        String getDescription(int level){
            return "Deal " + level + " additional damage if your attack deals more than 30% of your opponents max health.";
        }

        @Override
        public void onHit(EntityDamageByEntityEvent event) {
            Player player = Events.getPlayer(event.getDamager());
            ItemStack itemUsed = Events.getItemUsed(event.getDamager());
            if(player != null){
                if(!CustomEnch.validatePlayer(event.getEntity(), Key.TANK,7)){
                    if(event.getEntity() instanceof Damageable && ((Damageable) event.getEntity()).getMaxHealth() / 3 < event.getDamage()){
                        event.setDamage(event.getDamage() + getLevel(itemUsed));
                    }
                }
            }
        }
    },
    EXTRAVERT(EnchantType.ARMOR){
        String getDescription(int level){
            return "Take " + 0.5*level + "% less damage per person within a 25 block radius.";
        }

        @Override
        public void onHit(EntityDamageByEntityEvent event) {
            if(event.getEntity() instanceof Player){
                Player player = (Player) event.getEntity();
                if(!CustomEnch.validatePlayer(player,Key.CORRUPTER,4)) return;
                int lvl = getTotalArmorLevel(player);
                if(lvl > 0){
                    event.setDamage(event.getDamage()*(1-(MiscUtils.getNearbyEntities(Player.class,player,25).size()*lvl*0.5)));
                }
            }
        }
    },
    NO_MERCY("No Mercy", "no_mercy", EnchantType.MELEE){
        String getDescription(int level){
            return "Deal " + 3*level + "% more damage if your opponent is under half health.";
        }
    },
    ANTI_FOCUS("Anti-Focus", "anti_focus", EnchantType.ARMOR){
        String getDescription(int level){
            return "Take " + NUM_FORMATTER.format(0.7*level) + "% less damage if your opponent is sprinting.";
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
                if(player.isSprinting()) event.setDamage(event.getDamage()*(1-(getTotalArmorLevel(player)*0.7)));
            }
        }
    },
    SPIRIT_GUARD("Spirit Guard","spirit_guard", EnchantType.MELEE){
        String getDescription(int level){
            return 8*level + "% chance to heal " + NUM_FORMATTER.format(0.3 * level) + " hp when blocking hits";
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
    ALERT(EnchantType.ARMOR){
        String getDescription(int level){
            return "Your senses heightened, take " + level + "% less damage on your opponent's first blow.";
        }
    } /*defined in EventsForScout*/,
    ETHEREAL(EnchantType.BOW){
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
                            List<Player> nearby = MiscUtils.getNearbyEntities(Player.class,event.getEntity(),0.5);
                            Bukkit.broadcastMessage("e6n: " + nearby);
                            if(!nearby.isEmpty() && nearby.get(0) != null && !nearby.get(0).equals(shooter)){
                                Bukkit.broadcastMessage("e6.1a = " + event.getEntity().getLocation());
                                event.getEntity().teleport(nearby.get(0).getLocation());

                                spawnEtherealFW(current);
                                break;
                            }
                        }
                        ParticleEffect.FIREWORKS_SPARK.display(0f,0f,0f,0,1,current,300);
                    }
                    final Location cc = current; //TODO why is this never used?
                    new BukkitRunnable(){
                        public void run(){
                            event.getEntity().setVelocity(new Vector(0,0,0));
                        }
                    }.runTaskLater(DesertMain.getInstance,2);
                    spawnEtherealFW(current);
                    Bukkit.broadcastMessage("e8l " + event.getEntity().getLocation());
                }
            }
        }

        void spawnEtherealFW(Location loc){
            MiscUtils.spawnFirework(loc, 0, false, false, Color.WHITE, FireworkEffect.Type.BALL, Color.BLUE, Color.WHITE);
        }
    };
    private static final DecimalFormat NUM_FORMATTER = new DecimalFormat();
    static{
        NUM_FORMATTER.setMaximumFractionDigits(1);
    }

    abstract String getDescription(int level);

    public static final String DOT = "\u25CF";
    public final String name;
    public final String id;
    public final EnchantType[] types;

    private void checkTypeLength(){
        if(types.length < 1) throw new IllegalArgumentException("Could not initialize enchant " + name() + ", because it didn't provide any enchant types.");
    }

    CustomEnch(String name, String id, EnchantType... types){
        this.name = name;
        this.id = id;
        this.types = types;
        checkTypeLength();
    }

    CustomEnch(EnchantType... types){
        this.name = capitalizeEnum(this.name()).replace("_"," ");
        this.id = this.name().toLowerCase();
        this.types = types;
        checkTypeLength();
    }

    public ItemStack getBook(int level){
        ItemStack book = MiscUtils.generateItem(Material.ENCHANTED_BOOK,
                ChatColor.BLUE + name + " " + level,
                StringUtil.wrapLore(ChatColor.GRAY + getDescription(level) + "\n" + ChatColor.DARK_GRAY + "Can be applied to " + StringUtil.series(StringUtil.toStringArray(types))),
                (byte) -1,
                1,
                "ENCHANTED_BOOK");
        NBTItem nbt = new NBTItem(book);
        NBTCompound customAttributes = nbt.addCompound("CustomAttributes");
        customAttributes.setInteger("BASE_LEVEL", level);
        customAttributes.setInteger("REAL_LEVEL", level);
        customAttributes.setString("ENCH_ID", id);
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
        System.out.println("id = " + id);
        for(CustomEnch ce : CustomEnch.values()){
            System.out.println("checking " + ce.id);
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
        return data.getClassLevel(clazz) > lv;
    }

    public void onHit(EntityDamageByEntityEvent event){}

    public void onKill(FallenDeathEvent event){}

    public void onShoot(ProjectileLaunchEvent event){}

}
