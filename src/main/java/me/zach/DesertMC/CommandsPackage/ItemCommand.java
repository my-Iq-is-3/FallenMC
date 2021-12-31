package me.zach.DesertMC.CommandsPackage;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.mythicalitems.Mythical;
import net.minecraft.server.v1_8_R3.CommandExecute;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import itempackage.Items;
import java.util.*;

public class ItemCommand extends CommandExecute implements CommandExecutor, Listener, TabCompleter {
    public static final ItemCommand INSTANCE = new ItemCommand();

    private static final HashMap<String,ItemStack> items = new HashMap<>();
    public static final HashMap<String, String> enchs = new HashMap<>();
    public static final char DOT = '\u25CF';
    static {
        items.put("MagicWand",itempackage.Items.getMagicWand());
        items.put("WizardBlade", Items.getWizardBlade());
        items.put("ScoutGoggles",Items.getScoutGoggles());
        items.put("VolcanicSword",Items.getVolcanicSword());
        items.put("CorruptedSword", Items.getCorruptedSword());
        items.put("LuckyChestplate", Items.getLuckyChestplate());
        items.put("CorrupterLeggings", Items.getCorrupterLeggings());
        items.put("ScoutBlade", Items.getScoutBlade());
        items.put("Dagger",Items.getDagger());
        items.put("StubbornBoots", Items.getStubbornBoots());
        items.put("FirstAidKit", Items.getFirstAidKit());
        items.put("Stomper", Items.getStomper());
        items.put("MagicSnack", Items.getMagicSnack());
        items.put("ProteinSnack", Items.getProteinSnack());
        items.put("LavaCake", Items.getLavaCake());
        items.put("Bludgeon", Items.getBludgeon());
        items.put("EnergySnack", Items.getEnergySnack());
        enchs.put("no_mercy",ChatColor.GRAY + "\u25CF" + ChatColor.BLUE + " No Mercy");
        enchs.put("giant_slayer",ChatColor.LIGHT_PURPLE + "\u25CF" + ChatColor.BLUE + " Giant Slayer");
        enchs.put("spike",ChatColor.GRAY + "\u25CF" + ChatColor.BLUE + " Spike");
        enchs.put("quick",ChatColor.GRAY + "\u25CF" + ChatColor.BLUE + " Quick");
    }

    public ItemCommand(){
        DesertMain.getInstance.getCommand("item").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args){
        if(commandSender instanceof Player){
            if(commandSender.hasPermission("item") ||commandSender.hasPermission("admin")){

                Player player = (Player) commandSender;

                if(args.length == 1){
                    try {


                        boolean isValid = false;
                        if (args[0].equals("Mythical")) {
                            isValid = true;
                            player.sendMessage(ChatColor.RED + "Usage: /item Mythical <id>");
                        } else if (items.get(args[0]) != null) {
                            player.getInventory().addItem(items.get(args[0]));
                            isValid = true;
                        }

                        if (!isValid) {
                            player.sendMessage(ChatColor.RED + "Please say a valid item.");
                        }




                    }catch(Exception e){
                        player.sendMessage(ChatColor.RED + "There was an error.");
                        e.printStackTrace();
                    }

                }else if(args.length == 0){
                    player.sendMessage(ChatColor.RED + "Usage: /item <item name>");
                    return false;
                }else if(args.length == 2){
                    if(args[0].equalsIgnoreCase("mythical")){
                        try{
                            int id = Integer.parseInt(args[1]);
                            player.getInventory().addItem(Mythical.getNewInstance().getByID(id));
                        }catch(NumberFormatException e){
                            player.sendMessage(ChatColor.RED + "Invalid ID." + ChatColor.GRAY + " (" + e + ").");
                        }
                    }
                }

            }else{
                commandSender.sendMessage(ChatColor.RED + "Only admins can use this command.");
                return false;
            }
        }else{
            commandSender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return false;
        }
        return false;
    }




    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
            List<String> args = new ArrayList<>();
            if (strings.length == 1) {
                if (commandSender.hasPermission("admin") && command.getName().equalsIgnoreCase("item")) {
                    args = Arrays.asList("ScoutGoggles", "MagicWand", "VolcanicSword", "Mythical", "Dagger", "StubbornBoots", "WizardBlade", "CorruptedSword", "LuckyChestplate", "CorrupterLeggings", "FirstAidKit", "ScoutBlade", "MagicSnack", "ProteinSnack", "LavaCake", "EnergySnack", "Bludgeon", "Stomper");
                }
            }
            return args;
    }
}
