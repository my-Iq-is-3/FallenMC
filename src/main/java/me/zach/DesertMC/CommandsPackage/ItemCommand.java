package me.zach.DesertMC.CommandsPackage;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.Utils.MiscUtils;
import me.zach.DesertMC.mythicalitems.Mythical;
import net.minecraft.server.v1_8_R3.CommandExecute;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Supplier;

public class ItemCommand implements CommandExecutor, Listener, TabCompleter {
    public static final ItemCommand INSTANCE = new ItemCommand();

    private static final HashMap<String, Supplier<ItemStack>> items = new HashMap<>();
    private static final Set<String> names;
    public static final HashMap<String, String> enchs = new HashMap<>();
    public static final char DOT = '\u25CF';
    static {
        Method[] methods = Items.class.getMethods();
        for(Method method : methods){
            if(method.getParameters().length == 0 && method.getReturnType() == ItemStack.class && method.getName().startsWith("get")){
                if(!method.isAccessible()) method.setAccessible(true); //hmph
                String name = method.getName().replace("get", "");
                items.put(name, () -> {
                    try{
                        return (ItemStack) method.invoke(null);
                    }catch(IllegalAccessException | InvocationTargetException e){
                        e.printStackTrace();
                    }
                    return null;
                });
            }
        }
        names = items.keySet();
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
            Player player = (Player) commandSender;
            if(MiscUtils.isAdmin(player)){
                if(args.length == 1){
                    try {


                        boolean isValid = false;
                        if (args[0].equals("Mythical")) {
                            isValid = true;
                            player.sendMessage(ChatColor.RED + "Usage: /item Mythical <id>");
                        } else if (items.get(args[0]) != null) {
                            player.getInventory().addItem(items.get(args[0]).get());
                            isValid = true;
                        }

                        if (!isValid) {
                            player.sendMessage(ChatColor.RED + "Please say a valid item.");
                        }else if(player.getInventory().firstEmpty() == -1) player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 10, 1);




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
        if(commandSender instanceof Player){
            Player player = (Player) commandSender;
            if(strings.length == 1){
                if(MiscUtils.isAdmin(player) && command.getName().equalsIgnoreCase("item")){
                    args = MiscUtils.narrowTabComplete(strings[0], names);
                }
            }
        }
        return args;
    }
}
