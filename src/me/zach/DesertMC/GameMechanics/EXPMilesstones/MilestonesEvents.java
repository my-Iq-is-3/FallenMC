package me.zach.DesertMC.GameMechanics.EXPMilesstones;

import me.zach.DesertMC.DesertMain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

import static me.zach.DesertMC.DesertMain.*;

public class MilestonesEvents implements Listener {
    @EventHandler
    public void inventoryClick(InventoryClickEvent e){
        if(e.getClickedInventory().getName().equals("EXP Milestones")){
            e.setCancelled(true);
            try{
                int level = MilestonesInventory.RewardsItem.parseLevel(e.getCurrentItem());
                if (DesertMain.unclaimed.contains(level)) {
                    new MilestonesInventory.RewardsItem(level, lv).claim((Player) e.getWhoClicked());
                }
            }catch(IllegalArgumentException ignored){}
            if(e.getCurrentItem().getType().equals(Material.BREWING_STAND_ITEM) && e.getCurrentItem().containsEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL)){
                if(DesertMain.claiming.contains(e.getWhoClicked().getUniqueId())) return;
                else claiming.add(e.getWhoClicked().getUniqueId());
                ArrayList<MilestonesInventory.RewardsItem> toClaim = new ArrayList<>();
                for(int i : unclaimed){
                    toClaim.add(new MilestonesInventory.RewardsItem(i, DesertMain.lv));
                }
                Player p = (Player) e.getWhoClicked();
                p.sendMessage(ChatColor.YELLOW + "Claiming all your milestone rewards...");
                p.closeInventory();
                p.playSound(p.getLocation(), Sound.NOTE_STICKS, 10, 1);
                new BukkitRunnable(){
                    int i = 0;
                    public void run(){
                        try{
                            MilestonesInventory.RewardsItem rewardsItem = toClaim.get(i);
                            rewardsItem.claim(p);
                        }catch(IndexOutOfBoundsException ex){
                            claiming.remove(p.getUniqueId());
                            cancel();
                        }
                        i++;
                    }
                }.runTaskTimer(Bukkit.getPluginManager().getPlugin("Fallen"), 15, 25);
            }
        }
    }
}
