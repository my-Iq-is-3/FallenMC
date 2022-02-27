package me.zach.DesertMC.GameMechanics.EXPMilesstones;

import de.tr7zw.nbtapi.NBTItem;
import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.Utils.nbt.NBTUtil;
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
import java.util.List;

public class MilestonesEvents implements Listener {
    @EventHandler
    public void inventoryClick(InventoryClickEvent e){
        String invName = e.getClickedInventory().getName();
        if(invName != null) {
            if (invName.equals("EXP Milestones")) {
                e.setCancelled(true);
                try {
                    MilestonesInventory.RewardsItem item = MilestonesInventory.RewardsItem.parseLevel(e.getCurrentItem(), (Player) e.getWhoClicked());
                    int level = item.level;
                    if (MilestonesData.get((Player) e.getWhoClicked()).getUnclaimed().contains(level)) {
                        item.claim((Player) e.getWhoClicked());
                    }
                } catch (IllegalArgumentException ignored) {}
                if (e.getCurrentItem().getType().equals(Material.BREWING_STAND_ITEM) && e.getCurrentItem().containsEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL)) {
                    if (DesertMain.claiming.contains(e.getWhoClicked().getUniqueId())) return;
                    else DesertMain.claiming.add(e.getWhoClicked().getUniqueId());
                    ArrayList<MilestonesInventory.RewardsItem> toClaim = new ArrayList<>();
                    List<Integer> unclaimed = MilestonesData.get((Player) e.getWhoClicked()).getUnclaimed();
                    for (int i : unclaimed) {
                        if (i != 58)
                            toClaim.add(new MilestonesInventory.RewardsItem(i, (Player) e.getWhoClicked()));
                    }
                    Player p = (Player) e.getWhoClicked();
                    p.sendMessage(ChatColor.YELLOW + "Claiming all your milestone rewards...");
                    p.closeInventory();
                    p.playSound(p.getLocation(), Sound.NOTE_STICKS, 10, 1);
                    new BukkitRunnable() {
                        int i = 0;
                        public void run() {
                            if(i < toClaim.size()) {
                                MilestonesInventory.RewardsItem rewardsItem = toClaim.get(i);
                                rewardsItem.claim(p);
                                i++;
                            }else{
                                DesertMain.claiming.remove(p.getUniqueId());
                            }
                        }
                    }.runTaskTimer(DesertMain.getInstance, 15, 25);
                }else{
                    NBTItem nbt = new NBTItem(e.getCurrentItem());
                    String id = NBTUtil.getCustomAttrString(nbt, "ID");
                    Player p = (Player) e.getWhoClicked();
                    if(id.equals("NEXT_PAGE")){
                        int currentPage = nbt.getCompound("CustomAttributes").getInteger("CURRENT_PAGE");
                        p.openInventory(MilestonesInventory.getInventory(p, currentPage + 1));
                    }else if(id.equals("PREVIOUS_PAGE")){
                        int currentPage = nbt.getCompound("CustomAttributes").getInteger("CURRENT_PAGE");
                        p.openInventory(MilestonesInventory.getInventory(p, currentPage - 1));
                    }
                }
            }
        }
    }
}
