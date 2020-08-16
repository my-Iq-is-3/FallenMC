package me.zach.DesertMC.mythicalitems;

import me.zach.DesertMC.mythicalitems.items.DestroyerItem;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class Mythical {

    private static HashMap<Integer, ItemStack> mythicalItems = new HashMap<>();
    private ItemStack item = null;
    private MythicalItem mythicalItem = null;

    public Mythical(MythicalItem mythicalItem){
        this.item = mythicalItem.getItem();
        this.mythicalItem = mythicalItem;
    }

    private Mythical(){

    }

    public static Mythical getNewInstance(){
        return new Mythical();
    }



    // On class load (happens once at the start of the program) load
    // all of the mythical weapons into a hashmap called mythicalItems
    // and make the key an increasing number
    static {
        MythicalItem destroyerItem = new DestroyerItem();
        mythicalItems.put(1, parseItem(destroyerItem));

    }

    public ItemStack getByID(int id){
        return mythicalItems.get(id);
    }

    public ItemStack parseItem(){
        return item;
    }

    private static ItemStack parseItem(MythicalItem mythicalItem){
        return mythicalItem.getItem();
    }

    public int parseID(){
        return mythicalItem.getID();
    }

    public int parsePrice(MythicalItem mythicalItem){
        return mythicalItem.getPrice();
    }

}
