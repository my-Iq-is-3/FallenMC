package me.zach.DesertMC.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Instrument;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import static org.bukkit.Note.natural;
import static org.bukkit.Note.sharp;
import static org.bukkit.Note.Tone;

public class MiscUtils {
    private static Plugin pl = Bukkit.getPluginManager().getPlugin("Fallen");
    public static void ootChestFanfare(Player player){
        player.playNote(player.getLocation().add(0, 3, 0), Instrument.PIANO, natural(1, Tone.F));
        player.playNote(player.getLocation().add(0, 3, 0), Instrument.PIANO, sharp(1, Tone.G));

        new BukkitRunnable(){
            public void run(){
                player.playNote(player.getLocation().add(0, 2, 0), Instrument.PIANO, sharp(1, Tone.F));
                player.playNote(player.getLocation().add(0, 2, 0), Instrument.PIANO, natural(1, Tone.A));
            }
        }.runTaskLater(pl, 4);

        new BukkitRunnable(){
            public void run(){
                player.playNote(player.getLocation().add(0, 1, 0), Instrument.PIANO, natural(1, Tone.G));
                player.playNote(player.getLocation().add(0, 1, 0), Instrument.PIANO, sharp(1, Tone.A));
            }
        }.runTaskLater(pl, 8);

        new BukkitRunnable(){
            public void run(){
                player.playNote(player.getLocation(), Instrument.PIANO, sharp(1, Tone.G));
                player.playNote(player.getLocation(), Instrument.PIANO, natural(1, Tone.B));
            }
        }.runTaskLater(pl, 12);
    }
}
