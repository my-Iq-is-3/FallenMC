package me.zach.DesertMC.cosmetics;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTEntity;
import de.tr7zw.nbtapi.NBTItem;
import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.Utils.Particle.ParticleEffect;
import me.zach.DesertMC.Utils.RankUtils.RankEvents;
import me.zach.DesertMC.Utils.StringUtils.StringUtil;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

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
                    else Cosmetic.standardArrowTrail(arrow, fast, ParticleEffect.VILLAGER_HAPPY);
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
                    else Cosmetic.standardArrowTrail(arrow, fast, ParticleEffect.FLAME);
                }
            }.runTaskTimerAsynchronously(DesertMain.getInstance, 0, 1);
            //trails.put(arrow.getUniqueId(), trail.getTaskId());
        }
    },
    MUSICAL_ARROWS("Musical Arrows", Material.JUKEBOX, ChatColor.YELLOW + "Your arrows fly with graceful musical chords following them!", CosmeticType.ARROW_TRAIL){
        public void activateArrow(Arrow arrow, boolean fast){
            BukkitTask trail = new BukkitRunnable(){
                int colorIndex = 0;
                @Override
                public void run(){
                    if(arrow.isOnGround() || arrow.isDead()) cancel();
                    else{
                        ParticleEffect.NoteColor pColor = noteColors[colorIndex];
                        Cosmetic.standardArrowTrail(arrow, fast, ParticleEffect.NOTE, pColor);
                        if(arrow.getShooter() instanceof Player){
                            ((Player) arrow.getShooter()).playNote(arrow.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.C));
                        }
                        colorIndex = colorIndex + 1 < rColors.length ? colorIndex + 1 : 0;
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
                    else Cosmetic.standardArrowTrail(arrow, fast, ParticleEffect.HEART);
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
                        ParticleEffect.ParticleColor pColor = rColors[colorIndex];
                        Cosmetic.standardArrowTrail(arrow, fast, ParticleEffect.REDSTONE, pColor);
                        colorIndex = colorIndex + 1 < rColors.length ? colorIndex + 1 : 0;
                    }
                }
            }.runTaskTimer(DesertMain.getInstance, 0, 1);
        }
    },

    DEATH_MESSAGES("Death Messages", Material.MAP,  ChatColor.YELLOW + "Displays a random message to other players at the spot that you died!", CosmeticType.DEATH_EFFECT, true){
        public void activateDeath(Player player){
            ArmorStand stand = (ArmorStand) player.getLocation().getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
            stand.setVisible(true);
            stand.setMarker(true);
            stand.setCustomName(deathMessages.get(ThreadLocalRandom.current().nextInt(deathMessages.size())));
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
                    else Cosmetic.standardArrowTrail(arrow, fast, ParticleEffect.DRIP_WATER);
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
                    else Cosmetic.standardArrowTrail(arrow, fast, ParticleEffect.DRIP_LAVA);
                }
            }.runTaskTimerAsynchronously(pl, 0, 1);
        }
    },
    EMERALD_DESTRUCTION("Emerald Destruction", Material.EMERALD_BLOCK, ChatColor.YELLOW + "Eliminate your foes with flair, and all sorts of stylish emerald things popping out of them.", CosmeticType.KILL_EFFECT, ChatColor.WHITE + "Unlocked with the purchase of " + ChatColor.GREEN + "SUPPORTER" + ChatColor.WHITE + " rank"){
        public void activateKill(Player player){
            standardKillEffect(player.getLocation(), 10, Material.EMERALD, Material.EMERALD_BLOCK);
        }
    },
    EVERYTHING("Diverse Destruction", Material.STAINED_CLAY, ChatColor.YELLOW + "When you defeat an enemy, everything comes out of them...?", CosmeticType.KILL_EFFECT){
        public void activateKill(Player player) {
            List<Material> materialsList = new ArrayList<>(Arrays.asList(Material.values()));
            Collections.shuffle(materialsList);
            standardKillEffect(player.getLocation(), 15, materialsList.toArray(new Material[0]));
        }
    },
    SKULL_DESTRUCTION("Skull Destruction", Material.GHAST_TEAR, "As if slaying them wasn't enough, this kill effect lifts your enemy's skull from where they previously stood and draws it into the air, only to destroy it afterward. Talk about overkill, jeez.", CosmeticType.KILL_EFFECT, true){
        public void activateKill(Player player){
            ArmorStand armorStand = player.getWorld().spawn(player.getLocation(), ArmorStand.class);
            armorStand.setVisible(false);
            new NBTEntity(armorStand).setBoolean("Invulnerable", true);
            armorStand.setCustomName(RankEvents.rankSession.get(player.getUniqueId()).c + player.getName());
            armorStand.setGravity(false);
            armorStand.setCustomNameVisible(true);
        }
    };



    private static final Plugin pl = DesertMain.getInstance;

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
        List<String> lore = StringUtil.wrapLore("\n" + iconDesc);
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

    static ParticleEffect.NoteColor[] noteColors = new ParticleEffect.NoteColor[9];
    static ParticleEffect.OrdinaryColor[] rColors = new ParticleEffect.OrdinaryColor[]{
            new ParticleEffect.OrdinaryColor(Color.RED),
            new ParticleEffect.OrdinaryColor(Color.ORANGE),
            new ParticleEffect.OrdinaryColor(Color.YELLOW),
            new ParticleEffect.OrdinaryColor(Color.LIME),
            new ParticleEffect.OrdinaryColor(Color.BLUE),
            new ParticleEffect.OrdinaryColor(Color.fromRGB(75, 0, 130)),
            new ParticleEffect.OrdinaryColor(Color.fromRGB(133, 0, 255))
    };
    static List<String> deathMessages = Arrays.asList("*angry noises*", "Why'd you have to do that?!", "Oh no, my streak!", "You'll pay for this.", "I call hacks!", "But my gear was so good!", "I'm staying determined!");
    static List<ParticleEffect> eParticles = Arrays.asList(ParticleEffect.BLOCK_CRACK, ParticleEffect.DRIP_WATER, ParticleEffect.DRIP_LAVA, ParticleEffect.SMOKE_NORMAL);
    static{
        for(int i = 0; i<9; i++){
            noteColors[i] = new ParticleEffect.NoteColor(i * 3);
        }
    }

    public static final HashMap<UUID, Integer> trails = new HashMap<>();


    static class CosmeticActivationException extends IllegalArgumentException{
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
        player.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "COSMETIC ACQUIRED! " + ChatColor.GREEN + "You got: " + this + "\n" + ChatColor.GRAY + "Select it with /cosmetics!");
    }

    public final boolean hasCosmetic(Player player){
        return pl.getConfig().getStringList("players." + player.getUniqueId() + ".cosmetics.acquired").contains(name());
    }

    public static Cosmetic getSelected(Player player, CosmeticType type){
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

    private static void standardArrowTrail(Arrow arrow, boolean fast, ParticleEffect particle, ParticleEffect.ParticleColor color){
        if(!fast){
            if(color == null) particle.display(0, 0, 0, 1, 1, arrow.getLocation(), 300);
            else particle.display(color, arrow.getLocation(), 300);
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

    private static void standardArrowTrail(Arrow arrow, boolean fast, ParticleEffect particle){
        standardArrowTrail(arrow, fast, particle, null);
    }

    private static void standardKillEffect(Location location, int amount, Material... materials){
        ItemStack base = new ItemStack(materials[0]);
        NBTItem nbt = new NBTItem(base);
        NBTCompound customAttributes = nbt.addCompound("CustomAttributes");
        customAttributes.setBoolean("NO_PICKUP", true);
        base = nbt.getItem();
        int stacksSize = Math.min(materials.length, amount);
        ItemStack[] itemStacks = new ItemStack[stacksSize];
        itemStacks[0] = base;

        for(int i = 1, j = 0; i<stacksSize; i++, j = (j + 1 < materials.length ? j + 1 : 0)){
            ItemStack itemStack = base.clone();
            itemStack.setType(materials[j]);
            itemStacks[i] = itemStack;
        }
        Item[] items = new Item[amount];
        for(int i = 0, j = 0; i<amount; i++, j = (j + 1 < itemStacks.length ? j + 1 : 0)){
            Item dropped = location.getWorld().dropItemNaturally(location, itemStacks[j]);
            items[i] = dropped;
        }
        Bukkit.getScheduler().runTaskLater(DesertMain.getInstance, () -> {
            for(Item item : items) item.remove();
        }, 40);
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
