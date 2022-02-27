package me.zach.DesertMC.ClassManager;

import me.zach.DesertMC.GameMechanics.hitbox.HitboxListener;
import me.zach.DesertMC.GameMechanics.hitbox.HitboxManager;
import me.zach.DesertMC.Prefix;
import me.zach.DesertMC.Utils.ActionBar.ActionBarUtils;
import me.zach.DesertMC.Utils.Config.ConfigUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static me.zach.DesertMC.Utils.Config.ConfigUtils.findClass;

public class TravellerEvents implements Listener {
    public static final HashMap<UUID, Set<Block>> travelled = new HashMap<>();
    private static final HashMap<UUID, Integer> scoutCounter = new HashMap<>();
    private static final HashMap<UUID, Integer> wizardCounter = new HashMap<>();
    public static final Set<UUID> blockNotifs = new HashSet<>();
    @EventHandler
    public void blockGet(PlayerMoveEvent e){
        Location to = e.getTo();
        Block block = to.clone().subtract(0, 1, 0).getBlock();
        if(block.getType().isSolid()){
            if(!HitboxListener.isInCafe(to) && !HitboxListener.isInSpawn(to)){
                UUID uuid = e.getPlayer().getUniqueId();
                if(!travelled.containsKey(uuid)){
                    if(ConfigUtils.getLevel(findClass(uuid), uuid) > 5) travelled.put(uuid, new HashSet<>());
                }else{
                    Set<Block> blockSet = travelled.get(uuid);
                    int prevSize = blockSet.size();
                    if(blockSet.add(block) && notifications(uuid)){
                        Player player = e.getPlayer();
                        String actionBar = ChatColor.YELLOW + "Unique blocks travelled this run: " + ChatColor.GREEN + blockSet.size();
                        if(prevSize % 200 == 0){
                            player.sendMessage(Prefix.SERVER + ChatColor.YELLOW.toString() + ChatColor.BOLD + " TIP: " + ChatColor.GRAY + "Toggle block notifications using /blocknotifications");
                        }
                        ActionBarUtils.sendActionBar(player, actionBar);
                    }
                    Player player = e.getPlayer();
                    if(findClass(uuid).equalsIgnoreCase("scout")){
                        if(!scoutCounter.containsKey(uuid)) scoutCounter.put(uuid, 1);
                        else if(scoutCounter.get(uuid) >= 100){
                            player.setWalkSpeed(player.getWalkSpeed() + 0.004f);
                            sendAchieved(ChatColor.AQUA.toString() + ChatColor.BOLD + "+2%" + ChatColor.AQUA + " move speed!", player); //TODO this
                            scoutCounter.remove(uuid);
                        }else scoutCounter.put(uuid, scoutCounter.get(uuid) + 1);
                    }else if(findClass(uuid).equalsIgnoreCase("wizard")){
                        if(!wizardCounter.containsKey(uuid)) wizardCounter.put(uuid, 1);
                        else if(wizardCounter.get(uuid) >= 250){
                            player.setMaxHealth(player.getMaxHealth() + 2);
                            player.setHealth(player.getHealth() + 2);
                            sendAchieved(ChatColor.AQUA.toString() + ChatColor.BOLD + "+2" + ChatColor.RED + " max health!", player);
                            wizardCounter.remove(uuid);
                        }else wizardCounter.put(uuid, wizardCounter.get(uuid) + 1);
                    }else if(findClass(uuid).equals("tank")){
                        if(blockSet.size() % 100 == 0){
                            sendAchieved(ChatColor.AQUA.toString() + ChatColor.BOLD + "+2% " + ChatColor.DARK_GREEN + "defense!", player);
                        }
                    }else if(findClass(uuid).equals("corrupter")){
                        if(blockSet.size() % 100 == 0)
                            sendAchieved(ChatColor.AQUA + ChatColor.BOLD.toString() + "+2% " + ChatColor.DARK_RED + "damage!", player);
                    }
                }
            }
        }
    }

    public static void resetTraveller(Player player){
        if(TravellerEvents.travelled.containsKey(player.getUniqueId())){
            if(findClass(player).equalsIgnoreCase("scout")){
                Set<Block> blocks = TravellerEvents.travelled.get(player.getUniqueId());
                player.setWalkSpeed(player.getWalkSpeed() - (Math.floorDiv(blocks.size(), 100) * 0.004f));
            }else if(findClass(player).equalsIgnoreCase("wizard")){
                Set<Block> blocks = TravellerEvents.travelled.get(player.getUniqueId());
                player.setMaxHealth(player.getMaxHealth() - (Math.floorDiv(blocks.size(), 250) * 2));
            }
            TravellerEvents.travelled.remove(player.getUniqueId());
            wizardCounter.remove(player.getUniqueId());
            scoutCounter.remove(player.getUniqueId());
        }
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
