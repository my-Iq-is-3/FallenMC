package me.zach.DesertMC.Utils;

import me.zach.DesertMC.DesertMain;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import static org.bukkit.Note.natural;
import static org.bukkit.Note.sharp;
import static org.bukkit.Note.Tone;

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
        FireworkEffect fireworkEffect = FireworkEffect.builder().with(type).flicker(flicker).trail(trail).build();
        fireworkMeta.addEffect(fireworkEffect);
        fireworkMeta.setPower(power);
        firework.setFireworkMeta(fireworkMeta);
        return firework;
    }

    public static String getOrdinalSuffix(int num){
        if((num + "").endsWith("1") && num != 11) return "st";
        else if((num + "").endsWith("2") && num != 12) return "nd";
        else if((num + "").endsWith("3") && num != (10 + 3)) return "rd";
        return "th";
    }
}
