package me.zach.DesertMC.Utils;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTEntity;
import de.tr7zw.nbtapi.NBTItem;
import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.GameMechanics.Events;
import me.zach.DesertMC.Utils.Config.ConfigUtils;
import me.zach.DesertMC.Utils.RankUtils.Rank;
import me.zach.DesertMC.Utils.RankUtils.RankEvents;
import me.zach.DesertMC.Utils.structs.Pair;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static org.bukkit.Note.*;

public class MiscUtils {
    private static final ItemStack emptyPane = generateItem(Material.STAINED_GLASS_PANE, " ", Collections.emptyList(), (byte) 7, 1);
    private static final Plugin pl = DesertMain.getInstance;
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
        Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        FireworkEffect fireworkEffect = FireworkEffect.builder().with(type).flicker(flicker).trail(trail).withColor(colors).build();
        fireworkMeta.addEffect(fireworkEffect);
        fireworkMeta.setPower(power);
        firework.setFireworkMeta(fireworkMeta);
        if(power == 0){
            Bukkit.getScheduler().runTask(DesertMain.getInstance, firework::detonate);
        }
        return firework;
    }

    public static String getOrdinalSuffix(int num){
        if((num + "").endsWith("1") && num != 11) return "st";
        else if((num + "").endsWith("2") && num != 12) return "nd";
        else if((num + "").endsWith("3") && num != (10 + 3)) return "rd";
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
        if(Events.invincible.contains(entity.getUniqueId())) return null;
        else if(entity instanceof Player) return ((Player) entity).getNoDamageTicks() == 0 ? (Damageable) entity : null;
        return !(entity.spigot().isInvulnerable()) && entity instanceof Damageable ? (Damageable) entity : null;
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

    public static ItemStack generateItem(Material type, String name, List<String> description, byte dataValue, int amount, String id, boolean canEnchant){
        return generateItem(type, name, description, dataValue, amount, id, canEnchant, 1, 0);
    }

    public static ItemStack generateItem(Material type, String name, List<String> description, byte dataValue, int amount, String id, float attackMultiplier, float defenseBonus){
        return generateItem(type, name, description, dataValue, amount, id, false, attackMultiplier, defenseBonus);
    }

    /**
     * Generates a custom item with the given parameters.
     *
     * @param attackMultiplier BONUS factor to multiply attack damage by (if any). Default: 1
     * @param defenseBonus Percentage to add to the armor set's total defense BONUS (works in tandem with the vanilla armor protection rates). Default: 0<br><br>example: If a player wore boots generated with a defense bonus of 5, and wore a chestplate with a defense bonus of 15, attack damage against them would be the vanilla damage reduced by 20%.
     */
    public static ItemStack generateItem(Material type, String name, List<String> description, byte dataValue, int amount, String id, boolean canEnchant, float attackMultiplier, float defenseBonus){
        ItemStack item = dataValue > -1 ? new ItemStack(type, amount, dataValue) : new ItemStack(type, amount);
        ItemMeta meta = item.getItemMeta();
        if(!name.isEmpty()) meta.setDisplayName(name);
        if(!description.isEmpty()) meta.setLore(description);
        meta.spigot().setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(meta);
        boolean hasId = id != null;
        boolean hasAtk = attackMultiplier != 1;
        boolean hasDef = defenseBonus > 0;
        if(hasId){
            NBTItem nbt = new NBTItem(item);
            NBTCompound customAttributes = nbt.addCompound("CustomAttributes");
            customAttributes.setString("ID", id);
            if(hasAtk) customAttributes.setFloat("ATTACK", attackMultiplier);
            if(hasDef) customAttributes.setFloat("DEFENSE", defenseBonus);
            if(canEnchant) customAttributes.setBoolean("CAN_ENCHANT", true);
            customAttributes.setString("UUID", UUID.randomUUID().toString());
            item = nbt.getItem();
        }
        return item;
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
        return block.isEmpty() || block.getType() == Material.GRASS || block.getType() == Material.LONG_GRASS || block.getType() == Material.SNOW;
    }

    public static String makePlural(String str){
        return str.endsWith("s") ? str : str + "s";
    }

    public static ChatColor getRankColor(Player player){
        return getRankColor(player.getUniqueId());
    }

    public static ChatColor getRankColor(UUID uuid){
        Rank rank = RankEvents.rankSession.get(uuid);
        if(rank != null){
            return rank.c;
        }else return ChatColor.GRAY;
    }

    public static void setOwner(Item item, Player owner){
        item.setCustomName(MiscUtils.getRankColor(owner) + owner.getName() + "'s " + ChatColor.WHITE + item.getItemStack().getItemMeta().getDisplayName());
        item.setCustomNameVisible(true);
        NBTEntity nbt = new NBTEntity(item);
        nbt.setString("OWNER", owner.getUniqueId().toString());
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

    public static <T> List<T> trimList(List<T> list, int trimTo){
        while(list.size() > trimTo) list.remove(list.size() - 1);
        return list;
    }
    @SuppressWarnings("unchecked")
    public static <T> T ensureDefault(String path, T defaultValue, Plugin plugin){
        FileConfiguration config = plugin.getConfig();
        if(!config.contains(path)){
            config.set(path, defaultValue);
            plugin.saveConfig();
            return defaultValue;
        }else return (T) config.get(path);
    }
}
