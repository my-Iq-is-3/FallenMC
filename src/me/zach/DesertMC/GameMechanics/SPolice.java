package me.zach.DesertMC.GameMechanics;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import me.zach.DesertMC.GameMechanics.NPCStructure.NPCDataPasser;
import me.zach.DesertMC.GameMechanics.NPCStructure.NPCSuper;
import me.zach.DesertMC.Prefix;
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

import static me.zach.DesertMC.DesertMain.weightQueue;


public class SPolice extends NPCSuper implements Listener {
    public static SPolice INSTANCE = new SPolice();
    private static ItemStack falseItem = new ItemStack(Material.STAINED_GLASS, 1, (short) 14);
    private static ItemStack trueItem = new ItemStack(Material.STAINED_CLAY, 1, (short) 5);
    static{
        ItemMeta trueMeta = trueItem.getItemMeta();
        ItemMeta falseMeta = falseItem.getItemMeta();
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

    public SPolice(){
        super(ChatColor.AQUA + "Streak Police", 240562954,
                "To retrieve items that I have taken, all you gotta do is give me the token and " + ChatColor.GREEN + "100 Bones-" + ChatColor.WHITE + " sorry, " + ChatColor.GREEN + "100 Gems" + ChatColor.WHITE + ". I'm also gonna have to reset your streak.",
                Sound.WOLF_BARK, 100,
                new NPCDataPasser() {
                    @Override
                    public Inventory getStartInventory(NPCInteractEvent event) {
                        ItemStack pane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 9);
                        ItemMeta paneMeta = pane.getItemMeta();
                        paneMeta.setDisplayName(" ");
                        pane.setItemMeta(paneMeta);
                        Inventory inv = Bukkit.getPluginManager().getPlugin("Fallen").getServer().createInventory(null, 27, "Recover Seized Items");
                        for(int i = 0; i<27; i++){
                            inv.setItem(i, pane);
                        }

                        inv.setItem(4, new ItemStack(Material.AIR));
                        inv.setItem(22, falseItem);
                        return inv;
                    }
                },ChatColor.GRAY + "Click me to recover your seized items");
    }


    public static void onHit(EntityDamageByEntityEvent e){
        Player p = (Player) e.getDamager();
        try{
            double weight = new NBTItem(p.getItemInHand()).getCompound("CustomAttributes").getDouble("WEIGHT");
            weight += new NBTItem(p.getItemInHand()).getCompound("CustomAttributes").getDouble("WEIGHT_ADD");
            boolean madeNew = false;
                HashMap<UUID, Integer> itemsandhits;
                if(weightQueue.containsKey(p.getUniqueId())) {
                    itemsandhits = weightQueue.get(p.getUniqueId());
                }else{
                    itemsandhits = new HashMap<>();
                    madeNew = true;
                }
                if(itemsandhits.contains(new NBTItem(p.getItemInHand()).getCompound("CustomAttributes").getString("UUID"))){
                    itemsandhits.set(itemsandhits.indexOf(new NBTItem(p.getItemInHand()).getCompound("CustomAttributes").getString("UUID")) + 1, (int) itemsandhits.get(itemsandhits.indexOf(new NBTItem(p.getItemInHand()).getCompound("CustomAttributes").getString("UUID")) + 1) + weight);
                }else {
                    p.sendMessage("Adding weight " + weight + "to index " + (itemsandhits.size() + 1));
                    p.sendMessage("Adding item UUID " + new NBTItem(p.getItemInHand()).getCompound("CustomAttributes").getString("UUID") + " to index " + itemsandhits.size());
                    itemsandhits.add(new NBTItem(p.getItemInHand()).getCompound("CustomAttributes").getString("UUID"));
                    itemsandhits.add(weight);
                    if(madeNew) weightQueue.put(p.getUniqueId(), itemsandhits);
                }



        }catch(NullPointerException ignored){
            return;
        }

    }




    public static boolean roll(ItemStack weapon){
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
    @EventHandler
    public void closeOnInv(InventoryCloseEvent e){
        if(e.getInventory().getName().equals("Recover Seized Items")){
            try {
                e.getPlayer().getInventory().addItem(e.getInventory().getItem(4));
            }catch(IllegalArgumentException ignored){}
            e.getInventory().clear(4);
        }
    }



    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        if(e.getWhoClicked() instanceof Player){
            Player p = (Player) e.getWhoClicked();
            if(cantClick.contains(p.getUniqueId())){
                e.setCancelled(true);
                if(e.getClick().equals(ClickType.LEFT) || e.getClick().equals(ClickType.RIGHT)) {
                    try {
                        if (new NBTItem(e.getCurrentItem()).getCompound("CustomAttributes").getString("ID").equals("TOKEN")) {
                            if (e.getClickedInventory().getName().equals("Recover Seized Items")) {
                                p.getInventory().addItem(e.getCurrentItem());
                                e.getClickedInventory().setItem(4, new ItemStack(Material.AIR));
                                e.getClickedInventory().setItem(22, falseItem);
                            } else {
                                try {
                                    if (!p.getOpenInventory().getTopInventory().getItem(4).getType().equals(Material.AIR))
                                        p.getInventory().addItem(p.getOpenInventory().getTopInventory().getItem(4));
                                } catch (NullPointerException ignored) {
                                }
                                p.getOpenInventory().getTopInventory().setItem(4, e.getCurrentItem());
                                p.getInventory().setItem(e.getSlot(), new ItemStack(Material.AIR));
                                p.getOpenInventory().getTopInventory().setItem(22, trueItem);
                            }
                        }
                    } catch (NullPointerException ignored) {
                    }
                    if (e.getCurrentItem().isSimilar(falseItem)) {
                        p.playSound(p.getLocation(), Sound.ANVIL_LAND, 10, 1);
                    } else if (e.getCurrentItem().isSimilar(trueItem)) {
                        Plugin pl = Bukkit.getPluginManager().getPlugin("Fallen");
                        if (pl.getConfig().getInt("players." + p.getUniqueId() + ".balance") >= 200) {
                            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Player " + p.getName() + " recovered a seized item with " + pl.getConfig().getInt("players." + p.getUniqueId() + ".balance") + "gems.");
                            if (p.getInventory().firstEmpty() == -1) {
                                p.getInventory().addItem(e.getClickedInventory().getItem(4));
                                e.getClickedInventory().setItem(4, new ItemStack(Material.AIR));
                                p.closeInventory();
                                p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 10, 1);
                                p.sendMessage(ChatColor.RED + "Full Inventory!");
                                return;
                            }
                            ItemStack token = e.getClickedInventory().getItem(4);
                            NBTItem tokenNBT = new NBTItem(token);

                            token.setType(Material.valueOf(tokenNBT.getCompound("CustomAttributes").getString("PREV_MATERIAL")));
                            ItemMeta tokenMeta = token.getItemMeta();
                            tokenMeta.setDisplayName(tokenMeta.getDisplayName().replaceAll( "Seized ", ""));
                            List<String> lore = (List<String>) tokenNBT.getCompound("CustomAttributes").getObject("PREV_LORE", List.class);
                            String prevID = tokenNBT.getCompound("CustomAttributes").getString("PREV_ID");
                            String wString = ChatColor.GRAY + "Weight: " + ChatColor.GREEN + "0";
                            for (int i = 0; i < lore.size(); i++) {
                                String line = lore.get(i);
                                if (line.contains("Weight: ")) {
                                    lore.set(i, wString);
                                    break;
                                }
                            }
                            tokenMeta.setLore(lore);
                            token.setItemMeta(tokenMeta);
                            tokenNBT = new NBTItem(token);
                            tokenNBT.getCompound("CustomAttributes").setString("ID", prevID);
                            //wiping PREVs
                            NBTCompound tokenComp = tokenNBT.getCompound("CustomAttributes");
                            tokenComp.removeKey("PREV_MATERIAL");
                            tokenComp.removeKey("PREV_LORE");
                            tokenComp.removeKey("PREV_ID");
                            tokenComp.setDouble("WEIGHT", 0.00);
                            ItemStack cleanItem = tokenNBT.getItem();
                            pl.getConfig().set("players." + p.getUniqueId() + ".balance", pl.getConfig().getInt("players." + p.getUniqueId() + ".balance") - 200);
                            pl.saveConfig();
                            e.getClickedInventory().clear(4);
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

    }



    public static void onKill(EntityDamageByEntityEvent e){
        if(e.getDamager() instanceof Player && e.getEntity() instanceof Player){
            Player killer =(Player)  e.getDamager();
            if(!weightQueue.containsKey(killer.getUniqueId())){
                Bukkit.getConsoleSender().sendMessage("Hmm, something went wrong. It looks like the player that just got a kill (" + killer.getName() + ", " + killer.getUniqueId() + ") wasn't registered in the item weight queue. FIX ME GABE");
            }else{
                HashMap<UUID, Integer> itemsandhits = weightQueue.get(killer.getUniqueId());
                ArrayList<UUID> toRemove = new ArrayList<>();
                List<UUID> keyList = new ArrayList<>(itemsandhits.keySet());
                for(UUID targetId : keyList){
                    getItem: for(int a = 0; a<killer.getInventory().getContents().length; a++){
                        try{

                            ItemStack item = killer.getInventory().getContents()[a];
                            killer.sendMessage("Checking if item " + item.getItemMeta().getDisplayName() + " matches item uuid " + targetId);

                            if(new NBTItem(item).getCompound("CustomAttributes").getString("UUID").equals(targetId.toString())){
                                NBTItem nbt = new NBTItem(item);
                                NBTCompound compound = nbt.getCompound("CustomAttributes");
                                compound.setDouble("WEIGHT", compound.getDouble("WEIGHT") + itemsandhits.get(targetId));
                                toRemove.add(targetId);
                                killer.getInventory().setItem(a, nbt.getItem());
                                if(roll(item)) {
                                    killer.getInventory().setItem(a, seize(item));
                                    killer.playSound(killer.getLocation(), Sound.PISTON_EXTEND, 7, 1);
                                    killer.playSound(killer.getLocation(), Sound.ANVIL_LAND, 10, 1);
                                    killer.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "YOUR ITEM HAS BEEN SEIZED! " + ChatColor.RED + "Talk to the Streak Police in the Cafe to get it back. It was replaced with a token you can use to recover it. Item: " + ChatColor.YELLOW + item.getItemMeta().getDisplayName());
                                }
                                break getItem;
                            }

                        }catch(NullPointerException ignored){}
                    }
                }
                for(UUID id : toRemove){
                    keyList.remove(id);
                }
            }
        }
    }

    public static ItemStack seize(ItemStack item){
        try{
            ItemStack prevItem = item.clone();
            item.setType(Material.DOUBLE_PLANT);
            ArrayList<String> lore = new ArrayList<>();
            lore.add(ChatColor.RED + "This item was SEIZED! Take it to");
            lore.add(ChatColor.RED + "the Streak Police to get it back.");
            ItemMeta meta = item.getItemMeta();
            meta.setLore(lore);
            String name = meta.getDisplayName();
            meta.setDisplayName(ChatColor.RED + "Seized " + name);
            item.setItemMeta(meta);
            NBTItem nbt = new NBTItem(item);
            NBTCompound compound = nbt.getCompound("CustomAttributes");
            compound.setObject("PREV_LORE", prevItem.getItemMeta().getLore());
            compound.setDouble("WEIGHT", 0.00);
            System.out.println(new NBTItem(prevItem).getCompound("CustomAttributes").getString("ID"));
            compound.setString("PREV_ID", new NBTItem(prevItem).getCompound("CustomAttributes").getString("ID"));
            compound.setString("ID", "TOKEN");
            compound.setString("PREV_MATERIAL", prevItem.getType().toString());
            return nbt.getItem();
        }catch(NullPointerException n){
            throw new NullPointerException("An item that was requested to be seized did not have the proper NBT. Item: " + item.toString());
        }

    }


   /*  pre-npcsuper spolice code
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


    public void createNPC(Location loc) {
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
                Bukkit.getConsoleSender().sendMessage("Skin fetch failed! NPC: Streak_Police");

            }
        });
    }

    @EventHandler
    public void moveOnInv(PlayerMoveEvent ev){
        try{
            if(cantClick.contains(ev.getPlayer().getUniqueId())) ev.setCancelled(true);
        }catch(NullPointerException ignored){}
    }
                              @EventHandler
                                    public void pickupOnInv(PlayerPickupItemEvent e){
                                        try {
                                            if (e.getPlayer().getOpenInventory().getTopInventory().getName().equals("Recover Seized Items")) {
                                                e.setCancelled(true);
                                            }
                                        }catch(NullPointerException ignored){ }
                                    }

    */


}
