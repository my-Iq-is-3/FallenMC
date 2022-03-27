package me.zach.DesertMC.Utils;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTEntity;
import de.tr7zw.nbtapi.NBTItem;
import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.GameMechanics.Events;
import me.zach.DesertMC.GameMechanics.hitbox.HitboxListener;
import me.zach.DesertMC.Prefix;
import me.zach.DesertMC.Utils.Config.ConfigUtils;
import me.zach.DesertMC.Utils.RankUtils.Rank;
import me.zach.DesertMC.Utils.StringUtils.StringUtil;
import me.zach.DesertMC.Utils.nbt.NBTUtil;
import me.zach.DesertMC.Utils.structs.Pair;
import me.zach.DesertMC.holo.Hologram;
import me.zach.databank.DBCore;
import me.zach.databank.saver.PlayerData;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

import static org.bukkit.Note.*;

public class MiscUtils {
    private static final ItemStack emptyPane = generateItem(Material.STAINED_GLASS_PANE, " ", Collections.emptyList(), (byte) 7, 1);
    private static final Plugin pl = DesertMain.getInstance;
    public static DecimalFormat DMG_FORMATTER = new DecimalFormat();
    static{
        MiscUtils.DMG_FORMATTER.setMaximumFractionDigits(1);
    }

    public static String cleanCoordinates(Location location){
        return "(" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ")";
    }

    public static void ootChestFanfare(Player player){
        player.playNote(player.getLocation(), Instrument.PIANO, natural(0, Tone.F));
        player.playNote(player.getLocation(), Instrument.PIANO, sharp(0, Tone.G));

        new BukkitRunnable(){
            public void run(){
                player.playNote(player.getLocation(), Instrument.PIANO, sharp(1, Tone.F));
                player.playNote(player.getLocation(), Instrument.PIANO, natural(1, Tone.A));
            }
        }.runTaskLater(pl, 4);

        new BukkitRunnable(){
            public void run(){
                player.playNote(player.getLocation(), Instrument.PIANO, natural(1, Tone.G));
                player.playNote(player.getLocation(), Instrument.PIANO, sharp(1, Tone.A));
            }
        }.runTaskLater(pl, 8);

        new BukkitRunnable(){
            public void run(){
                player.playNote(player.getLocation(), Instrument.PIANO, sharp(1, Tone.G));
                player.playNote(player.getLocation(), Instrument.PIANO, natural(1, Tone.B));
            }
        }.runTaskLater(pl, 12);
    }

    public static Firework spawnFirework(Location location, int power, boolean flicker, boolean trail, FireworkEffect.Type type, Color... colors){
        return spawnFirework(location, power, flicker, trail, null, type, colors);
    }

    public static Firework spawnFirework(Location location, int power, boolean flicker, boolean trail, Color fade, FireworkEffect.Type type, Color... colors){
        Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        FireworkEffect.Builder fireworkEffect = FireworkEffect.builder().with(type).flicker(flicker).trail(trail).withColor(colors);
        if(fade != null) fireworkEffect.withFade(fade);
        fireworkMeta.addEffect(fireworkEffect.build());
        fireworkMeta.setPower(power);
        firework.setFireworkMeta(fireworkMeta);
        if(power == 0){
            Bukkit.getScheduler().runTaskLater(DesertMain.getInstance, firework::detonate, 2);
        }
        return firework;
    }

    public static String getOrdinalSuffix(int num){
        if((num + "").endsWith("1") && num != 11) return "st";
        else if((num + "").endsWith("2") && num != 12) return "nd";
        else if((num + "").endsWith("3") && num != 13) return "rd";
        return "th";
    }

    public static <K, V> List<Map.Entry<K, V>> sortValues(HashMap<K, V> map){
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        list.sort((e1, e2) -> {
            V value = e1.getValue();
            if(value instanceof Comparable){
                return ((Comparable<V>) value).compareTo(e2.getValue());
            }else throw new IllegalArgumentException("HashMap parameter isn't comparable");
        });
        return list;
    }

    public static <T> void clearDuplicates(List<T> list){
        Set<T> clearDuplicates = new HashSet<>(list);
        list.clear();
        list.addAll(clearDuplicates);
    }

    public static <T extends Entity> List<T> getNearbyEntities(Class<T> type, Entity entity, double range){
        return getNearbyEntities(type, entity, range, range, range);
    }

    public static List<Damageable> getNearbyDamageables(Entity entity, double range){
        return getNearbyDamageables(entity, range, range, range);
    }

    public static List<Damageable> getNearbyDamageables(Entity entity, double xRange, double yRange, double zRange){
        List<Entity> nearbyEntities = entity.getNearbyEntities(xRange, yRange, zRange);
        ArrayList<Damageable> nearbyDamageables = new ArrayList<>();
        for(Entity e : nearbyEntities){
            Damageable damageable = canDamage(e);
            if(damageable != null) nearbyDamageables.add(damageable);
        }
        return nearbyDamageables;
    }

    public static double trueRandom(){
        double base = Math.random();
        return (base - 0.5) * 2;
    }

    public static Damageable canDamage(Entity entity){
        Location location = entity.getLocation();
        if(Events.invincible.contains(entity.getUniqueId()) || HitboxListener.isInSafeZone(location)){
            return null;
        }else{
            NBTEntity nbt = new NBTEntity(entity);
            return entity instanceof Damageable && !nbt.getBoolean("Invulnerable") && !nbt.getBoolean("NoAI") ? (Damageable) entity : null;
        }
    }

    public static boolean isAdmin(Player player){
        Rank rank = ConfigUtils.getRank(player);
        return (rank != null && rank.admin) || player.hasPermission("admin");
    }

    /**
     * Finds all entities within a radius of an already existing entity that are of specified type.<br>
     * Example (getting a list of {@link Player}s within a 5-block radius of the first player listed in <code>Bukkit.getOnlinePlayers()</code>:<blockquote><code>List{@literal <Player>} nearbyPlayers = getNearbyEntities(Player.class, Bukkit.getOnlinePlayers().get(0), 5, 5, 5);</code></blockquote>
     * @author Archonic/DrMlem
     * @param toFind Class of entity to find
     * @param entity Entity to search nearby from
     * @param <T> Entity type to find
     * @return Returns a list of entities of the specified type in the defined radius
     */
    @SuppressWarnings("unchecked")
    public static <T extends Entity> List<T> getNearbyEntities(Class<T> toFind, Entity entity, double xRange, double yRange, double zRange){
        List<Entity> nearbyEntities = entity.getNearbyEntities(xRange, yRange, zRange);
        ArrayList<T> nearbyFiltered = new ArrayList<>();
        for(Entity e : nearbyEntities)
            if(toFind.isInstance(e))
                nearbyFiltered.add((T) e);
        return nearbyFiltered;
    }

    public static ItemStack generateItem(Material type, String name, List<String> description, byte dataValue, int amount){
        return generateItem(type, name, description, dataValue, amount, null);
    }

    public static ItemStack generateItem(Material type, String name, List<String> description, byte dataValue, int amount, String id){
        return generateItem(type, name, description, dataValue, amount, id, false, 1, 0);
    }

    public static ItemStack generateItem(Material type, String name, List<String> description, byte dataValue, int amount, String id, int lives){
        return generateItem(type, name, description, dataValue, amount, id, false, 1, 0, lives);
    }

    public static ItemStack generateItem(Material type, String name, List<String> description, byte dataValue, int amount, String id, boolean canEnchant){
        return generateItem(type, name, description, dataValue, amount, id, canEnchant, 1, 0);
    }

    public static ItemStack generateItem(Material type, String name, List<String> description, byte dataValue, int amount, String id, float attackMultiplier, float defenseBonus){
        return generateItem(type, name, description, dataValue, amount, id, false, attackMultiplier, defenseBonus);
    }

    public static ItemStack generateItem(Material type, String name, List<String> description, byte dataValue, int amount, String id, boolean canEnchant, float attackMultiplier, float defenseBonus){
        return generateItem(type, name, description, dataValue, amount, id, canEnchant, attackMultiplier, defenseBonus, -1);
    }

    /**
     * Generates a custom item with the given parameters.
     *
     * @param attackMultiplier BONUS factor to multiply attack damage by (if any). Default: 1
     * @param defenseBonus Percentage to add to the armor set's total defense BONUS (works in tandem with the vanilla armor protection rates). Default: 0<br><br>example: If a player wore boots generated with a defense bonus of 5, and wore a chestplate with a defense bonus of 15, attack damage against them would be the vanilla damage reduced by 20%.
     */
    public static ItemStack generateItem(Material type, String name, List<String> description, byte dataValue, int amount, String id, boolean canEnchant, float attackMultiplier, float defenseBonus, int lives){
        ItemStack item = dataValue > -1 ? new ItemStack(type, amount, dataValue) : new ItemStack(type, amount);
        ItemMeta meta = item.getItemMeta();
        if(!name.isEmpty()) meta.setDisplayName(name);
        if(!description.isEmpty()) meta.setLore(description);
        meta.spigot().setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(meta);
        boolean hasId = id != null;
        if(hasId){
            NBTItem nbt = new NBTItem(item);
            NBTCompound customAttributes = nbt.addCompound("CustomAttributes");
            customAttributes.setString("ID", id);
            boolean hasAtk = attackMultiplier != 1;
            boolean hasDef = defenseBonus > 0;
            boolean hasLives = lives > 0;
            if(hasAtk) customAttributes.setFloat("ATTACK", attackMultiplier);
            if(hasDef) customAttributes.setFloat("DEFENSE", defenseBonus);
            if(canEnchant) customAttributes.setBoolean("CAN_ENCHANT", true);
            customAttributes.setString("UUID", UUID.randomUUID().toString());
            if(hasLives){
                nbt.setBoolean("Unbreakable", true);
                item = NBTUtil.setLives(nbt.getItem(), lives);
            }else item = nbt.getItem();
        }
        return item;
    }

    public static void setPersistentCustomName(Entity entity, String name){
        if(entity.getPassenger() instanceof ArmorStand) entity.getPassenger().setCustomName(name);
        else new Hologram(name, entity).create();
    }

    public static void removePersistentCustomName(Entity entity){
        Entity passenger = entity.getPassenger();
        if(passenger instanceof ArmorStand){
            entity.eject();
            passenger.remove();
        }
    }

    public static ItemStack getHologramWand(String name){
        ItemStack wandBase = new ItemStack(Material.STICK);
        ItemMeta wandMeta = wandBase.getItemMeta();
        wandMeta.setDisplayName(ChatColor.YELLOW + name);
        wandMeta.setLore(StringUtil.wrapLore(ChatColor.GRAY + "Spawns a hologram named " + ChatColor.YELLOW + name + ChatColor.GRAY + "." + ".\nLeft click block - spawn on block\nRight click - spawn on current location\nRight click entity - spawn riding entity"));
        wandBase.setItemMeta(wandMeta);
        NBTItem nbt = new NBTItem(wandBase);
        NBTCompound customAttr = nbt.addCompound("CustomAttributes");
        customAttr.setString("HOLOGRAM_NAME", name.replaceAll(" ", "_"));
        customAttr.setString("ID", "HOLOGRAM_WAND");
        return nbt.getItem();
    }

    public static Player getClosest(Location location) throws IllegalStateException {
        Iterator<? extends Player> playerIterator = Bukkit.getOnlinePlayers().iterator();
        if(!playerIterator.hasNext()) throw new IllegalStateException("Closest player check request with no players online!");
        Player firstPlayer = playerIterator.next();
        Pair<Player, Integer> closest = new Pair<>(firstPlayer, (int) firstPlayer.getLocation().distanceSquared(location));
        while(playerIterator.hasNext()){
            Player player = playerIterator.next();
            int distance = (int) player.getLocation().distanceSquared(location);
            if(distance < closest.second){
                closest = new Pair<>(player, distance);
            }
        }
        return closest.first;
    }

    public static ItemStack getEmptyPane(){
        return emptyPane;
    }

    public static ItemStack getEmptyPane(byte color){
        return generateItem(Material.STAINED_GLASS_PANE, " ", Collections.emptyList(), color, 1);
    }

    public static ItemStack getGemsItem(Player player){
        return getGemsItem(ConfigUtils.getGems(player));
    }

    public static ItemStack getGemsItem(int gems){
        return generateItem(Material.EMERALD, ChatColor.GREEN.toString() + gems + (gems == 1 ? " Gem" : " Gems"), Collections.emptyList(), (byte) -1, 1);
    }

    public static boolean trueEmpty(Block block){
        return !block.getType().isSolid();
    }

    public static String makePlural(String str){
        return str.endsWith("s") ? str : str + "s";
    }

    public static String getRankColor(Player player){
        return getRankColor(player.getUniqueId());
    }

    public static String getRankColor(UUID uuid){
        Rank rank = ConfigUtils.getRank(uuid);
        return getRankColor(rank);
    }

    public static String getRankColor(Rank rank){
        return rank == null ? ChatColor.GRAY.toString() : rank.c;
    }

    public static void refreshTablistName(Player player){
        Prefix title = ConfigUtils.getSelectedTitle(player);
        player.setPlayerListName(title != null ? title + " " + player.getDisplayName() : player.getDisplayName());
    }

    public static ArrayList<Location> getCircle(Location center, double radius, int amount) {
        World world = center.getWorld();
        double increment = 6.283185307179586D / (double)amount;
        ArrayList<Location> locations = new ArrayList<>();

        for(int i = 0; i < amount; ++i) {
            double angle = (double)i * increment;
            double x = center.getX() + radius * Math.cos(angle);
            double z = center.getZ() + radius * Math.sin(angle);
            locations.add(new Location(world, x, center.getY(), z));
        }

        return locations;
    }

    public static boolean isCoolPerson(UUID uuid){
        return uuid.equals(ImportantPeople.ARCHMLEM.uuid) || uuid.equals(ImportantPeople.ONE_IQ.uuid);
    }

    public static void setOwner(Item item, Player owner){
        item.setCustomName(owner.getDisplayName() + "'s " + ChatColor.WHITE + item.getItemStack().getItemMeta().getDisplayName());
        item.setCustomNameVisible(true);
        NBTEntity nbt = new NBTEntity(item);
        nbt.setString("Owner", owner.getUniqueId().toString());
    }

    public static String potionEffectToString(PotionEffect potionEffect){
        return capitalizeEnum(potionEffect.getType().getName()) + " " + potionEffect.getAmplifier() + 1;
    }

    public static ItemStack decrementItem(ItemStack item){
        int amount = item.getAmount();
        if(amount > 1) item.setAmount(amount - 1);
        else item = null;
        return item;
    }

    /**
     *
     * @return A list of strings derived from {@code wideTarget}, only if the start of them matches the start of {@code current}.
     */
    public static List<String> narrowTabComplete(String current, Collection<String> wideTarget){
        if(wideTarget.isEmpty() || current.isEmpty()) return new ArrayList<>(wideTarget);
        List<String> product = new ArrayList<>();
        for(String string : wideTarget){
            if(string.toLowerCase().startsWith(current.toLowerCase())){
                product.add(string);
            }
        }
        return product;
    }

    /**
     * Rounds the provided Location to the nearest whole x, y, and z.
     * @param location the Location to modify
     * @return the modified Location
     */
    public static Location roundLocation(Location location){
        location.setX(Math.round(location.getX()));
        location.setY(Math.round(location.getY()));
        location.setZ(Math.round(location.getZ()));
        return location;
    }

    private static final double INDICATOR_MIN_DISTANCE = 0.3;
    private static final double INDICATOR_MAX_DISTANCE = 1.4;

    public static void showIndicator(String content, Location center){
        Location location = indicatorLocation(center);
        Hologram holo = new Hologram(content, location);
        holo.create();
        new NBTEntity(holo.getStand()).setBoolean("Indicator", true);
        Bukkit.getScheduler().runTaskLater(DesertMain.getInstance, holo::remove, 15);
    }

    /**
     *
     * @return A location randomly deviated slightly from the center, ideal for a player.
     */
    public static Location indicatorLocation(Location center){
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return center.clone().add(randomWithRandomSign(INDICATOR_MIN_DISTANCE, INDICATOR_MAX_DISTANCE, random), random.nextDouble(INDICATOR_MIN_DISTANCE,INDICATOR_MAX_DISTANCE), randomWithRandomSign(INDICATOR_MIN_DISTANCE, INDICATOR_MAX_DISTANCE, random));
    }

    public static void damageIndicator(double damage, Entity damager, Entity entity){
        if(damage > 0){
            boolean playerInvolved;
            playerInvolved = damager instanceof Player || entity instanceof Player;
            if(playerInvolved){
                String content = DMG_FORMATTER.format(damage);
                if(damage > 15)
                    content = ChatColor.RED + ChatColor.MAGIC.toString() + "A" + ChatColor.GOLD + ChatColor.BOLD + content + ChatColor.RED + ChatColor.MAGIC + "A";
                else if(damage > 10) content = ChatColor.GOLD + ChatColor.BOLD.toString() + content;
                else if(damage > 5) content = ChatColor.GOLD + content;
                else content = ChatColor.GRAY + content;
                Location center = entity.getLocation();
                MiscUtils.showIndicator(content, center);
            }
        }
    }

    public static double randomWithRandomSign(double origin, double bound){
        return randomWithRandomSign(origin, bound, ThreadLocalRandom.current());
    }

    public static double randomWithRandomSign(double origin, double bound, ThreadLocalRandom random){
        double original = random.nextDouble(origin, bound);
        return random.nextBoolean() ? -original : original;
    }

    private static Field tickField;

    static{
        try{
            tickField = Bukkit.getScheduler().getClass().getDeclaredField("currentTick");
            tickField.setAccessible(true);
        }catch(NoSuchFieldException e){
            e.printStackTrace();
        }
    }

    public static int getCurrentTick() {
        try{
            return tickField.getInt(Bukkit.getScheduler());
        }catch(IllegalAccessException e){
            e.printStackTrace();
            return 50;
        }
    }

    public static int getEmpties(Inventory inventory){
        int empties = 0;
        for(ItemStack item : inventory.getContents()){
            if(item == null) empties++;
        }
        return empties;
    }

    /**
     * @param min range minimum (inclusive)
     * @param max range maximum (exclusive)
     * @return an array of ints in the range of the two parameters provided.
     */
    public static int[] range(int min, int max){
        int[] product = new int[max - min];
        for(int i = 0; min != max; min++, i++){
            product[i] = min;
        }
        return product;
    }

    public static <T> int indexOf(T[] array, T object){
        for(int i = 0; i<array.length; i++){
            if(array[i].equals(object)) return i;
        }
        return -1;
    }

    public static <T> boolean contains(T[] array, T object){
        for(T item : array){
            if(item.equals(object)) return true;
        }
        return false;
    }

    /**
     * {@link Arrays#asList(T[])} returns an immutable list, so this method was created.
     */
    @SafeVarargs
    public static <T> ArrayList<T> asArrayList(T... items){
        return new ArrayList<>(Arrays.asList(items));
    }

    //an example converter
    public static final Function<String, UUID> UUID_CONVERTER = UUID::fromString;

    public static <B, A> Collection<A> convertCollection(Collection<B> before, Collection<A> newCollection, Function<B, A> converter){
        for(B unconverted : before){
            newCollection.add(converter.apply(unconverted));
        }
        return newCollection;
    }

    public static <T> List<T> trimList(List<T> list, int trimTo){
        while(list.size() > trimTo) list.remove(list.size() - 1);
        return list;
    }

    public static <T> T ensureDefault(String path, T defaultValue, Plugin plugin){
        return ensureDefault(path, defaultValue, plugin, false);
    }

    @SuppressWarnings("unchecked")
    public static <T> T ensureDefault(String path, T defaultValue, Plugin plugin, boolean isConfigurationSection){
        FileConfiguration config = plugin.getConfig();
        if(!config.contains(path)){
            if(isConfigurationSection) config.createSection(path, (Map<?, ?>) defaultValue);
            else config.set(path, defaultValue);
            plugin.saveConfig();
            return defaultValue;
        }else{
            if(isConfigurationSection) return (T) config.getConfigurationSection(path).getValues(true);
            else return (T) config.get(path);
        }
    }

    public static String capitalizeEnum(String str) {
        String[] words = str.toLowerCase().split("_");
        for(int i = 0; i< words.length; i++){
            String capital = StringUtil.capitalizeFirst(words[i]);
            words[i] = capital;
        }
        return String.join(" ", words);
    }
}
