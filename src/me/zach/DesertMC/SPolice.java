package me.zach.DesertMC;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTList;
import me.zach.DesertMC.GameMechanics.Events;
import net.jitse.npclib.NPCLib;
import net.jitse.npclib.api.NPC;
import net.jitse.npclib.api.events.NPCInteractEvent;
import net.jitse.npclib.api.skin.MineSkinFetcher;
import net.jitse.npclib.api.skin.Skin;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static me.zach.DesertMC.DesertMain.weightQueue;


public class SPolice implements Listener {
    private static ItemStack trueItem = new ItemStack(Material.STAINED_CLAY, 1, (short) 5);
    private static ItemMeta trueMeta = trueItem.getItemMeta();
    private static ItemStack falseItem = new ItemStack(Material.STAINED_GLASS, 1, (short) 14);
    private static ItemMeta falseMeta = falseItem.getItemMeta();
    static{
        trueMeta.setDisplayName(ChatColor.GREEN + "Click to retrieve");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN + "Cough up 200 gems, and I'll give");
        lore.add(ChatColor.GREEN + "your item back no problem. (breaks streak!)");
        trueMeta.setLore(lore);
        trueItem.setItemMeta(trueMeta);

        falseMeta.setDisplayName(ChatColor.RED + "Insert Token");
        ArrayList<String> flore = new ArrayList<>();
        flore.add(ChatColor.RED + "You're gonna need a seized item");
        flore.add(ChatColor.RED + "token for me to give it back.");
        falseMeta.setLore(flore);
        falseItem.setItemMeta(falseMeta);
    }
    public void onHit(EntityDamageByEntityEvent e){
        Player p = (Player) e.getDamager();
        try{
            double weight = new NBTItem(p.getItemInHand()).getCompound("CustomAttributes").getDouble("WEIGHT");
            weight += new NBTItem(p.getItemInHand()).getCompound("CustomAttributes").getDouble("WEIGHT_ADD");
                ArrayList<Object> itemsandhits;
                if(weightQueue.containsKey(p.getUniqueId())) {
                    itemsandhits = weightQueue.get(p.getUniqueId());
                }else{
                    itemsandhits = new ArrayList<>();
                }
                if(itemsandhits.contains(new NBTItem(p.getItemInHand()).getCompound("CustomAttributes").getString("UUID"))){
                    itemsandhits.set(itemsandhits.indexOf(new NBTItem(p.getItemInHand()).getCompound("CustomAttributes").getString("UUID")) + 1, (int) itemsandhits.get(itemsandhits.indexOf(new NBTItem(p.getItemInHand()).getCompound("CustomAttributes").getString("UUID")) + 1) + weight);
                    weightQueue.put(p.getUniqueId(), itemsandhits);
                }else {
                    boolean foundEmpty = false;
                    for (int i = 0; !foundEmpty; i++) {
                        if (itemsandhits.get(i) == null) {
                            foundEmpty = true;
                            itemsandhits.set(i, new NBTItem(p.getItemInHand()).getCompound("CustomAttributes").getString("UUID"));
                            itemsandhits.set(i + 1, weight);
                            weightQueue.put(p.getUniqueId(), itemsandhits);
                        }

                    }
                }



        }catch(NullPointerException ignored){
            return;
        }

    }




    public static boolean roll(ItemStack weapon){ //TODO current task finishing this method
        NBTItem weaponNBT = new NBTItem(weapon);
        NBTCompound weaponCompound = weaponNBT.getCompound("CustomAttributes");
        if(weaponCompound.getDouble("WEIGHT") == 0) return false;
        double weight = weaponCompound.getDouble("WEIGHT");
        int digits = Double.toString(weight).substring((Double.toString(weight) + "").indexOf(".")).length();
        StringBuilder toMultiply = new StringBuilder("1");
        for(int i = 0; i<=digits; i++) {
            toMultiply.append("0");
        }
        int weightInteger = (int) weight * Integer.parseInt(toMultiply.toString());
        int random = new Random().nextInt((((100 * Integer.parseInt(toMultiply.toString()))) - weightInteger) + weightInteger + 1);
        if(random == 100 * Integer.parseInt(toMultiply.toString())) return true;
        else return false;
    }
    public static void createPolice(Location loc){
        NPCLib library = DesertMain.getNPCLib();
        ArrayList<String> text = new ArrayList<>();

        text.add(ChatColor.AQUA + "Streak Police");
        text.add(ChatColor.GRAY + "Click me to retrieve your seized items");
        NPC npc = library.createNPC(text);
        npc.setLocation(loc);
        MineSkinFetcher.fetchSkinFromIdAsync(240562954, new MineSkinFetcher.Callback() {
            @Override
            public void call(Skin skin) {
                npc.setSkin(skin);
                npc.create();
                for(Player player : Bukkit.getServer().getOnlinePlayers()){
                    npc.show(player);
                }
            }
            @Override
            public void failed(){
                Bukkit.getConsoleSender().sendMessage("Skin fetch failed!");

            }
        });
    }
    private static ArrayList<UUID> cantClick = new ArrayList<>();
    @EventHandler
    public void closeOnInv(InventoryCloseEvent e){
        if(e.getInventory().getName().equals("Recover Seized Items")){
            try {
                e.getPlayer().getInventory().addItem(e.getInventory().getItem(4));
            }catch(NullPointerException ignored){}
            e.getInventory().setItem(4, new ItemStack(Material.AIR));
        }
    }
    @EventHandler
    public void pickupOnInv(PlayerPickupItemEvent e){
        try {
            if (e.getPlayer().getOpenInventory().getTopInventory().getName().equals("Recover Seized Items")) {
                e.setCancelled(true);
            }
        }catch(NullPointerException ignored){ }
    }
    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        if(e.getWhoClicked() instanceof Player){
            Player p = (Player) e.getWhoClicked();
            if(p.getOpenInventory().getTopInventory().getName().equals("Recover Seized Items")){
                e.setCancelled(true);
                try{
                    if(new NBTItem(e.getCurrentItem()).getCompound("CustomAttributes").getString("ID").equals("TOKEN")){
                        if(e.getClickedInventory().getName().equals("Recover Seized Items")){
                            p.getInventory().addItem(e.getCurrentItem());
                            e.getClickedInventory().setItem(4, new ItemStack(Material.AIR));
                            e.getClickedInventory().setItem(24, falseItem);
                        }else{
                            try{
                                if(!p.getOpenInventory().getTopInventory().getItem(4).getType().equals(Material.AIR)) p.getInventory().addItem(p.getOpenInventory().getTopInventory().getItem(4));
                            }catch(NullPointerException ignored){}
                            p.getOpenInventory().getTopInventory().setItem(4, e.getCurrentItem());
                            p.getInventory().setItem(e.getSlot(), new ItemStack(Material.AIR));
                            p.getOpenInventory().getTopInventory().setItem(24, trueItem);
                        }
                    }
                }catch(NullPointerException ignored){}
                if(e.getCurrentItem().isSimilar(falseItem)){
                    p.playSound(p.getLocation(), Sound.ANVIL_LAND, 10, 1);
                }else if(e.getCurrentItem().isSimilar(trueItem)){
                    Plugin pl = Bukkit.getPluginManager().getPlugin("Fallen");
                    if(pl.getConfig().getInt("players." + p.getUniqueId() + ".gems") >= 200){
                        if(p.getInventory().firstEmpty() == -1){
                            p.getInventory().addItem(e.getClickedInventory().getItem(4));
                            e.getClickedInventory().setItem(4, new ItemStack(Material.AIR));
                            p.closeInventory();
                            p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 10, 1);
                            p.sendMessage(ChatColor.RED + "Full Inventory!");
                            return;
                        }

                        ItemStack token = e.getClickedInventory().getItem(4);
                        token.setType(Material.valueOf(new NBTItem(token).getCompound("CustomAttributes").getString("PREV_MATERIAL")));
                        ItemMeta tokenMeta = token.getItemMeta();
                        tokenMeta.setDisplayName(tokenMeta.getDisplayName().replaceAll(ChatColor.RED + "Seized ", ""));
                        tokenMeta.setLore((List<String>) new NBTItem(token).getCompound("CustomAttributes").getObject("PREV_LORE", List.class));
                        token.setItemMeta(tokenMeta);
                        NBTItem tokenNBT = new NBTItem(token);
                        tokenNBT.getCompound("CustomAttributes").setString("ID", tokenNBT.getString("PREV_ID"));
                        //wiping PREVs
                        NBTCompound tokenComp = tokenNBT.getCompound("CustomAttributes");
                        tokenComp.removeKey("PREV_MATERIAL");
                        tokenComp.removeKey("PREV_LORE");
                        tokenComp.removeKey("PREV_ID");
                        tokenComp.setDouble("WEIGHT", 0.00);
                        ItemStack cleanItem = tokenNBT.getItem();
                        pl.getConfig().set("players." + p.getUniqueId() + ".gems", pl.getConfig().getInt("players." + p.getUniqueId() + ".gems") - 200);
                        e.getClickedInventory().setItem(4, new ItemStack(Material.AIR));
                        p.closeInventory();
                        p.getInventory().setItem(p.getInventory().firstEmpty(), cleanItem);
                        Events.ks.put(p.getUniqueId(), 0);
                        p.sendMessage(Prefix.NPC + ChatColor.DARK_GRAY.toString() + " | " + ChatColor.AQUA + "Streak Police" + ChatColor.GRAY + ": " + ChatColor.WHITE + "I put the item in your first open slot. Pleasure doin' business with ya.");

                    }else{
                        p.getInventory().addItem(e.getClickedInventory().getItem(4));
                        e.getClickedInventory().setItem(4, new ItemStack(Material.AIR));
                        p.closeInventory();
                        p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 10, 1);
                        p.sendMessage(ChatColor.RED + "Not enough gems!");
                    }
                }
            }

        }

    }
    @EventHandler
    public void moveOnInv(PlayerMoveEvent ev){
        try{
            if(cantClick.contains(ev.getPlayer().getUniqueId())) ev.setCancelled(true);
        }catch(NullPointerException ignored){}
    }


    public static void onKill(EntityDamageByEntityEvent e){
        if(e.getDamager() instanceof Player && e.getEntity() instanceof Player){
            Player killer =(Player)  e.getDamager();
            Player dieer = (Player) e.getEntity();
            if(!weightQueue.containsKey(killer.getUniqueId())){
                Bukkit.getConsoleSender().sendMessage("Hmm, something went wrong. It looks like the player that just got a kill (" + killer.getName() + ", " + killer.getUniqueId() + ") wasn't registered in the item weight queue. FIX ME GABE");
            }else{
                ArrayList<Object> itemsandhits = weightQueue.get(killer.getUniqueId());
                for(int i = 0; i<itemsandhits.size(); i+=2){
                    for(int a = 0; a<killer.getInventory().getContents().length; a++){
                        try{
                            ItemStack item = killer.getInventory().getContents()[a];
                            if(new NBTItem(item).getCompound("CustomAttributes").getString("UUID").equals(itemsandhits.get(i))){
                                NBTItem nbt = new NBTItem(item);
                                NBTCompound compound = nbt.getCompound("CustomAttributes");
                                compound.setDouble("WEIGHT", compound.getDouble("WEIGHT") + (int) itemsandhits.get(i + 1));
                                killer.getInventory().setItem(a, nbt.getItem());

                                break;
                            }

                        }catch(NullPointerException ignored){}
                    }
                }
            }
        }
    }

    public static void seize(ItemStack item){
        try{

            NBTItem nbt = new NBTItem(item);
            NBTCompound compound = nbt.getCompound("CustomAttributes");
            compound.setString("PREV_ID", compound.getString("ID"));
            compound.setString("ID", "TOKEN");
            compound.setString("PREV_MATERIAL", item.getType().toString());
            item.setType(Material.DOUBLE_PLANT);
            compound.setObject("PREV_LORE", item.getItemMeta().getLore());

        }catch(NullPointerException n){
            Bukkit.getConsoleSender().sendMessage("An item that was requested to be seized did not have the proper NBT. Item: " + item.toString());
        }

    }


    @EventHandler
    public void policeClick(NPCInteractEvent event){
        try {
            if (event.getNPC().getText().get(0).equals(ChatColor.AQUA + "Streak Police") && !cantClick.contains(event.getWhoClicked().getUniqueId())){
                event.getWhoClicked().sendMessage(Prefix.NPC + ChatColor.DARK_GRAY.toString() + " | " + ChatColor.AQUA + "Streak Police" + ChatColor.GRAY + ": " + ChatColor.WHITE + "To retrieve items that I have taken, all you gotta do is give me the token and " + ChatColor.GREEN + "100 Bones-" + ChatColor.WHITE + " sorry, " + ChatColor.GREEN + "100 Gems" + ChatColor.WHITE + ". I'm also gonna have to reset your streak.");
                event.getWhoClicked().playSound(event.getWhoClicked().getLocation(), Sound.WOLF_BARK, 10, 1);
                cantClick.add(event.getWhoClicked().getUniqueId());
                Inventory inv = Bukkit.getPluginManager().getPlugin("Fallen").getServer().createInventory(null, 27, "Recover Seized Items");
                ItemStack pane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 9);
                ItemMeta paneMeta = pane.getItemMeta();
                paneMeta.setDisplayName(" ");
                pane.setItemMeta(paneMeta);
                for(int i = 0; i<27; i++){
                    inv.setItem(i, pane);
                }
                inv.setItem(4, new ItemStack(Material.AIR));

                inv.setItem(22, falseItem);

                new BukkitRunnable(){
                    public void run(){
                        event.getWhoClicked().openInventory(inv);
                        cantClick.remove(event.getWhoClicked().getUniqueId());
                    }
                }.runTaskLater(Bukkit.getPluginManager().getPlugin("Fallen"), 80);
            }
        }catch(NullPointerException ignored){}
    }

}
