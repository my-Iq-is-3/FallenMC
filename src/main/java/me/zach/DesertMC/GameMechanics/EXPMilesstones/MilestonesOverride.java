package me.zach.DesertMC.GameMechanics.EXPMilesstones;

import itempackage.Items;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import static me.zach.DesertMC.GameMechanics.EXPMilesstones.MilestonesInventory.RewardsItem.addOverride;
import static me.zach.DesertMC.GameMechanics.EXPMilesstones.MilestonesInventory.RewardsItem.RewardOverride;
import static me.zach.DesertMC.GameMechanics.EXPMilesstones.MilestonesInventory.RewardsItem.IRewardGrant;
public class MilestonesOverride {
    public static void addOverrides(){
        addOverride(new RewardOverride("Diamond Hammer", 10, (player, mLevel) -> {
            if(player.getInventory().firstEmpty() != -1) {
                player.getInventory().addItem(Items.getDiamondHammer());
                return true;
            } else{
                player.sendMessage(ChatColor.RED + "Full Inventory!");
                return false;
            }
        }, Material.DIAMOND_HOE), 10);

        addOverride(new RewardOverride("Special Hammer", 20, (player, mLevel) ->{
            if(player.getInventory().firstEmpty() != -1) {
                player.getInventory().addItem(Items.getSpecialHammer());
                return true;
            } else{
                player.sendMessage(ChatColor.RED + "Full Inventory!");
                return false;
            }
        }, Material.GOLD_HOE), 15);

        addOverride(new RewardOverride("Death Defiance", 7, (player, mLevel) ->{
            if(player.getInventory().firstEmpty() != -1){
                player.getInventory().addItem(Items.getDD());
                return true;
            }else{
                player.sendMessage(ChatColor.RED + "Full Inventory!");
                return false;
            }
        }, Material.SKULL_ITEM), 7);
    }
}
