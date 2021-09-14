package me.zach.DesertMC.Utils.ActionBar;

//import me.zach.DesertMC.DesertMain;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
//import org.bukkit.scheduler.BukkitRunnable;

public class ActionBarUtils {
    public static void sendActionBar(Player player, String message){
        PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(message), (byte) 2);

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

//    public static void sendActionBar(Player player, String startMessage, ActionBarModifier modifier, int runAmount, int frequency){
//        new BukkitRunnable(){
//            int i = 0;
//            String bar = startMessage;
//            @Override
//            public void run() {
//                if(i < runAmount){
//                    if(modifier == null || i == 0) sendActionBar(player, startMessage);
//                    else{
//                        bar = modifier.modActionBar(bar, player);
//                        sendActionBar(player, bar);
//                    }
//                    i++;
//                }else cancel();
//            }
//        }.runTaskTimer(DesertMain.getInstance, 0, frequency);
//    }
//
//    public interface ActionBarModifier {
//        String modActionBar(String currentBar, Player player);
//    }
}
