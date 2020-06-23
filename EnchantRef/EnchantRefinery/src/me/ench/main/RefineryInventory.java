package me.ench.main;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class RefineryInventory implements Listener {
    private int ableToRefine = 0;
    boolean hasHammer = false;
    boolean hasBook = false;
    private ItemStack hammer;
    private ItemStack book;
    ItemStack buttonItem;
    ItemMeta buttonMeta;
    ItemStack borderItem;
    ItemMeta bordermeta;
    NBTItem hammerNBT;
    NBTCompound hammerCompound;
    NBTItem bookNBT;
    NBTCompound bookCompound;
    ItemStack slot1 = new ItemStack(Material.AIR);
    ItemStack slot2 = new ItemStack(Material.AIR);
    Plugin plugin = Bukkit.getPluginManager().getPlugin("Enchant Refinery");
    @EventHandler
    public void invClick(InventoryClickEvent event) {
        Inventory i = event.getClickedInventory();
        ItemStack item = event.getCurrentItem();
        if (i.getTitle().equalsIgnoreCase("Enchant Refinery")) {

            if(event.getSlot() == 12 || event.getSlot() == 14) {
                event.getWhoClicked().sendMessage("12 or 14 -- check");
                 if(null != i.getItem(event.getSlot())) {
                     event.getWhoClicked().sendMessage("player is taking item out, returning");
                     return;
                 }else {
                     event.getWhoClicked().sendMessage("Current item was null! executing ifs");
                     if (event.getSlot() == 12) {
                         event.getWhoClicked().sendMessage("Slot was 12, setting temphammeritem");
                         NBTItem test1 = new NBTItem(event.getCursor());
                         NBTCompound test2 = test1.getCompound("CustomAttributes");
                         Integer test3 = test2.getInteger("MAX_LEVELS_TO_UPGRADE");
                         event.getWhoClicked().sendMessage(event.getCursor().toString() + new NBTItem(event.getCursor()).toString() + "||" + new NBTItem(event.getCursor()).getCompound("CustomAttributes") + "||" + new NBTItem(event.getCursor()).getCompound("CustomAttributes").getInteger("DOWNGRADE_CHANCE"));
                         ItemStack temphammeritem = event.getCursor();

                         if (null == new NBTItem(temphammeritem).getCompound("CustomAttributes").getInteger("DOWNGRADE_CHANCE")) {
                             event.getWhoClicked().sendMessage("Canceling event, it isn't a hammer");
                             event.setCancelled(true);
                             return;
                         } else {
                            event.getWhoClicked().sendMessage("DOWNGRADE_CHANCE wasn't null, continuing without cancel");
                             hammer = temphammeritem;
                             hammerNBT = new NBTItem(hammer);
                             hammerCompound = hammerNBT.getCompound("CustomAttributes");
                                event.getWhoClicked().sendMessage("Setting hasHammer to true");
                                 event.setCancelled(false);
                                 hasHammer = true;
                                 slot1 = event.getCursor();

                         }
                     } else if (event.getSlot() == 14) {
                         ItemStack tempbookitem = event.getCursor();
                         if (new NBTItem(tempbookitem).getCompound("CustomAttributes").getInteger("BASE_LEVEL") != null) {
                             book = tempbookitem;
                             bookNBT = new NBTItem(book);
                             bookCompound = bookNBT.getCompound("CustomAttributes");

                             if (i.getItem(14).equals(book)) {
                                 event.setCancelled(false);
                                 hasBook = true;
                                 slot2 = event.getCursor();
                             }
                         } else {
                             event.setCancelled(true);
                             return;
                         }
                     }
                 }


                if(hasBook && hasHammer) {
                    if (hammerCompound.getInteger("MAX_LEVELS_TO_UPGRADE") > (bookCompound.getInteger("BASE_LEVEL") - bookCompound.getInteger("REAL_LEVEL"))){
                        ableToRefine = 1;
                        openRefineryInventory((Player) event.getWhoClicked());

                    }else {
                        if(hammerCompound.getInteger("MAX_LEVELS_TO_UPGRADE") == 5) {
                            if (bookCompound.getCompound("Special") != null) {
                                ableToRefine = 4;
                                openRefineryInventory((Player) event.getWhoClicked());


                            }else {
                                ableToRefine = 3;
                                openRefineryInventory((Player) event.getWhoClicked());

                            }
                        }else {
                            ableToRefine = 2;
                            openRefineryInventory((Player) event.getWhoClicked());
                        }
                    }
                }else {
                    ableToRefine = 0;
                    openRefineryInventory((Player) event.getWhoClicked());
                }

            }else {

                event.setCancelled(true);
                return;
            }


        }else {

            return;
        }
    }

    public void openRefineryInventory(Player p) {
    if (ableToRefine == 0) {
         buttonItem = new ItemStack(Material.STAINED_GLASS, 1, (short) 14);
         buttonMeta = buttonItem.getItemMeta();
        buttonMeta.setDisplayName(ChatColor.RED + "Can't refine!");
        ArrayList<String> buttonLore = new ArrayList<String>();
        buttonLore.add(ChatColor.RED + "Please put your hammer to the slot on the left, and");
        buttonLore.add(ChatColor.RED + "your book that you would like to refine on the right.");
        buttonMeta.setLore(buttonLore);
        buttonItem.setItemMeta(buttonMeta);
         borderItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
         bordermeta = borderItem.getItemMeta();
        bordermeta.setDisplayName(" ");
        borderItem.setItemMeta(bordermeta);
    }
    if (ableToRefine == 1) {
        hammerNBT = new NBTItem(hammer);
        hammerCompound = hammerNBT.getCompound("CustomAttributes");
        Integer downchance = hammerCompound.getInteger("DOWNGRADE_CHANCE");
        Integer upchance = 100 - downchance;
        bookNBT = new NBTItem(book);
        bookCompound = bookNBT.getCompound("CustomAttributes");
        Integer baselevel = bookCompound.getInteger("BASE_LEVEL");
        Integer maxlevel = hammerCompound.getInteger("MAX_LEVELS_TO_UPGRADE") + baselevel;
        buttonItem = new ItemStack(Material.STAINED_GLASS, 1,(short)  5);
        buttonMeta = buttonItem.getItemMeta();
        buttonMeta.setDisplayName(ChatColor.GREEN + "Click to refine!");
        ArrayList<String> buttonLore = new ArrayList<String>();
        buttonLore.add(ChatColor.YELLOW + "Details:");
        buttonLore.add(ChatColor.YELLOW + "Chance to be downgraded: " + ChatColor.BLUE + downchance.toString() + "%");
        buttonLore.add(ChatColor.YELLOW + "Chance to be upgraded: " + ChatColor.BLUE + upchance + "%");
        buttonLore.add(ChatColor.YELLOW + "Max level to go up to: " + ChatColor.BLUE + maxlevel);
        buttonMeta.setLore(buttonLore);
        buttonItem.setItemMeta(buttonMeta);

        borderItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);
        bordermeta = borderItem.getItemMeta();
        bordermeta.setDisplayName(" ");
        borderItem.setItemMeta(bordermeta);
    }
    if (ableToRefine == 2) {
        buttonItem = new ItemStack(Material.STAINED_GLASS, 1, (short) 14);
        buttonMeta = buttonItem.getItemMeta();
        buttonMeta.setDisplayName(ChatColor.RED + "Use a better hammer!");
        ArrayList<String> buttonLore = new ArrayList<String>();
        buttonLore.add(ChatColor.RED + "It appears that your book has reached the max level for your hammer.");
        buttonLore.add(ChatColor.RED + "Please try again with a better hammer.");
        buttonMeta.setLore(buttonLore);
        buttonItem.setItemMeta(buttonMeta);

        borderItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
        bordermeta = borderItem.getItemMeta();
        bordermeta.setDisplayName(" ");
        borderItem.setItemMeta(bordermeta);
    }
    if(ableToRefine == 3) {
        buttonItem = new ItemStack(Material.STAINED_GLASS, 1, (short) 5);
        buttonMeta = buttonItem.getItemMeta();
        buttonMeta.setDisplayName(ChatColor.YELLOW + "Maxed!");
        ArrayList<String> buttonLore = new ArrayList<String>();
        buttonLore.add(ChatColor.YELLOW + "This book is the max level (8)! If you would like to try to");
        buttonLore.add(ChatColor.YELLOW + "get a special enchant, please use a special hammer.");
        buttonMeta.setLore(buttonLore);

        borderItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 4);
        bordermeta = borderItem.getItemMeta();
        bordermeta.setDisplayName(" ");
        borderItem.setItemMeta(bordermeta);
    }
    if(ableToRefine == 4) {
        buttonItem = new ItemStack(Material.STAINED_GLASS, 1, (short) 5);
        buttonMeta = buttonItem.getItemMeta();
        buttonMeta.setDisplayName(ChatColor.YELLOW + "Why are you here?!");
        ArrayList<String> buttonLore = new ArrayList<String>();
        buttonLore.add(ChatColor.YELLOW + "This book is already maxed, WITH a special enchant!");
        buttonLore.add(ChatColor.YELLOW + "If you really want to use this, go get a different book!");
        buttonMeta.setLore(buttonLore);

        borderItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 4);
        bordermeta = borderItem.getItemMeta();
        bordermeta.setDisplayName(" ");
        borderItem.setItemMeta(bordermeta);
    }



        Inventory i = plugin.getServer().createInventory(p, 36, "Enchant Refinery");
        for (int inc = 0; inc<36; inc++) {
            ItemStack empty = new ItemStack( Material.STAINED_GLASS_PANE, 1, (short) 7);
            ItemMeta em = empty.getItemMeta();
            em.setDisplayName(" ");
            empty.setItemMeta(em);
            i.setItem(inc, empty);
        }
        if(slot1 != null) {
            i.setItem(12, new ItemStack(slot1));
        }else {
            i.setItem(12, new ItemStack(Material.AIR));
        }
        if (slot2 != null) {
            i.setItem(14, new ItemStack(slot2));
        }else {
            i.setItem(14, new ItemStack(Material.AIR));
        }


        i.setItem(22, buttonItem);

        for (int in = 0;in<36;in+=9){
            i.setItem(in, borderItem);
        }
        for (int in = 8; in<36;in+=9) {
            i.setItem(in, borderItem);
        }
        i.setItem(26, borderItem);
        p.openInventory(i);
    }


}
