package me.ench.main;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import me.ench.items.Hammers;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;
public class Commands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        if(!(commandSender instanceof Player)){ return false;}
        Player p = (Player) commandSender;
        if(command.getName().equalsIgnoreCase("testinv")) {
            new RefineryInventory().openRefineryInventory((Player) commandSender);
        }
        if(command.getName().equalsIgnoreCase("givehammer")) {
            HashMap<String, ItemStack> map = new HashMap<String, ItemStack>();
            map.put("1", Hammers.WoodHammer);
            map.put("2", Hammers.StoneHammer);
            map.put("3", Hammers.IronHammer);
            map.put("4", Hammers.DiamondHammer);
            map.put("5", Hammers.SpecialHammer);
            if (map.containsKey(args[0])) {
                p.getInventory().addItem(map.get(args[0]));
                return true;
            }else {
                p.sendMessage(ChatColor.RED + "Invalid Usage! Usage: /givehammer <hammer level>");
                return false;
            }
        }
        if(command.getName().equalsIgnoreCase("givebookdummy")) {
            ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
            NBTItem bookNBT = new NBTItem(book);
            NBTCompound bookCompound = bookNBT.addCompound("CustomAttributes");
            bookCompound.setInteger("BASE_LEVEL", 3);
            bookCompound.setInteger("REAL_LEVEL", 3);
            p.getInventory().addItem(bookNBT.getItem());
            return true;
        }

        return true;
    }
}