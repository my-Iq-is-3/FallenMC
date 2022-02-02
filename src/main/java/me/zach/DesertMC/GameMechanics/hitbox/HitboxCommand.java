package me.zach.DesertMC.GameMechanics.hitbox;

import me.zach.DesertMC.GameMechanics.hitbox.hitboxes.BlobHitbox;
import me.zach.DesertMC.GameMechanics.hitbox.hitboxes.BoxHitbox;
import me.zach.DesertMC.GameMechanics.hitbox.hitboxes.CircleHitbox;
import me.zach.DesertMC.Utils.MiscUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class HitboxCommand implements CommandExecutor, Listener {
    public static HashMap<UUID, Location> hitboxAwait = new HashMap<>();
    public static HashMap<UUID, BlobHitbox> currentBlob = new HashMap<>();
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(sender instanceof Player){
            Player player = (Player) sender;
            if(command.getName().equalsIgnoreCase("hitbox")){
                if(MiscUtils.isAdmin(player)){
                    if(args.length > 0){
                        if(args[0].equalsIgnoreCase("remove")){
                            if(args.length == 2){
                                String name = args[1];
                                if(HitboxManager.remove(name) != null) player.sendMessage(ChatColor.GREEN + "Removed hitbox '" + name + "'!");
                                else player.sendMessage(ChatColor.RED + "No hitbox exists with that name!");
                                return true;
                            }else{
                                player.sendMessage(ChatColor.RED + "Usage: /hitbox remove <name>");
                                return true;
                            }
                        }else if(args[0].equalsIgnoreCase("rect")){
                            if(hitboxAwait.containsKey(player.getUniqueId())){
                                if(args.length < 2){
                                    player.sendMessage(ChatColor.RED + "To close your active rectangle hitbox, give it a name with /hitbox rect <name>.");
                                    return true;
                                }
                                Location location = MiscUtils.floorToBlockLocation(player.getLocation());
                                Hitbox hitbox = new BoxHitbox(hitboxAwait.get(player.getUniqueId()), location.subtract(1, 1, 1));
                                checkoutHitbox(hitbox, args[1], player);
                            }else{
                                player.sendMessage(ChatColor.GREEN + "Use /hitbox rect <name> at the next location.");
                                hitboxAwait.put(player.getUniqueId(), MiscUtils.floorToBlockLocation(player.getLocation()).add(1, 0, 1));
                            }
                        }else if(args[0].equalsIgnoreCase("sphere")){
                            if(args.length == 3){
                                try{
                                    checkoutHitbox(new CircleHitbox(player.getLocation(), Integer.parseInt(args[1])), args[1], player);
                                }catch(NumberFormatException ex){
                                    player.sendMessage(ChatColor.RED + "Invalid usage! Usage: /hitbox sphere <radius> <name>");
                                    return true;
                                }
                            }else player.sendMessage(ChatColor.RED + "Invalid usage! Usage: /hitbox sphere <radius> <name>");
                        }else if(args[0].equalsIgnoreCase("blob")){
                            if(args.length > 1){
                                UUID uuid = player.getUniqueId();
                                if(args[1].equalsIgnoreCase("start")){
                                    if(args.length > 2){
                                        String name = args[2];
                                        Hitbox previous = HitboxManager.get(name);
                                        if(previous instanceof BlobHitbox){
                                            currentBlob.put(uuid, (BlobHitbox) previous);
                                            player.sendMessage(ChatColor.GREEN + "Adding to blob '" + name + "'.");
                                        }else{
                                            BlobHitbox blob = new BlobHitbox();
                                            currentBlob.put(uuid, blob);
                                            HitboxManager.set(name, blob);
                                            player.sendMessage(ChatColor.GREEN + "Creating blob '" + name + "'.");
                                        }
                                    }else player.sendMessage(ChatColor.RED + "Usage: /hitbox blob start <name>");
                                }else if(args[1].equals("end")){
                                    if(currentBlob.remove(uuid) != null)
                                        player.sendMessage(ChatColor.GREEN + "Ended your current blob!");
                                    else player.sendMessage(ChatColor.RED + "You didn't have a blob already active.");
                                }else if(args[1].equals("undo")){
                                    if(currentBlob.containsKey(uuid)){
                                        List<Hitbox> blobList = currentBlob.get(uuid).hitboxes;
                                        int size = blobList.size();
                                        if(size > 0){
                                            Hitbox removed = blobList.remove(size - 1);
                                            player.sendMessage(ChatColor.GREEN + "Removed a " + removed.getClass().getSimpleName());
                                        }else player.sendMessage(ChatColor.RED + "Your current blob has no hitboxes!");
                                    }else player.sendMessage(ChatColor.RED + "You have no hitbox blob currently active.");
                                }else if(args[1].equalsIgnoreCase("clear")){
                                    if(currentBlob.containsKey(uuid)){
                                        List<Hitbox> blobList = currentBlob.get(uuid).hitboxes;
                                        blobList.clear();
                                        player.sendMessage(ChatColor.GREEN + "Cleared contents of your current blob");
                                    }else player.sendMessage(ChatColor.RED + "You have no hitbox blob currently active.");
                                }else player.sendMessage(ChatColor.RED + "Usage: /hitbox blob <start|end|undo|clear> <start;name>");
                            }else player.sendMessage(ChatColor.RED + "Usage: /hitbox blob <start|end|undo|clear> <start;name>");
                        }
                    }else return false;
                }else player.sendMessage(ChatColor.RED + "Only admins can use this command!");
            }
        }else sender.sendMessage("You must be a player to run this command.");
        return true;
    }

    private void checkoutHitbox(Hitbox hitbox, String name, Player player){
        UUID uuid = player.getUniqueId();
        if(currentBlob.containsKey(uuid)){
            currentBlob.get(uuid).hitboxes.add(hitbox);
            player.sendMessage(ChatColor.GREEN + "Hitbox added to current blob. End with /hitbox blob end");
        }else{
            HitboxManager.set(name, hitbox);
            player.sendMessage(ChatColor.GREEN + "Hitbox created!");
        }
        hitboxAwait.remove(player.getUniqueId());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        UUID uuid = event.getPlayer().getUniqueId();
        hitboxAwait.remove(uuid);
        currentBlob.remove(uuid);
    }
}
