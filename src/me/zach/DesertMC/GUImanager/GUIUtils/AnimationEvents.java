package me.zach.DesertMC.GUImanager.GUIUtils;

import me.zach.DesertMC.DesertMain;
import net.minecraft.server.v1_8_R3.CommandExecute;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;



public class AnimationEvents extends CommandExecute implements Listener, CommandExecutor {
    //This project was abandoned for now...
    GUIAnimation animation;
    String name = "jsdafhkjdsahfkldjshakfljdh";
    ItemStack item1 = new ItemStack(Material.AIR);
    ItemStack item2 = new ItemStack(Material.AIR);
    boolean closed = false;
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(commandSender instanceof Player) {
            Player player = ((Player) commandSender).getPlayer();

            if (command.getName().equalsIgnoreCase("newanimation")) {
                if (player.hasPermission("admin")) {
                    if (args[0] != null && args[1] != null && args[2] != null) {
                        try {
                            closed = false;
                            int interval = Integer.parseInt(args[1]);
                            int size = Integer.parseInt(args[2]);
                            animation = new GUIAnimation(size, interval);
                            name = args[0];
                            //initiating animation creation...

                            player.sendMessage(ChatColor.GREEN + "Initiating animation creation... save frames by clicking the book, and save the animation by clicking the enchanted book. Close the inventory to cancel.");
                            player.playSound(player.getLocation(), Sound.FIRE_IGNITE, 10, 1);
                            ItemStack book = new ItemStack(Material.BOOK);
                            ItemStack enchbook = new ItemStack(Material.ENCHANTED_BOOK);
                            ItemMeta bmeta = book.getItemMeta();
                            bmeta.setDisplayName(ChatColor.YELLOW + "Save frame");
                            book.setItemMeta(bmeta);
                            ItemMeta ebmeta = enchbook.getItemMeta();
                            ebmeta.setDisplayName(ChatColor.GREEN + "Save animation");
                            enchbook.setItemMeta(ebmeta);
                            if(player.getInventory().getItem(11) != null) item1 = player.getInventory().getItem(11);
                            else item1 = new ItemStack(Material.AIR);
                            if(player.getInventory().getItem(13) != null) item2 = player.getInventory().getItem(13);
                            else item2 = new ItemStack(Material.AIR);
                            Inventory inv = DesertMain.getInstance.getServer().createInventory(null, size, args[0]);

                            player.getInventory().setItem(11, book);
                            player.getInventory().setItem(13, enchbook);


                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    player.openInventory(inv);
                                    player.getInventory().setItem(11, book);
                                    player.getInventory().setItem(13, enchbook);
                                }

                            }.runTaskLater(DesertMain.getInstance, 40);


                            return true;

                        } catch (Exception e) {
                            if (e instanceof NumberFormatException) {
                                player.sendMessage(ChatColor.RED + "Invalid usage. Usage: /newanimation <name> <tick interval> <size>");
                            } else {
                                player.sendMessage(ChatColor.RED + "An unknown error has occured. Animation creation terminated. \nError message: " + e.getMessage());
                                e.printStackTrace();
                            }
                            return false;
                        }

                    } else {
                        player.sendMessage(ChatColor.RED + "Invalid usage. Usage: /newanimation <name> <tick interval> <size>");
                        return false;
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Sorry, only admins can use this.");
                    return false;
                }
            }else{
                return false;
            }
        }else{
            return false;
        }

    }
    /* @EventHandler
    public void saveAnimation(InventoryClickEvent event){


        if(event.getClickedInventory().getItem(event.getSlot()) == null){
            Bukkit.getConsoleSender().sendMessage("player is putting item down returning");
            return;
        }

        if(event.getClickedInventory().getItem(event.getSlot()).getItemMeta() == null){
            Bukkit.getConsoleSender().sendMessage("meta was null returning");
            return;
        }



        if(closed){
            event.setCancelled(true);
            event.getClickedInventory().setItem(event.getSlot(), item2);
            return;
        }
        if(event.getClickedInventory().getItem(event.getSlot()).getItemMeta().getDisplayName() == null) {
            Bukkit.getConsoleSender().sendMessage("name was null");
            return;
        }
        if(event.getClickedInventory().getItem(event.getSlot()).getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Save animation") && animation != null){
            DesertMain.getPlugin(DesertMain.class).getAnimations().set(event.getWhoClicked().getUniqueId().toString() + "." + name, animation);
            Player p = (Player) event.getWhoClicked();
            event.getClickedInventory().setItem(11, item1);
            event.getClickedInventory().setItem(13, item2);
            p.playNote(p.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.G));
            p.sendMessage(ChatColor.GREEN + "Animation successfully saved on animations.yml, path is " + ChatColor.BLUE + event.getWhoClicked().getUniqueId().toString() + "." + name);
            DesertMain.getPlugin(DesertMain.class).saveAnimations();
            closed = true;

            p.closeInventory();

        }
    }
    // @EventHandler
    public void cancelAnimation(InventoryCloseEvent event){
        Player p = (Player) event.getPlayer();
        if(!closed && event.getInventory().getName().equals(name)) {
            p.sendMessage(ChatColor.RED + "Animation creation cancelled.");
            p.playNote(p.getLocation(), Instrument.BASS_GUITAR, Note.flat(1, Note.Tone.D));
            p.getInventory().setItem(11, item1);
            p.getInventory().setItem(13, item2);
            closed = true;
        }
    }

    // @EventHandler
    public void saveFrame(InventoryClickEvent event){
        if(event.getClickedInventory().getItem(event.getSlot()) == null){
            Bukkit.getConsoleSender().sendMessage("player is putting item down returning");
            return;
        }

        if(event.getClickedInventory().getItem(event.getSlot()).getItemMeta() == null){
            Bukkit.getConsoleSender().sendMessage("meta is null returning");
            return;
        }

        if(event.getClickedInventory().getItem(event.getSlot()).getItemMeta().getDisplayName() == null) {
            Bukkit.getConsoleSender().sendMessage("name was null");
            return;
        }

        if(event.getClickedInventory().getItem(event.getSlot()).getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Save Frame")){
            if(closed){
                event.setCancelled(true);
                event.getClickedInventory().setItem(event.getSlot(), item1);
                return;
            }
            try{
                Player p = (Player) event.getWhoClicked();
                animation.addFrame(p.getOpenInventory().getTopInventory());
                p.playNote(p.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.B));
                event.setCancelled(true);
            }catch(Exception e) {
                e.printStackTrace();
                event.getWhoClicked().sendMessage("an error has occurred.");
            }
        }
    }

     */


}
