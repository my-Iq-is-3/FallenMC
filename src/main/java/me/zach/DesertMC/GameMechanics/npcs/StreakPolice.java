package me.zach.DesertMC.GameMechanics.npcs;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
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
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
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
    private static final ItemStack trueItem = new ItemStack(Material.STAINED_CLAY, 1, (short) 5);
    static{
        ItemMeta trueMeta = trueItem.getItemMeta();
        ItemMeta falseMeta = falseItem.getItemMeta();
        trueMeta.setDisplayName(ChatColor.GREEN + "Click to retrieve");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN + "Cough up 200 gems, and I'll give");
        lore.add(ChatColor.GREEN + "your item back no problem.");
        lore.add(ChatColor.RED + "Breaks streak!");
        trueMeta.setLore(lore);
        trueItem.setItemMeta(trueMeta);
        falseMeta.setDisplayName(ChatColor.RED + "Insert Token");
        ArrayList<String> flore = new ArrayList<>();
        flore.add(ChatColor.RED + "You're gonna need a seized item");
        flore.add(ChatColor.RED + "token for me to give it back.");
        falseMeta.setLore(flore);
        falseItem.setItemMeta(falseMeta);
    }

    public StreakPolice(){
        super(ChatColor.AQUA + "Streak Police", SKIN_ID,
                "To retrieve items that I've taken, all you gotta do is give me the token and " + ChatColor.GREEN + "100 Bones-" + ChatColor.WHITE + " sorry, " + ChatColor.GREEN + "100 Gems" + ChatColor.WHITE + ". I'm also gonna have to reset your streak.",
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
            for(String targetId : keyList){
                for(int a = 0; a < player.getInventory().getContents().length; a++){
                    ItemStack item = player.getInventory().getContents()[a];
                    if(NBTUtil.getCustomAttrString(item, "UUID").equals(targetId)){
                        NBTItem nbt = new NBTItem(item);
                        Double weight = NBTUtil.getCustomAttr(nbt, "WEIGHT", double.class, null);
                        if(weight == null) continue;
                        weight += itemsandhits.get(targetId);
                        nbt.getCompound("CustomAttributes").setDouble("WEIGHT", weight);
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
        ItemStack newItem = MiscUtils.generateItem(Material.DOUBLE_PLANT, ChatColor.RED + "Seized " + item.getItemMeta().getDisplayName(), StringUtil.wrapLore(ChatColor.GRAY + "This item was " + ChatColor.RED + "SEIZED" + ChatColor.GRAY + "!\nTake it to the Streak Police in the Cafe to get it back!"), (byte) -1, 1, "TOKEN");
        NBTItem newNBT = new NBTItem(newItem);
        NBTCompound customAttr = NBTUtil.checkCustomAttr(newNBT);
        customAttr.setItemStack("PREV_ITEM", item);
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

    public Inventory getStartInventory(NPCInteractEvent event){
        return new StreakPoliceInventory().getInventory();
    }

    private static class StreakPoliceInventory implements GUIHolder {
        Inventory inventory = Bukkit.getServer().createInventory(this, 27, "Recover Seized Items");
        public StreakPoliceInventory(){
            ItemStack pane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 9);
            ItemMeta paneMeta = pane.getItemMeta();
            paneMeta.setDisplayName(" ");
            pane.setItemMeta(paneMeta);
            for(int i = 0; i<27; i++){
                inventory.setItem(i, pane);
            }
            inventory.clear(4);
            inventory.setItem(22, falseItem);
        }

        public Inventory getInventory(){
            return inventory;
        }

        public void inventoryClick(Player player, int slot, ItemStack clickedItem, ClickType clickType, InventoryClickEvent event){
            event.setCancelled(true);
            if(NBTUtil.getCustomAttrString(clickedItem, "ID").equals("TOKEN")){
                player.getInventory().addItem(clickedItem);
                inventory.clear(4);
                inventory.setItem(22, falseItem);
            }

            if(clickType.equals(ClickType.LEFT) || clickType.equals(ClickType.RIGHT)) {
                if (clickedItem.isSimilar(falseItem)) {
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 10, 1);
                } else if (clickedItem.isSimilar(trueItem)) {
                    int gems = ConfigUtils.getGems(player);
                    if(ConfigUtils.deductGems(player, 200)) {
                        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Player " + player.getName() + " recovered a seized item with " + gems + " gems.");
                        if (player.getInventory().firstEmpty() == -1) {
                            player.getInventory().addItem(inventory.getItem(4));
                            inventory.clear(4);
                            player.closeInventory();
                            player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 10, 1);
                            player.sendMessage(ChatColor.RED + "Full Inventory!");
                            return;
                        }
                        ItemStack token = inventory.getItem(4);
                        if(!NBTUtil.getCustomAttrString(token, "ID").equals("TOKEN")){
                            inventory.clear(4);
                            player.getInventory().addItem(token);
                            return;
                        }
                        ItemStack cleanItem = retrieveItem(token);
                        inventory.clear(4);
                        player.closeInventory();
                        player.getInventory().addItem(cleanItem);
                        Events.ks.put(player.getUniqueId(), 0);
                        player.sendMessage(Prefix.NPC + ChatColor.DARK_GRAY.toString() + " | " + ChatColor.AQUA + "Streak Police" + ChatColor.GRAY + ": " + ChatColor.WHITE + "I put the item in your first open slot. Pleasure doin' business with ya.");
                    }else{
                        player.getInventory().addItem(inventory.getItem(4));
                        inventory.clear(4);
                        player.closeInventory();
                        player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 10, 1);
                        player.sendMessage(ChatColor.RED + "Not enough gems!");
                    }
                }
            }
        }

        @Override
        public void bottomInventoryClick(Player player, Inventory bottomInv, int slot, ItemStack clickedItem, ClickType clickType, InventoryClickEvent event){
            event.setCancelled(true);
            if(NBTUtil.getCustomAttrString(clickedItem, "ID").equals("TOKEN")){
                ItemStack token = inventory.getItem(4);
                if(NBTUtil.getCustomAttrString(token, "ID").equals("TOKEN")){
                    if(token != null)
                        bottomInv.addItem(token);
                }
                bottomInv.clear(slot);
                inventory.setItem(4, clickedItem);
                inventory.setItem(22, trueItem);
            }
        }

        public void inventoryClose(Player player, Inventory inventory, InventoryCloseEvent event){
            Inventory playerInv = player.getInventory();
            ItemStack token = inventory.getItem(4);
            if(token != null){
                playerInv.addItem(token);
                inventory.clear(4);
            }
        }
    }
}
