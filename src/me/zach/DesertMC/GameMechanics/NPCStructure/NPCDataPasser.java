package me.zach.DesertMC.GameMechanics.NPCStructure;

import net.jitse.npclib.api.events.NPCInteractEvent;
import org.bukkit.inventory.Inventory;

public interface NPCDataPasser {
    public Inventory getStartInventory(NPCInteractEvent event);
}
