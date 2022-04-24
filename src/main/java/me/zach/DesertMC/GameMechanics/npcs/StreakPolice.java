package me.zach.DesertMC.GameMechanics.npcs;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import itempackage.Items;
import me.zach.DesertMC.CommandsPackage.NPCCommand;
import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.GameMechanics.Events;
import me.zach.DesertMC.GameMechanics.NPCStructure.NPCSuper;
import me.zach.DesertMC.Prefix;
import me.zach.DesertMC.Utils.Config.ConfigUtils;
import me.zach.DesertMC.Utils.MiscUtils;
import me.zach.DesertMC.Utils.StringUtils.StringUtil;
import me.zach.DesertMC.Utils.gui.GUIHolder;
import me.zach.DesertMC.Utils.nbt.NBTUtil;
import net.jitse.npclib.api.events.NPCInteractEvent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static me.zach.DesertMC.DesertMain.weightQueue;


public class StreakPolice extends NPCSuper {
    public static final int SKIN_ID = 240562954;
    private static final ItemStack falseItem = new ItemStack(Material.STAINED_GLASS, 1, (short) 14);

    static{
        ItemMeta falseMeta = falseItem.getItemMeta();
        falseMeta.setDisplayName(ChatColor.RED + "Insert Token");
        ArrayList<String> flore = new ArrayList<>();
        flore.add(ChatColor.RED + "You're gonna need a seized item");
        flore.add(ChatColor.RED + "token for me to give it back.");
        falseMeta.setLore(flore);
        falseItem.setItemMeta(falseMeta);
    }

    public StreakPolice(){
        super(ChatColor.AQUA + "Streak Police", SKIN_ID,
                "Hey, buddy. If you've come to pay off a seized item's streak cost, you've got the right guy. Not that it would be easy to get the wrong guy. Honestly, all this magical floating text above people's heads is making things way too easy.",
                Sound.WOLF_BARK,
                ChatColor.GRAY + "Click me to recover your seized items");
    }

    public static void onHit(EntityDamageByEntityEvent e){
        Player p = (Player) e.getDamager();
        UUID uuid = p.getUniqueId();
        ItemStack item = p.getItemInHand();
        try{
            if(!NBTUtil.hasCustomKey(item, "WEIGHT_ADD")) return;
            double weight = NBTUtil.getCustomAttr(item, "WEIGHT_ADD", double.class);
            boolean madeNew = false;
            HashMap<String, Double> itemsandhits;
            if(weightQueue.containsKey(uuid)) {
                itemsandhits = weightQueue.get(uuid);
            }else{
                itemsandhits = new HashMap<>();
                madeNew = true;
            }
            String itemId = NBTUtil.getCustomAttrString(item, "UUID");
            if(itemId.equals("null")) return;
            if(itemsandhits.containsKey(itemId)){
                itemsandhits.put(itemId, itemsandhits.get(itemId) + weight);
            }else{
                itemsandhits.put(itemId, weight);
                if(madeNew) weightQueue.put(uuid, itemsandhits);
            }
        }catch(NullPointerException ignored){}
    }

    public static boolean roll(ItemStack weapon){
        double weight = NBTUtil.getCustomAttr(weapon, "WEIGHT", double.class);
        if(weight == 0) return false;
        else return roll(weight);
    }

    public static boolean roll(double weight){
        ThreadLocalRandom random = ThreadLocalRandom.current();
        double roll = random.nextDouble(0, 100);
        return roll <= weight;
    }

    public static void onKill(Player player){
        HashMap<String, Double> itemsandhits = weightQueue.get(player.getUniqueId());
        if(itemsandhits != null && !itemsandhits.isEmpty()){
            ArrayList<String> toRemove = new ArrayList<>();
            Set<String> keyList = itemsandhits.keySet();
            ItemStack[] contents = player.getInventory().getContents();
            for(String targetId : keyList){
                for(int a = 0; a < contents.length; a++){
                    ItemStack item = contents[a];
                    if(NBTUtil.getCustomAttrString(item, "UUID").equals(targetId)){
                        NBTItem nbt = new NBTItem(item);
                        Double weight = NBTUtil.getCustomAttr(nbt, "WEIGHT", Double.class, null);
                        if(weight == null) continue;
                        weight += itemsandhits.get(targetId);
                        nbt.getCompound("CustomAttributes").setDouble("WEIGHT", Math.min(100, weight));
                        toRemove.add(targetId);
                        player.getInventory().setItem(a, nbt.getItem());
                        if(roll(weight)){
                            player.getInventory().setItem(a, seize(item));
                            player.playSound(player.getLocation(), Sound.PISTON_EXTEND, 10, 1);
                            player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 10, 1);
                            StringUtil.sendCenteredMessage(player,"", ChatColor.RED + ChatColor.BOLD.toString() + "YOUR ITEM HAS BEEN SEIZED!", ChatColor.RED + "Talk to the Streak Police in the Cafe to get it back!", "");
                            Bukkit.getLogger().info(player.getName() + "'s " + item.getItemMeta().getDisplayName() + " seized with weight " + weight);
                        }
                        break;
                    }
                }
            }
            for(String uuid : toRemove){
                itemsandhits.remove(uuid);
            }
        }
    }

    public static ItemStack seize(ItemStack item){
        int streakPrice = calculateStreakPrice(NBTUtil.getCustomAttr(item, "WEIGHT", double.class, 10.0));
        ItemStack newItem = MiscUtils.generateItem(Material.DOUBLE_PLANT, ChatColor.RED + "Seized " + ChatColor.stripColor(item.getItemMeta().getDisplayName()), getTokenLore(streakPrice), (byte) -1, 1, "TOKEN");
        NBTItem newNBT = new NBTItem(newItem);
        NBTCompound customAttr = NBTUtil.checkCustomAttr(newNBT);
        customAttr.setItemStack("PREV_ITEM", item);
        customAttr.setInteger("KILLSTREAK_REMAINING", streakPrice);
        return newNBT.getItem();
    }

    public static ItemStack retrieveItem(ItemStack token){
        NBTItem tokenNBT = new NBTItem(token);
        NBTCompound customAttr = Objects.requireNonNull(tokenNBT.getCompound("CustomAttributes"), "CustomAttributes when retrieving seized item cannot be null");
        NBTItem newNBT = new NBTItem(customAttr.getItemStack("PREV_ITEM"));
        NBTCompound newCustomAttr = newNBT.getCompound("CustomAttributes");
        if(newCustomAttr.hasKey("WEIGHT")) newCustomAttr.setDouble("WEIGHT", 0.0);
        return newNBT.getItem();
    }

    public static List<String> getTokenLore(int killstreakRemaining){
        return StringUtil.wrapLore(ChatColor.GRAY + "This item has been " + ChatColor.RED + "SEIZED" + ChatColor.GRAY + "!\n" + "Talk to the streak police at the Cafe to get it back!\nKillstreak remaining: " + ChatColor.RED + killstreakRemaining, 35);
    }

    private static int calculateStreakPrice(double weight){
        return (int) Math.round(Math.min(weight * (1 - weight/20 * (0.145 - weight/20 * 0.01)), 50) + 2); //https://www.desmos.com/calculator/ndrtpgmtws
    }

    public Inventory getStartInventory(NPCInteractEvent event){
        return new StreakPoliceInventory().getInventory();
    }

    private class StreakPoliceInventory implements GUIHolder {
        int BUTTON_SLOT = 22;
        int TOKEN_SLOT = 4;
        Inventory inventory = Bukkit.getServer().createInventory(this, 27, "Recover Seized Items");
        public StreakPoliceInventory(){
            ItemStack pane = MiscUtils.getEmptyPane((byte) 9);
            for(int i = 0; i<27; i++){
                inventory.setItem(i, pane);
            }
            inventory.clear(TOKEN_SLOT);
            inventory.setItem(BUTTON_SLOT, falseItem);
        }

        public Inventory getInventory(){
            return inventory;
        }

        public void inventoryClick(Player player, int slot, ItemStack clickedItem, ClickType clickType, InventoryClickEvent event){
            event.setCancelled(true);
            if(NBTUtil.getCustomAttrString(clickedItem, "ID").equals("TOKEN")){
                player.getInventory().addItem(clickedItem);
                inventory.clear(TOKEN_SLOT);
                inventory.setItem(BUTTON_SLOT, falseItem);
            }

            if(clickType.equals(ClickType.LEFT) || clickType.equals(ClickType.RIGHT)) {
                if (clickedItem.isSimilar(falseItem)) {
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 10, 1);
                } else if (slot == BUTTON_SLOT) {
                    ItemStack token = inventory.getItem(TOKEN_SLOT);
                    if(token != null){
                        UUID uuid = player.getUniqueId();
                        int ks = Events.ks.getOrDefault(uuid, 0);
                        if(ks <= 0){
                            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 10, 1.1f);
                            return;
                        }
                        int ksRemaining = NBTUtil.getCustomAttr(token, "KILLSTREAK_REMAINING", int.class);
                        if(ks >= ksRemaining){
                            Events.ks.put(uuid, ks - ksRemaining);
                            inventory.clear(TOKEN_SLOT);
                            ItemStack prevItem = NBTUtil.getCustomAttr(token, "PREV_ITEM", ItemStack.class);
                            player.getInventory().addItem(prevItem);
                            npcMessage(player, "I put the item in your first open slot. Pleasure doin' business with ya.");
                            player.closeInventory();
                            if(NBTUtil.getCustomAttr(prevItem, "WEIGHT_ADD", double.class) >= Items.STARTER_WPH){
                                Bukkit.getScheduler().runTaskLater(DesertMain.getInstance, () -> {
                                    if(player.isOnline()){
                                        player.playSound(player.getLocation(), Sound.WOLF_BARK, 10, 1);
                                        npcMessage(player, "Oh, and by the way... if it seems like your item is getting seized too often, I've heard that the " + ChatColor.LIGHT_PURPLE + "Soul Broker" + ChatColor.WHITE + " runs a little business down in the basement that could help, as shady as he may be.");
                                        Bukkit.getScheduler().runTaskLater(DesertMain.getInstance, () -> npcMessage(player, "...Don't tell anyone I told you that."), 100);
                                    }
                                }, 50);
                            }
                        }else{
                            Events.ks.put(uuid, 0);
                            inventory.clear(TOKEN_SLOT);
                            ItemMeta tokenMeta = token.getItemMeta();
                            tokenMeta.setLore(getTokenLore(ksRemaining - ks));
                            token.setItemMeta(tokenMeta);
                            NBTItem tokenNBT = new NBTItem(token);
                            tokenNBT.getCompound("CustomAttributes").setInteger("KILLSTREAK_REMAINING", ksRemaining - ks);
                            player.getInventory().addItem(tokenNBT.getItem());
                            npcMessage(player, "And just like that, you're " + ks + " steps closer to getting your item back. You go, buddy.");
                            player.closeInventory();
                        }
                    }
                }
            }
        }

        @Override
        public void bottomInventoryClick(Player player, Inventory bottomInv, int slot, ItemStack clickedItem, ClickType clickType, InventoryClickEvent event){
            event.setCancelled(true);
            if(NBTUtil.getCustomAttrString(clickedItem, "ID").equals("TOKEN")){
                ItemStack token = inventory.getItem(TOKEN_SLOT);
                if(NBTUtil.getCustomAttrString(token, "ID").equals("TOKEN")){
                    if(token != null)
                        bottomInv.addItem(token);
                }
                NBTItem clickedTokenNBT = new NBTItem(clickedItem);
                bottomInv.clear(slot);
                ItemStack item;
                int ks = Events.ks.getOrDefault(player.getUniqueId(), 0);
                if(ks == 0){
                    item = MiscUtils.generateItem(Material.STAINED_GLASS, ChatColor.RED + "No killstreak!", StringUtil.wrapLore(ChatColor.GRAY + "You don't have any killstreak to contribute towards getting your item back!\nGo get some kills then come back here!"), DyeColor.RED.getData(), 1);
                }else{
                    Integer ksRemaining = NBTUtil.getCustomAttr(clickedTokenNBT, "KILLSTREAK_REMAINING", Integer.class);
                    ItemStack prevItem = NBTUtil.getCustomAttr(clickedTokenNBT, "PREV_ITEM", ItemStack.class);
                    if(ksRemaining == null){
                        ksRemaining = calculateStreakPrice(NBTUtil.getCustomAttr(prevItem, "WEIGHT", double.class));
                        clickedTokenNBT.getCompound("CustomAttributes").setInteger("KILLSTREAK_REMAINING", ksRemaining);
                        clickedItem = clickedTokenNBT.getItem();
                    }
                    List<String> desc = new ArrayList<>();
                    desc.add(ChatColor.GRAY + "Killstreak left to pay: " + ChatColor.RED + ksRemaining);
                    desc.add("§7Your current killstreak: " + ChatColor.RED + ks);
                    desc.add(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "--------------------------");
                    String displayName;
                    if(ksRemaining <= ks){
                        desc.add(ChatColor.GRAY + "Killstreak left after payment: " + ChatColor.GREEN + "0");
                        desc.add("");
                        desc.add(ChatColor.GREEN + "Click to retrieve " + ChatColor.stripColor(prevItem.getItemMeta().getDisplayName()) + "!");
                        displayName = ChatColor.GREEN + "Final Payment";
                    }else{
                        desc.add(ChatColor.GRAY + "Killstreak left after payment: " + ChatColor.RED + (ksRemaining - ks));
                        desc.add("");
                        desc.add(ChatColor.GREEN + "Click to pay!");
                        displayName = ChatColor.YELLOW + "Item Retrieval Payment";
                    }
                    item = MiscUtils.generateItem(Material.STAINED_CLAY, displayName, desc, DyeColor.GREEN.getData(), 1);
                }
                inventory.setItem(TOKEN_SLOT, clickedItem);
                inventory.setItem(BUTTON_SLOT, item);
            }
        }

        public void inventoryClose(Player player, Inventory inventory, InventoryCloseEvent event){
            Inventory playerInv = player.getInventory();
            ItemStack token = inventory.getItem(TOKEN_SLOT);
            if(token != null){
                playerInv.addItem(token);
                inventory.clear(TOKEN_SLOT);
            }
        }
    }
}
