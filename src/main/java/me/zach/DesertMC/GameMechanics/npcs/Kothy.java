package me.zach.DesertMC.GameMechanics.npcs;

import me.zach.DesertMC.ClassManager.KothyMenu;
import me.zach.DesertMC.GameMechanics.NPCStructure.NPCSuper;
import net.jitse.npclib.api.events.NPCInteractEvent;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.inventory.Inventory;

public class Kothy extends NPCSuper {
    public Kothy(){
        super(ChatColor.AQUA + "Kothy", 1186706551, "Sorry, I can't give you coffee. But I can show some useful options for managing your classes, buying items, and more!", Sound.VILLAGER_HAGGLE, ChatColor.GRAY + "Click me to view class selectors and more");
    }

    public Inventory getStartInventory(NPCInteractEvent event){
        return new KothyMenu(event.getWhoClicked()).getInventory();
    }
}
