package me.zach.DesertMC.CommandsPackage;

import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.GameMechanics.NPCStructure.SavedNPC;
import me.zach.DesertMC.GameMechanics.NPCStructure.SimpleNPC;
import me.zach.DesertMC.Utils.MiscUtils;
import me.zach.DesertMC.Utils.StringUtils.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

public class NPCCommand implements CommandExecutor, TabCompleter {
    //TODO this
    static final HashMap<String, Supplier<? extends SimpleNPC>> NPC_MAP = new HashMap<>();


    public static Supplier<? extends SimpleNPC> getNPCSupplier(String name){
        return NPC_MAP.get(name);
    }

    public static void registerNPC(String name, Supplier<? extends SimpleNPC> npc){
        NPC_MAP.put(name, npc);
    }

    @SuppressWarnings("unchecked")
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(sender instanceof Player){
            Player player = (Player) sender;
            if(command.getName().equalsIgnoreCase("spawnnpc")){
                if(MiscUtils.isAdmin(player)){
                    if(args.length > 1){
                        boolean save;
                        if(args[0].equalsIgnoreCase("true")) save = true;
                        else if(args[0].equalsIgnoreCase("false")) save = false;
                        else return false;
                        StringBuilder supplierName = new StringBuilder();
                        String[] npcNameArr = new String[args.length - 1];
                        System.arraycopy(args, 1, npcNameArr, 0, npcNameArr.length);
                        for(String npcNameWord : npcNameArr)
                            supplierName.append(StringUtil.capitalizeFirst(npcNameWord));
                        Supplier<? extends SimpleNPC> supplier = getNPCSupplier(supplierName.toString());
                        if(supplier != null){
                            SimpleNPC npc = supplier.get();
                            Location location = player.getLocation();
                            npc.createNPC(player.getLocation());
                            if(save){
                                DesertMain main = DesertMain.getInstance;
                                Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
                                    List<SavedNPC> npcs = new ArrayList<>(SavedNPC.stored(main));
                                    npcs.add(new SavedNPC(location, supplierName.toString()));
                                    main.getConfig().set(SavedNPC.PATH, npcs);
                                    main.saveConfig();
                                    sender.sendMessage(ChatColor.GREEN + "Saved your NPC.");
                                });
                            }
                        }else player.sendMessage(ChatColor.RED + "Sorry, that NPC wasn't found.");
                        return true;
                    }else{
                        return false;
                    }
                }else{
                    player.sendMessage(ChatColor.RED + "Sorry, you don't have access to that command.");
                }
                return true;
            }else return false;
        }else{
            sender.sendMessage("You must be a player to use this command!");
            return true;
        }
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args){
        if(args.length > 1){
            String[] newArgs = new String[args.length - 1];
            System.arraycopy(args, 1, newArgs, 0, newArgs.length); //account for loadOnStartup argument
            return MiscUtils.narrowTabComplete(String.join(" ", newArgs), NPC_MAP.keySet());
        }else return Collections.emptyList();
    }
}
