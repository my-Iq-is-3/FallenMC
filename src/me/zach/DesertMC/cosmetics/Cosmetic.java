package me.zach.DesertMC.cosmetics;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.Utils.Particle.ParticleEffect;
import me.zach.DesertMC.Utils.StringUtils.StringUtil;
import me.zach.artifacts.gui.inv.ArtifactData;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArmorStand;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public enum Cosmetic implements CosmeticActivator{
    EXPLOSION("Explosion Kill Effect", Material.TNT,  ChatColor.YELLOW + "Explode your enemies for the ultimate revenge!", CosmeticType.KILL_EFFECT){
        @Override
        public void activateKill(Player player){
            ParticleEffect.EXPLOSION_LARGE.display(0f, 0f, 0f, 1, 10, player.getLocation(), 75);
        }
    },
    EMERALD_TRAIL("Emerald Sparkle Arrow Trail", Material.EMERALD_ORE, ChatColor.YELLOW + "A green, sparkling trail that follows your arrows for some extra style!", CosmeticType.ARROW_TRAIL, ChatColor.WHITE + "Unlocked with the purchase of " + ChatColor.GREEN + "SUPPORTER" + ChatColor.WHITE + "rank"){
        public void activateArrow(Arrow arrow, boolean fast){
            BukkitTask trail = new BukkitRunnable(){
                @Override
                public void run() {
                    if(arrow.isOnGround() || arrow.isDead()) cancel();
                    else Cosmetic.arrowTrail(arrow, fast, ParticleEffect.VILLAGER_HAPPY);
                }
            }.runTaskTimerAsynchronously(DesertMain.getInstance, 0, 1);
            //trails.put(arrow.getUniqueId(), trail.getTaskId());
        }
    },
    FLAMING_ARROWS("Flaming Arrows", Material.FIREBALL, ChatColor.YELLOW + "Your arrows are followed by menacing flames!", CosmeticType.ARROW_TRAIL){
        public void activateArrow(Arrow arrow, boolean fast){
            BukkitTask trail = new BukkitRunnable(){
                @Override
                public void run(){
                    if(arrow.isOnGround() || arrow.isDead()) cancel();
                    else Cosmetic.arrowTrail(arrow, fast, ParticleEffect.FLAME);
                }
            }.runTaskTimerAsynchronously(DesertMain.getInstance, 0, 1);
            //trails.put(arrow.getUniqueId(), trail.getTaskId());
        }
    },
    MUSICAL_ARROWS("Musical Arrows", Material.JUKEBOX, ChatColor.YELLOW + "Your arrows fly with graceful musical chords following them!", CosmeticType.ARROW_TRAIL){
        public void activateArrow(Arrow arrow, boolean fast){
            BukkitTask trail = new BukkitRunnable(){
                int color = 0;
                @Override
                public void run(){
                    if(arrow.isOnGround() || arrow.isDead()) cancel();
                    else{
                        ParticleEffect.ParticleColor pColor;
                        try{
                            pColor = noteColors.get(color);
                        }catch(IndexOutOfBoundsException ex){
                            color = 0;
                            pColor = noteColors.get(0);
                        }
                        Cosmetic.arrowTrail(arrow, fast, ParticleEffect.NOTE, pColor);
                        if(arrow.getShooter() instanceof Player){
                            ((Player) arrow.getShooter()).playNote(arrow.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.C));
                        }
                        color++;
                    }
                }
            }.runTaskTimerAsynchronously(DesertMain.getInstance, 0, 1);
        }
    },
    CUPID_ARROWS("Cupid Arrows", Material.RED_ROSE, ChatColor.YELLOW + "Shoot arrows with a \"lovely\" trail, courtesy of the elusive Cupid.", CosmeticType.ARROW_TRAIL){
        public void activateArrow(Arrow arrow, boolean fast){
            new BukkitRunnable(){
                @Override
                public void run(){
                    if(arrow.isOnGround() || arrow.isDead()) cancel();
                    else Cosmetic.arrowTrail(arrow, fast, ParticleEffect.HEART);
                }
            }.runTaskTimer(DesertMain.getInstance, 0, 1);
        }
    },
    RAINBOW("Rainbow Arrows", Material.YELLOW_FLOWER, ChatColor.YELLOW + "A colorful rainbow trails behind the arrows you shoot!", CosmeticType.ARROW_TRAIL){
        public void activateArrow(Arrow arrow, boolean fast){
            new BukkitRunnable(){
                int colorIndex = 0;
                public void run(){
                    if(arrow.isOnGround() | arrow.isDead()) cancel();
                    else{
                        ParticleEffect.ParticleColor pColor;
                        try{
                            pColor = rColors.get(colorIndex);
                        }catch(IndexOutOfBoundsException ex){
                            colorIndex = 0;
                            pColor = rColors.get(0);
                        }
                        Cosmetic.arrowTrail(arrow, fast, ParticleEffect.REDSTONE, pColor);
                        colorIndex++;
                    }
                }
            }.runTaskTimerAsynchronously(DesertMain.getInstance, 0, 1);
        }
    },

    DEATH_MESSAGES("Death Messages", Material.MAP,  ChatColor.YELLOW + "Displays a random message to other players at the spot that you died!", CosmeticType.DEATH_EFFECT, true){
        public void activateDeath(Player player){
            ArmorStand stand = (ArmorStand) player.getLocation().getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
            stand.setVisible(true);
            stand.setMarker(true);
            stand.setCustomName(deathMessages.get(new Random().nextInt(deathMessages.size())));
            new BukkitRunnable(){
                @Override
                public void run() {
                    stand.remove();
                }
            }.runTaskLater(pl, 40);
        }
    },
    WATER_ARROWS("Water Arrows", Material.WATER_BUCKET, ChatColor.YELLOW + "Your arrows drip water as they whiz past your opponent!", CosmeticType.ARROW_TRAIL) {
        public void activateArrow(Arrow arrow, boolean fast){
            new BukkitRunnable(){
                @Override
                public void run() {
                    if(arrow.isDead() || arrow.isOnGround()) cancel();
                    else Cosmetic.arrowTrail(arrow, fast, ParticleEffect.DRIP_WATER);
                }
            }.runTaskTimerAsynchronously(pl, 0, 1);
        }
    },
    LAVA_ARROWS("Lava Arrows", Material.LAVA_BUCKET, ChatColor.YELLOW + "Your arrows drip molten lava as they whiz past your opponent!", CosmeticType.ARROW_TRAIL, true) {
        public void activateArrow(Arrow arrow, boolean fast){
            new BukkitRunnable(){
                @Override
                public void run() {
                    if(arrow.isDead() || arrow.isOnGround()) cancel();
                    else Cosmetic.arrowTrail(arrow, fast, ParticleEffect.DRIP_LAVA);
                }
            }.runTaskTimerAsynchronously(pl, 0, 1);
        }
    },
    EMERALD_DESTRUCTION("Emerald Destruction", Material.EMERALD_BLOCK, ChatColor.YELLOW + "Eliminate your foes with flair, and all sorts of stylish emerald things popping out of them.", CosmeticType.KILL_EFFECT, ChatColor.WHITE + "Unlocked with the purchase of " + ChatColor.GREEN + "SUPPORTER" + ChatColor.WHITE + " rank"){
        public void activateKill(Player player){
            Location location = player.getLocation();
            ItemStack emerald = new ItemStack(Material.EMERALD);
            NBTItem nbt = new NBTItem(emerald);
            NBTCompound customAttributes = nbt.addCompound("CustomAttributes");
            customAttributes.setBoolean("NO_PICKUP", true);
            emerald = nbt.getItem();

            ItemStack emeraldBlock = emerald.clone();
            emeraldBlock.setType(Material.EMERALD_BLOCK);
            int j = 0;
            for(int i = 0; i<10; i++){
                location.getWorld().dropItemNaturally(location, j == 0 ? emeraldBlock : emerald);
                j = Math.abs(j) - 1; //lol
            }
        }
    };



    private static Plugin pl = DesertMain.getInstance;

    public final String displayName;
    public final ItemStack icon;
    public final ItemStack lockedIcon;
    public final CosmeticType cosmeticType;
    public final boolean hidden;

    Cosmetic(String name, Material iconType, String iconDesc, CosmeticType type){
        this(name, iconType, iconDesc, type, false);
    }

    Cosmetic(String name, Material iconType, String iconDesc, CosmeticType type, boolean hidden){
        this(name, iconType, iconDesc, type, hidden, null);
    }

    Cosmetic(String name, Material iconType, String iconDesc, CosmeticType type, String unlockMethod){
        this(name, iconType, iconDesc, type, false, unlockMethod);
    }

    Cosmetic(String name, Material iconType, String iconDesc, CosmeticType type, boolean hidden, String unlockMethod){
        displayName = name;
        cosmeticType = type;
        ItemStack item = new ItemStack(iconType);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + name);
        ArrayList<String> lore = new ArrayList<>(StringUtil.wrapLore(iconDesc));
        lore.add(0, "");
        lore.add(ChatColor.GRAY + "Type: " + ChatColor.GOLD + type);
        meta.setLore(lore);
        item.setItemMeta(meta);

        NBTItem nbt = new NBTItem(item);
        nbt.setString("NAME", name());
        icon = nbt.getItem();

        ItemStack locked = icon.clone();
        locked.setType(Material.INK_SACK);
        locked.setDurability((short) 8);
        ItemMeta lockedMeta = locked.getItemMeta();
        lockedMeta.setDisplayName(ChatColor.RED + ChatColor.stripColor(name));
        ArrayList<String> lockedLore = new ArrayList<>();
        lockedLore.add(0, "");
        lockedLore.add(ChatColor.RED + "This cosmetic is locked!");
        if(unlockMethod != null){
            lockedLore.add(unlockMethod);
        }
        lockedMeta.setLore(lockedLore);
        locked.setItemMeta(lockedMeta);
        lockedIcon = locked;
        this.hidden = hidden;
    }

    static List<ParticleEffect.NoteColor> noteColors = new ArrayList<>();
    static List<ParticleEffect.OrdinaryColor> rColors = new ArrayList<>();
    static List<String> deathMessages = Arrays.asList("*angry noises*", "Why'd you have to do that?!", "Oh no, my streak!", "You'll pay for this.", "I call hacks!", "But my gear was so good!", "I'm staying determined!");
    static List<ParticleEffect> eParticles = Arrays.asList(ParticleEffect.BLOCK_CRACK, ParticleEffect.DRIP_WATER, ParticleEffect.DRIP_LAVA, ParticleEffect.SMOKE_NORMAL);
    static{
        for(int i = 0; i<=24; i+=3){
            noteColors.add(new ParticleEffect.NoteColor(i));
        }
        rColors.add(new ParticleEffect.OrdinaryColor(Color.RED));
        rColors.add(new ParticleEffect.OrdinaryColor(Color.ORANGE));
        rColors.add(new ParticleEffect.OrdinaryColor(Color.YELLOW));
        rColors.add(new ParticleEffect.OrdinaryColor(Color.LIME));
        rColors.add(new ParticleEffect.OrdinaryColor(Color.BLUE));
        rColors.add(new ParticleEffect.OrdinaryColor(Color.fromRGB(75, 0, 130)));
        rColors.add(new ParticleEffect.OrdinaryColor(Color.fromRGB(133, 0, 255)));
    }

    public static final HashMap<UUID, Integer> trails = new HashMap<>();


    static class CosmeticActivationException extends IllegalArgumentException{
        private CosmeticActivationException(){this("Error activating cosmetic");}
        CosmeticActivationException(String message){super(message);}
    }


    public static void init(Player player){
        CosmeticType[] types = CosmeticType.values();
        for(CosmeticType type : types){
            pl.getConfig().set("players." + player.getUniqueId() + ".cosmetics." + type.name() + ".selected", "NONE");
        }
        pl.getConfig().set("players." + player.getUniqueId() + ".cosmetics.acquired", new ArrayList<String>());
        pl.saveConfig();
    }

    public final boolean isSelected(Player player){
        try{
            return valueOf(pl.getConfig().getString("players." + player.getUniqueId() + ".cosmetics." + cosmeticType.name() + ".selected")).equals(this);
        }catch(IllegalArgumentException ex){return false;}
    }

    public final boolean select(Player player){
        if(!hasCosmetic(player)) return false;
        pl.getConfig().set("players." + player.getUniqueId() + ".cosmetics." + cosmeticType.name() + ".selected", this.name());
        pl.saveConfig();
        return true;
    }

    public final boolean deselect(Player player){
        if(!isSelected(player)) return false;
        pl.getConfig().set("players." + player.getUniqueId() + ".cosmetics." + cosmeticType.name() + ".selected", "NONE");
        pl.saveConfig();
        return true;
    }

    public final void grant(Player player){
        List<String> cosmetics = pl.getConfig().getStringList("players." + player.getUniqueId() + ".cosmetics.acquired");
        if(!cosmetics.contains(name())) cosmetics.add(name());
        pl.getConfig().set("players." + player.getUniqueId() + ".cosmetics.acquired", cosmetics);
        pl.saveConfig();
        player.sendMessage(ChatColor.GREEN + "COSMETIC ACQUIRED! " + ChatColor.GREEN + "You got: " + this + "\n" + ChatColor.GRAY + "Select it with /cosmetics!");
    }

    public final boolean hasCosmetic(Player player){
        return pl.getConfig().getStringList("players." + player.getUniqueId() + ".cosmetics.acquired").contains(name());
    }

    public final static Cosmetic getSelected(Player player, CosmeticType type){
        String selectedRaw = pl.getConfig().getString("players." + player.getUniqueId() + ".cosmetics." + type.name() + ".selected");
        if(selectedRaw.equals("NONE")) return null;
        else return Cosmetic.valueOf(selectedRaw);
    }

    public static Cosmetic getFromName(String name){
        for(Cosmetic cosmetic : values()){
            if(cosmetic.displayName.equalsIgnoreCase(name)) return cosmetic;
        }
        return null;
    }

    private static void arrowTrail(Arrow arrow, boolean fast, ParticleEffect particle, ParticleEffect.ParticleColor color){
        if(!fast){
            particle.display(0, 0, 0, 0, 1, arrow.getLocation());
        }else{
            Location baseLocation = arrow.getLocation();
            Vector vector = arrow.getVelocity();
            double distance = 0.2;
            if(particle.equals(ParticleEffect.REDSTONE)) distance = 0.05;
            final int steps = (int) (vector.length() / distance);
            final Vector increment = vector.clone().normalize().multiply(distance);
            final Vector base = increment.clone();
            for (int step = 0; step < steps; step++) {
                final Location location = baseLocation.clone().add(base);
                if(color == null) particle.display(0, 0, 0, 0, 1, location, 300);
                else particle.display(color, arrow.getLocation(), 300);
                base.add(increment);
            }
        }
    }

    private static void arrowTrail(Arrow arrow, boolean fast, ParticleEffect particle){
        arrowTrail(arrow, fast, particle, null);
    }

    public String toString(){
        return displayName;
    }

    public enum CosmeticType{
        PARTICLE_EFFECT("Particle Effect"),
        KILL_EFFECT("Kill Effect"),
        DEATH_EFFECT("Death Effect"),
        STREAK_EFFECT("Streak Effect"),
        ARROW_TRAIL("Arrow Trail");
        public String displayName;
        CosmeticType(String name){
            displayName = name;
        }
        public String toString(){
            return displayName;
        }
    }
}
