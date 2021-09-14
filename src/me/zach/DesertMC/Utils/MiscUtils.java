package me.zach.DesertMC.Utils;

import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.GameMechanics.Events;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static org.bukkit.Note.*;

public class MiscUtils {
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
            Bukkit.getScheduler().runTaskLater(DesertMain.getInstance, () -> {
                firework.detonate();
            }, 2);
        }
        return firework;
    }

    public static String getOrdinalSuffix(int num){
        if((num + "").endsWith("1") && num != 11) return "st";
        else if((num + "").endsWith("2") && num != 12) return "nd";
        else if((num + "").endsWith("3") && num != (10 + 3)) return "rd";
        return "th";
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static HashMap sortValues(HashMap<?, Integer> map){
        List<Map.Entry<?, Integer>> list = new LinkedList<>(map.entrySet());
        list.sort((e1, e2) -> ((Comparable<Integer>) ((e1)).getValue()).compareTo(e2.getValue()));
        HashMap sortedHashMap = new LinkedHashMap();
        for(Object o : list){
            Map.Entry<?, Integer> entry = (Map.Entry<?, Integer>) o;
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
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
        ItemStack item = dataValue > -1 ? new ItemStack(type, amount, dataValue) : new ItemStack(type, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if(!description.isEmpty()) meta.setLore(description);
        item.setItemMeta(meta);
        return item;
    }

    public static boolean trueEmpty(Block block){
        return block.isEmpty() || block.getType() == Material.GRASS || block.getType() == Material.LONG_GRASS || block.getType() == Material.SNOW;
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
}
