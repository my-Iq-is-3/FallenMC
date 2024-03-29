package me.zach.DesertMC.ClassManager;

import me.zach.DesertMC.GameMechanics.hitbox.HitboxListener;
import me.zach.DesertMC.GameMechanics.hitbox.HitboxManager;
import me.zach.DesertMC.Prefix;
import me.zach.DesertMC.Utils.ActionBar.ActionBarUtils;
import me.zach.DesertMC.Utils.Config.ConfigUtils;
import me.zach.DesertMC.Utils.MiscUtils;
import me.zach.DesertMC.Utils.PlayerUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.fallenmc.risenboss.main.utils.RisenUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static me.zach.DesertMC.Utils.Config.ConfigUtils.findClass;

public class TravellerEvents implements Listener {
    public static final HashMap<UUID, Set<Block>> travelled = new HashMap<>();
    public static final Set<UUID> blockNotifs = new HashSet<>();
    @EventHandler(priority = EventPriority.HIGH)
    public void blockGet(PlayerMoveEvent e){
        Player player = e.getPlayer();
        Location to = e.getTo();
        Block block = to.clone().subtract(0, 1, 0).getBlock();
        boolean safeZone = HitboxListener.isInSafeZone(to);
        boolean safeZoneBefore = HitboxListener.isInSafeZone(e.getFrom());
        if(block.getType().isSolid()){
            if(!RisenUtils.isBoss(player.getUniqueId()) && !safeZone){
                UUID uuid = player.getUniqueId();
                if(!travelled.containsKey(uuid)){
                    if(ConfigUtils.getLevel(findClass(uuid), uuid) > 5) travelled.put(uuid, new HashSet<>());
                }else{
                    Set<Block> blockSet = travelled.get(uuid);
                    if(blockSet.add(block)){
                        if(notifications(uuid)){
                            String actionBar = ChatColor.YELLOW + "Blocks travelled this run: " + ChatColor.GREEN + blockSet.size();
                            if(blockSet.size() == 250){
                                player.sendMessage(Prefix.SERVER + ChatColor.YELLOW.toString() + ChatColor.BOLD + " TIP: " + ChatColor.GRAY + "Toggle block notifications using /blocknotifications");
                            }
                            ActionBarUtils.sendActionBar(player, actionBar);
                        }
                        if(findClass(uuid).equalsIgnoreCase("scout")){
                            if(blockSet.size() % 100 == 0){
                                player.setWalkSpeed(player.getWalkSpeed() + 0.002f);
                                sendAchieved(ChatColor.AQUA.toString() + ChatColor.BOLD + "+1%" + ChatColor.AQUA + " move speed!", player);
                            }
                        }else if(findClass(uuid).equalsIgnoreCase("wizard")){
                            if(blockSet.size() % 500 == 0){
                                player.setMaxHealth(player.getMaxHealth() + 2);
                                player.setHealth(player.getHealth() + 2);
                                sendAchieved(ChatColor.AQUA.toString() + ChatColor.BOLD + "+2" + ChatColor.RED + " max health!", player);
                            }
                        }else if(findClass(uuid).equals("tank")){
                            if(blockSet.size() % 100 == 0){
                                sendAchieved(ChatColor.AQUA.toString() + ChatColor.BOLD + "+1% " + ChatColor.DARK_GREEN + "defense!", player);
                            }
                        }else if(findClass(uuid).equals("corrupter")){
                            if(blockSet.size() % 100 == 0)
                                sendAchieved(ChatColor.AQUA + ChatColor.BOLD.toString() + "+1% " + ChatColor.DARK_RED + "damage!", player);
                        }
                    }
                }
            }
        }
        if(safeZone && !safeZoneBefore && !PlayerUtils.isIdle(player)){
            e.setTo(e.getFrom());
            player.playSound(e.getFrom(), Sound.NOTE_BASS, 10, 1);
            if(MiscUtils.getCurrentTick() % 20 == 0){
                player.sendMessage(ChatColor.RED + "You can't go there while fighting!");
            }
        }
    }

    public static void resetTraveller(Player player){
        TravellerEvents.travelled.remove(player.getUniqueId());
    }

    private void sendAchieved(String message, Player player){
        if(notifications(player.getUniqueId())){
            ActionBarUtils.sendActionBar(player, message, 50);
            player.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 10, 1);
        }
    }

    public static boolean notifications(UUID uuid){
        return blockNotifs.contains(uuid);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e){
        resetTraveller(e.getPlayer());
    }
}
