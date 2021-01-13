package me.zach.DesertMC.GameMechanics;

import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.NPCClass;
import net.jitse.npclib.NPCLib;
import net.jitse.npclib.api.NPC;
import net.jitse.npclib.api.events.NPCInteractEvent;
import net.jitse.npclib.api.skin.MineSkinFetcher;
import net.jitse.npclib.api.skin.Skin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class SoulShop implements Listener, NPCClass {
    public static SoulShop INSTANCE = new SoulShop();
    @Override
    public void createNPC(Location loc) {
        NPCLib library = DesertMain.getNPCLib();
        ArrayList<String> text = new ArrayList<>();

        text.add(ChatColor.LIGHT_PURPLE + "Soul Broker");
        text.add(ChatColor.GRAY + "Click me to barter your souls");
        text.add(ChatColor.GRAY + "for weight-reducing and other wares");
        NPC npc = library.createNPC(text);
        npc.setLocation(loc);
        MineSkinFetcher.fetchSkinFromIdAsync(472860282, new MineSkinFetcher.Callback() {
            @Override
            public void call(Skin skin) {
                npc.setSkin(skin);
                npc.create();
                for(Player player : Bukkit.getServer().getOnlinePlayers()){
                    npc.show(player);
                }
            }
            @Override
            public void failed(){
                Bukkit.getConsoleSender().sendMessage("Skin fetch failed! NPC: Soul_Broker");
            }
        });
    }

}
