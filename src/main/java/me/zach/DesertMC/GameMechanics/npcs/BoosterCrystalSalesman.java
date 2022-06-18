package me.zach.DesertMC.GameMechanics.npcs;

import de.tr7zw.nbtapi.NBTItem;
import me.zach.DesertMC.GameMechanics.NPCStructure.NPCSuper;
import me.zach.DesertMC.Utils.MiscUtils;
import me.zach.DesertMC.Utils.StringUtils.StringUtil;
import me.zach.DesertMC.Utils.gui.GUIHolder;
import me.zach.DesertMC.Utils.nbt.NBTUtil;
import me.zach.DesertMC.shops.ShopInventory;
import me.zach.DesertMC.shops.ShopItem;
import net.jitse.npclib.api.events.NPCInteractEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;

public class BoosterCrystalSalesman extends NPCSuper {
    public BoosterCrystalSalesman(){
        super(ChatColor.AQUA + "Booster Crystal Salesman", 1385115481, "I sell booster crystals. That's it. I'm the booster crystal salesman, absolutely nothing more.", Sound.VILLAGER_HAGGLE, ChatColor.GRAY + "Click me to buy booster crystals");
    }

    public Inventory getStartInventory(NPCInteractEvent event){
        return new BoosterCrystalInventory(event.getWhoClicked()).getInventory();
    }

    public static class BoosterCrystalInventory extends ShopInventory {
        Inventory inventory;
        public BoosterCrystalInventory(Player player){
            super("Buy Booster Crystals", Collections.singletonList(new BoosterCrystal()), player, (byte) -1, null, 1);
        }
    }

    public static class BoosterCrystal extends ShopItem {
        public BoosterCrystal(){
            super(35, 22);
        }

        protected ItemStack get(){
            ItemStack item = MiscUtils.generateItem(Material.QUARTZ, ChatColor.LIGHT_PURPLE + "Booster Crystal", StringUtil.wrapLore(ChatColor.GRAY + "Right click this while falling down to shoot yourself in the direction you're looking!\n" + ChatColor.DARK_GRAY + "Consumable"), (byte) -1, 1, "BOOSTER_CRYSTAL");
            item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
            NBTItem nbt = new NBTItem(item);
            NBTUtil.checkCustomAttr(nbt).removeKey("UUID");
            return nbt.getItem();
        }
    }
}
