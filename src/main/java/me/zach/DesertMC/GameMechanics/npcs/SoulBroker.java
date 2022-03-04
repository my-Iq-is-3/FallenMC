package me.zach.DesertMC.GameMechanics.npcs;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.GameMechanics.NPCStructure.NPCSuper;
import me.zach.DesertMC.Utils.Config.ConfigUtils;
import me.zach.DesertMC.Utils.nbt.NBTUtil;
import net.jitse.npclib.api.events.NPCInteractEvent;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Level;

import static org.bukkit.Note.Tone;
//TODO refactor horrible class
public class SoulBroker extends NPCSuper implements Listener{
    public static final int SKIN_ID = 1646375186;
    static Plugin pl = DesertMain.getInstance;
    static ItemStack clear = new ItemStack(Material.GLASS);
    static ItemStack reduce = new ItemStack(Material.GOLD_NUGGET);
    static ItemStack pane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 9);
    static ItemMeta paneMeta = pane.getItemMeta();
    static ItemStack increase = new ItemStack(Material.EMERALD_BLOCK);
    static ItemStack decrease = new ItemStack(Material.REDSTONE_BLOCK);
    static ItemMeta increaseMeta = increase.getItemMeta();
    static ItemMeta decreaseMeta = decrease.getItemMeta();
    static Set<UUID> dontGiveItemOnClose = new HashSet<>();
    static{
        //creating some unchanging items statically to save on processing power (yeah right past me)
        ItemMeta clearMeta = clear.getItemMeta();
        paneMeta.setDisplayName(" ");
        pane.setItemMeta(paneMeta);
        clearMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
        clearMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Clear Weight");
        clearMeta.setLore(Arrays.asList(ChatColor.YELLOW + "Temporarily clear an item of", ChatColor.YELLOW + "its weight.", "", ChatColor.YELLOW + "Cost: (Add an item to view cost)"));
        clearMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        clear.setItemMeta(clearMeta);
        ItemMeta reduceMeta = reduce.getItemMeta();
        reduceMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Reduce Weight Per Hit");
        reduceMeta.setLore(Arrays.asList(ChatColor.YELLOW + "Reduce the amount weight added to", ChatColor.YELLOW + "an item each time you hit a player.", "", ChatColor.YELLOW + "Cost: " + ChatColor.BLUE + "15" + ChatColor.LIGHT_PURPLE + " Souls" + ChatColor.YELLOW + " per " + ChatColor.BLUE + "0.00005%"));
        reduce.setItemMeta(reduceMeta);

        decreaseMeta.setDisplayName(ChatColor.GREEN + "Decrease WPH to remove");
        decreaseMeta.setLore(Arrays.asList(ChatColor.GRAY + "Current Increment: " + ChatColor.BLUE + "0.00005%", ChatColor.GRAY + "Right click to cycle increments"));
        decrease.setItemMeta(decreaseMeta);
        NBTItem decreaseNBT = new NBTItem(decrease);
        decreaseNBT.addCompound("CustomAttributes").setDouble("INCREMENT", 0.00005);
        decrease = decreaseNBT.getItem();

        increaseMeta.setDisplayName(ChatColor.GREEN + "Increase WPH to remove");
        increaseMeta.setLore(Arrays.asList(ChatColor.GRAY + "Current Increment: " + ChatColor.BLUE + "0.00005%", ChatColor.GRAY + "Right click to cycle increments"));
        increase.setItemMeta(increaseMeta);
        NBTItem increaseNBT = new NBTItem(increase);
        increaseNBT.addCompound("CustomAttributes").setDouble("INCREMENT", 0.00005);
        increase = increaseNBT.getItem();
    }



    public SoulBroker(){
        super(ChatColor.LIGHT_PURPLE + "Soul Broker",
                SKIN_ID,
                ChatColor.WHITE + "I can wipe an items weight, or decrease its Weight Per Hit, but only in exchange for a few souls.",
                Sound.WITHER_IDLE,
                ChatColor.GRAY + "Click me to barter your souls");
    }

    public static int calculateReducePrice(double WPHtoRemove) throws Exception{
        long price = Math.round((WPHtoRemove / 0.00005) * 15);
        if(price > Integer.MAX_VALUE) throw new Exception(ChatColor.RED + "Price calculated too high!");
        return (int) price;
    }

    @EventHandler
    public void invClick(InventoryClickEvent event){
        Player p = (Player) event.getWhoClicked();
        Inventory playerInv = p.getInventory();
        //does the player have the inventory open?
        if(openInv.contains(p.getUniqueId())){
            //cancel event immediately
            event.setCancelled(true);
            if(event.getClick().equals(ClickType.LEFT) || event.getClick().equals(ClickType.RIGHT)) {
                //shop inventory variable
                Inventory shopInv = p.getOpenInventory().getTopInventory();
                //calling the Reduce Item WPH inventory event if needed, and then returning
                if (shopInv.getName().equals("Reduce Item WPH")) {
                    reduceInvClick(event);
                    return;
                }
                //getting the item and nbt
                ItemStack item = event.getCurrentItem();
                NBTCompound nbt = new NBTItem(item);
                //seeing if we can extract the CustomAttributes compound
                try {
                    nbt = nbt.getCompound("CustomAttributes");
                } catch (NullPointerException ignored) {
                }
                //does the clicked item have the necessary values to be a player's item?
                boolean playerItem = false;
                try {
                    playerItem = nbt.hasKey("WEIGHT") && nbt.hasKey("WEIGHT_ADD") && !NBTUtil.getCustomAttrString(nbt, "ID").equals("TOKEN");
                }catch(NullPointerException ignored){}
                if(playerItem){
                    //checking if it is in the shopInv or player inventory
                    if (!event.getClickedInventory().getName().equals(shopInv.getName())) {
                        //checking if the shop inventory's item slot is open, and if so, adding the item occupying the slot into the player's inventory
                        try {
                            if (!shopInv.getItem(13).getType().equals(Material.AIR)) {
                                playerInv.addItem(shopInv.getItem(13));
                                shopInv.clear(13);
                            }
                        } catch (NullPointerException ignored) {
                        }
                        //setting the shop item slot and clearing the player's slot
                        shopInv.setItem(13, event.getCurrentItem());
                        playerInv.clear(event.getSlot());
                        //creating a new "clear" item and other significant values, because some values in the lore and NBT need to be modified
                        ItemStack newClear = clear.clone();
                        ItemMeta newClearMeta = newClear.getItemMeta();
                        List<String> nLore = newClearMeta.getLore();
                        //calculating the price
                        int price = (int) (15 * (nbt.getDouble("WEIGHT_ADD") / 0.01));
                        //adding it to the lore, and replacing the old "(Add an item to view cost)" string
                        nLore.set(3, nLore.get(3).replaceAll("\\(Add an item to view cost\\)", ChatColor.BLUE.toString() + price));
                        //creating an NBTItem for the clear item, and then setting the "PRICE" value with the integer we have already calculated
                        newClearMeta.setLore(nLore);
                        newClear.setItemMeta(newClearMeta);
                        NBTItem newClearNBT = new NBTItem(newClear);
                        newClearNBT.addCompound("CustomAttributes").setInteger("PRICE", price);
                        shopInv.setItem(11, newClearNBT.getItem());
                    } else {
                        if (event.getSlot() != 13) {
                            //in case there is somehow a player item in any slot other than the designated one
                            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Player " + p.getUniqueId() + " clicked an item with the attributes WEIGHT and WEIGHT_ADD inside the Soul Shop inventory, but the slot wasn't 13! Something's fishy...");
                        } else {
                            shopInv.clear(13);
                            shopInv.setItem(11, clear);
                            playerInv.addItem(item);
                        }
                    }
                } else if (item.getItemMeta().getDisplayName().equals(ChatColor.LIGHT_PURPLE + "Clear Weight")) {
                    if (!event.getClickedInventory().getName().equals(shopInv.getName())) {
                        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Player " + p.getUniqueId() + " clicked the Clear Weight item, but the click didn't occur in the shop inventory! Something's fishy...");
                        p.playSound(p.getLocation(), Sound.ANVIL_LAND, 10, 1);
                    } else {
                        //Checking if it is the extracted CustomAttributes compound
                        if (!(nbt instanceof NBTItem)) {
                            //checking if it has a price value
                            if (nbt.hasKey("PRICE")) {
                                int price = nbt.getInteger("PRICE");
                                if (ConfigUtils.deductSouls(p, price)) {
                                    playerInv.addItem(clearWeight(shopInv.getItem(13)));
                                    shopInv.clear(13);
                                    p.closeInventory();
                                    npcMessage(p, "I cleared your weapon's weight, and took the promised souls in return. Thanks for trading with me.");
                                } else {
                                    p.playSound(p.getLocation(), Sound.ANVIL_LAND, 10, 1);
                                }
                            } else {
                                p.playSound(p.getLocation(), Sound.ANVIL_LAND, 10, 1);
                            }
                        } else {
                            p.playSound(p.getLocation(), Sound.ANVIL_LAND, 10, 1);
                        }
                    }
                }else if(item.getItemMeta().getDisplayName().equals("§dReduce Weight Per Hit")){
                    if (!event.getClickedInventory().getName().equals(shopInv.getName())) {
                        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Player " + p.getUniqueId() + " clicked the Reduce Item Weight Per Hit item, but the click didn't occur in the shop inventory! Something's fishy...");
                        p.playSound(p.getLocation(), Sound.ANVIL_LAND, 10, 1);
                    }else{
                        ItemStack itemToReduce = shopInv.getItem(13);
                        if(itemToReduce == null){
                            p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 10, 1);
                            p.sendMessage(ChatColor.RED + "Insert an item before trying to reduce it's WPH!");
                        }else{
                            dontGiveItemOnClose.add(p.getUniqueId());
                            p.closeInventory();
                            p.openInventory(getReduceInventory(p, itemToReduce));
                            openInv.add(p.getUniqueId());
                        }
                    }
                } else {
                    p.playSound(p.getLocation(), Sound.ANVIL_LAND, 10, 1);
                }
            }
        }
    }

    public void reduceInvClick(InventoryClickEvent e){
        e.setCancelled(true);
        Bukkit.getConsoleSender().sendMessage("Reduce inventory click registered.");
        Player p = (Player) e.getWhoClicked();
        Inventory inv = e.getClickedInventory();
        ItemStack item = e.getCurrentItem();
        ItemMeta meta = item.getItemMeta();
        if(e.getClick().equals(ClickType.LEFT) || e.getClick().equals(ClickType.RIGHT)) {
            boolean ifIncrease = meta.getDisplayName().equals(ChatColor.GREEN + "Increase WPH to remove");
            boolean ifDecrease = meta.getDisplayName().equals(ChatColor.GREEN + "Decrease WPH to remove");
            if (ifIncrease || ifDecrease){
                try{
                    NBTItem nbt = new NBTItem(item);
                    NBTCompound compound = nbt.getCompound("CustomAttributes");
                    double increment = compound.getDouble("INCREMENT");
                    if(e.getClick().equals(ClickType.LEFT)){
                        ItemStack book = inv.getItem(13);
                        NBTItem bookNBT = new NBTItem(book);
                        double weaponWPH = new NBTItem(inv.getItem(4)).getCompound("CustomAttributes").getDouble("WEIGHT_ADD");
                        double WPHtoRemove = bookNBT.getCompound("CustomAttributes").getDouble("WPH_TO_REMOVE");
                        //checking if the item WPH after modification already amounts to 0
                        //creating a split condition, if the item clicked is the decrease item we set the condition to if you can't remove anymore, if it is the increase item we set the condition to if you can't add anymore
                        boolean splitCond;
                        if(ifIncrease) splitCond = weaponWPH == WPHtoRemove;
                        else splitCond = WPHtoRemove == 0;
                        //creating another split condition, if we the item clicked is the decrease item, we set the condition to if the WPH to remove (after modification) is less than 0, if it is the increase item we set the condition to if the WPH to remove (after modification) subtracted from the weapon WPH is less than 0
                        boolean splitCond2;
                        if(ifIncrease) splitCond2 = (WPHtoRemove + increment) > weaponWPH;
                        else splitCond2 = WPHtoRemove - increment < 0;
                        double splitDouble;
                        if(ifIncrease) splitDouble = weaponWPH;
                        else splitDouble = 0;
                        if(splitCond){
                            p.playSound(p.getLocation(), Sound.ANVIL_LAND, 10, 1);
                            return;
                        }else if(splitCond2){
                            WPHtoRemove = splitDouble;
                        }else{
                            //adding (or removing) the increment
                            if(ifIncrease) WPHtoRemove += increment;
                            else WPHtoRemove -= increment;
                        }
                        int price;
                        try{
                            price = calculateReducePrice(WPHtoRemove);
                        }catch(Exception exception){
                            Bukkit.getLogger().log(Level.WARNING, "Exception while calculating WPH removal price,", exception);
                            p.playSound(p.getLocation(), Sound.ANVIL_LAND, 10, 1);
                            return;
                        }
                        //iterating through the book lore and updating some lines
                        inv.setItem(13, getBook(p, weaponWPH, WPHtoRemove, price));
                    }else if(e.getClick().equals(ClickType.RIGHT)){
                        DecimalFormat formatter = new DecimalFormat("#.#####");
                        formatter.setRoundingMode(RoundingMode.HALF_EVEN);
                        if(increment * 10 > 0.05){
                            List<String> newLore = item.getItemMeta().getLore();
                            newLore.remove(0);
                            newLore.add(0, ChatColor.GRAY + "Current increment: " + ChatColor.BLUE + formatter.format(0.00005) + "%");
                            ItemMeta newMeta = item.getItemMeta();
                            newMeta.setLore(newLore);
                            item.setItemMeta(newMeta);
                            p.playSound(p.getLocation(), Sound.NOTE_PLING, 10, 1.1f);
                            NBTItem newNBT = new NBTItem(item);
                            newNBT.getCompound("CustomAttributes").setDouble("INCREMENT", 0.00005);
                            if(ifIncrease) inv.setItem(17, newNBT.getItem());
                            else inv.setItem(9, newNBT.getItem());
                        }else{
                            final double newIncrement = increment * 10;
                            p.playSound(p.getLocation(), Sound.NOTE_PLING, 10, 1);
                            List<String> newLore = item.getItemMeta().getLore();
                            newLore.remove(0);
                            newLore.add(0, ChatColor.GRAY + "Current increment: " + ChatColor.BLUE + formatter.format(newIncrement) + "%");
                            ItemMeta newMeta = item.getItemMeta();
                            newMeta.setLore(newLore);
                            item.setItemMeta(newMeta);
                            NBTItem newNBT = new NBTItem(item);
                            newNBT.getCompound("CustomAttributes").setDouble("INCREMENT", newIncrement);
                            if(ifIncrease) inv.setItem(17, newNBT.getItem());
                            else inv.setItem(9, newNBT.getItem());
                        }
                    }
                }catch(NullPointerException requiredValuesAbsent){
                    throw new NullPointerException(ChatColor.RED + "Error processing \"Increase/Decrease WPH to remove\" item click event: I'm pretty sure that an item did not have the proper NBT values (CustomAttributes, INCREMENT).");
                }
            }else if(meta.getDisplayName().equals(ChatColor.YELLOW + "Weapon Details")){
                NBTItem nbt = new NBTItem(item);
                if(nbt.getCompound("CustomAttributes").getInteger("PRICE") > 0) {
                    if (ConfigUtils.deductSouls(p, nbt.getInteger("PRICE"))) {
                        //if the player has enough gems, subtract the gems and remove the WPH from their weapon.
                        NBTItem weaponNBT = new NBTItem(inv.getItem(4));
                        weaponNBT.getCompound("CustomAttributes").setDouble("WEIGHT_ADD", weaponNBT.getCompound("CustomAttributes").getDouble("WEIGHT_ADD") - nbt.getCompound("CustomAttributes").getDouble("WPH_TO_REMOVE"));
                        inv.clear(4);
                        p.closeInventory();
                        p.getInventory().addItem(weaponNBT.getItem());
                        List<List<Note>> notes;
                        notes = Arrays.asList(naturalNoteChord(0, Tone.A, Tone.C), naturalNoteChord(0, Tone.B, Tone.D), naturalNoteChord(0, Tone.C, Tone.E), naturalNoteChord(1, Tone.D, Tone.F), naturalNoteChord(1, Tone.E, Tone.G));
                        npcMessage(p, "Alright, I just took that WPH off your item. Just... do me a favor and don't tell anyone about this, ok? This stuff is my entire income... and standing in one place won't pay for itself ;)");
                        new BukkitRunnable() {
                            int i = 0;
                            public void run() {
                                if(i < notes.size()) {
                                    for (Note note : notes.get(i)) {
                                        p.playNote(p.getLocation(), Instrument.PIANO, note);
                                    }
                                    i++;
                                }else cancel();
                            }
                        }.runTaskTimer(pl, 0, 3);
                    } else {
                        //if the player doesn't have enough gems, close the inventory, tell them they don't have enough gems, and play a sound
                        p.closeInventory();
                        p.playSound(p.getLocation(), Sound.ANVIL_LAND, 10, 1);
                        npcMessage(p, "Hey, stop tryna cheap me out. I don't negotiate my rates. Get a few more souls then come back to me.");
                    }
                }else{
                    p.closeInventory();
                    npcMessage(p, "But... you didn't even ask me to remove anything...");
                }
            }
        }
    }

    private static ArrayList<Note> naturalNoteChord(int octave, Tone... tone){
        ArrayList<Note> chord = new ArrayList<>();
        for(Tone t : tone){
            chord.add(Note.natural(octave, t));
        }
        return chord;
    }

    public static ItemStack getBook(Player p, double weaponWPH, double WPHtoRemove, int price){
        ItemStack book = new ItemStack(Material.BOOK);
        ItemMeta bookMeta = book.getItemMeta();
        bookMeta.setDisplayName(ChatColor.YELLOW + "Weapon Details");
        bookMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        DecimalFormat formatter = new DecimalFormat("#.#####");
        formatter.setRoundingMode(RoundingMode.HALF_EVEN);
        ArrayList<String> lore = new ArrayList<>(Arrays.asList(ChatColor.YELLOW + "Weight Per Hit to remove: " + ChatColor.BLUE + formatter.format(WPHtoRemove),
                ChatColor.YELLOW + "Use the buttons on the",
                ChatColor.YELLOW + "left and right to modify this.",
                ChatColor.YELLOW + "Item WPH after modification: " + (formatter.format(weaponWPH - WPHtoRemove)),
                ChatColor.YELLOW + "Price: " + ChatColor.BLUE + price + ChatColor.LIGHT_PURPLE + " Souls"));
        if(ConfigUtils.getSouls(p) >= price){
            lore.add(ChatColor.GREEN + "Click to confirm!");
            bookMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
        }else lore.add(ChatColor.RED + "Not enough souls!");
        bookMeta.setLore(lore);
        book.setItemMeta(bookMeta);
        NBTItem bookNBT = new NBTItem(book);
        bookNBT.addCompound("CustomAttributes").setInteger("PRICE", price);
        bookNBT.getCompound("CustomAttributes").setDouble("WPH_TO_REMOVE", WPHtoRemove);
        return bookNBT.getItem();
    }

    public Inventory getReduceInventory(Player p, ItemStack weaponToReduce) throws NullPointerException{
        double WPH;
        Inventory inv = pl.getServer().createInventory(null, 27, "Reduce Item WPH");

        for(int i = 0; i<27; i++){
            inv.setItem(i, pane);
        }

        try{
            WPH = new NBTItem(weaponToReduce).getCompound("CustomAttributes").getDouble("WEIGHT_ADD");
        }catch(NullPointerException requiredValuesAbsent){
            throw new NullPointerException(ChatColor.RED + "Item passed through getReduceInventory method without required nbt values (CustomAttributes, WEIGHT_ADD)");
        }


        inv.setItem(13, getBook(p, WPH,0, 0));
        inv.setItem(4, weaponToReduce);
        inv.setItem(9, decrease);
        inv.setItem(17, increase);
        return inv;
    }

    public static ItemStack clearWeight(ItemStack weapon) throws NullPointerException{
        //defining meta and lore, along with a string that we need for easy access
        ItemMeta meta = weapon.getItemMeta();
        List<String> lore = meta.getLore();
        String wString = ChatColor.GRAY + "Weight: " + ChatColor.GREEN + "0";
        //iterating through each line, and setting it to "Weight: 0" if it is on the weight line
        for(int i = 0; i<lore.size(); i++){
            String line = lore.get(i);
            if(line.contains("Weight: ")){
                lore.set(i, wString);
                break;
            }
        }
        //setting the lore and meta
        meta.setLore(lore);
        weapon.setItemMeta(meta);
        //making a new NBTItem, and throwing a custom NullPointerException if the required values don't exist
        NBTItem weaponNBT = new NBTItem(weapon);
        NBTCompound weaponCompound;
        //retrieving the weapon's CustomAttributes data
        weaponCompound = weaponNBT.getCompound("CustomAttributes");
        //transitioning to the catch statement if the compound doesn't have the WEIGHT key
        if(!weaponCompound.hasKey("WEIGHT"))
            throw new NullPointerException(ChatColor.RED + "Item passed through clearWeight method with required NBT values (CustomAttributes, WEIGHT) absent.");
        //setting the "WEIGHT" NBT value to 0
        weaponCompound.setDouble("WEIGHT", 0.00);
        //returning the clean weapon
        return weaponNBT.getItem();
    }

    @EventHandler
    public void closeOnInv(InventoryCloseEvent event){
        if(event.getInventory().getName().equals("Soul Shop") || event.getInventory().getName().equals("Reduce Item WPH")){
            try{
                if(dontGiveItemOnClose.contains(event.getPlayer().getUniqueId())){
                    dontGiveItemOnClose.remove(event.getPlayer().getUniqueId());
                    return;
                }
                if(event.getInventory().getName().equals("Soul Shop")) {
                    event.getPlayer().getInventory().addItem(event.getInventory().getItem(13));
                }else if(event.getInventory().getName().equals("Reduce Item WPH")){
                    event.getPlayer().getInventory().addItem(event.getInventory().getItem(4));
                }
            }catch(IllegalArgumentException ignored){}
            event.getInventory().clear(13);
        }
    }

    @Override
    public Inventory getStartInventory(NPCInteractEvent event) {
        Inventory startInv = pl.getServer().createInventory(null, 36, "Soul Shop");
        for(int i = 0; i<36; i++){
            startInv.setItem(i, pane);
        }
        startInv.clear(13);
        ItemStack soulsItem = new ItemStack(Material.INK_SACK, 1, (short) 9);
        ItemMeta soulsMeta = soulsItem.getItemMeta();
        int souls = ConfigUtils.getSouls(event.getWhoClicked());
        soulsMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "" + souls + (souls == 1 ? " Souls" : " Soul"));
        soulsItem.setItemMeta(soulsMeta);
        startInv.setItem(15, reduce);
        startInv.setItem(35, soulsItem);
        startInv.setItem(11, clear);
        return startInv;
    }
}
