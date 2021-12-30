package me.zach.DesertMC.GameMechanics.EXPMilesstones;

import itempackage.Items;
import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.Utils.Config.ConfigUtils;
import me.zach.DesertMC.Utils.MiscUtils;
import me.zach.DesertMC.Utils.StringUtils.StringUtil;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.command.CraftConsoleCommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static me.zach.DesertMC.DesertMain.lv;
import static me.zach.DesertMC.DesertMain.unclaimed;

public class MilestonesInventory implements Listener {
    static Plugin pl = DesertMain.getInstance;
    static ItemStack empty = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
    static{
        ItemMeta emptyMeta = empty.getItemMeta();
        emptyMeta.setDisplayName(" ");
        empty.setItemMeta(emptyMeta);
    }
    public static Inventory getInventory(Player player){
        int[] slots = {0, 9, 18, 27, 36, 37, 38, 29, 20, 11, 2, 3, 4, 13, 22, 31, 40, 41, 42, 33, 24, 15, 6, 7, 8, 17, 26, 35, 44};
        Inventory inv = pl.getServer().createInventory(null, 54, "EXP Milestones");
        for(int i = 0; i<inv.getSize(); i++){
            inv.setItem(i, empty);
        }
        for(int i = 0; i<slots.length; i++){
            inv.setItem(slots[i], rewardsList(player, 0).get(i));
        }
        ItemStack claimItem = new ItemStack(Material.BREWING_STAND_ITEM);
        ItemMeta claimMeta = claimItem.getItemMeta();
        if(unclaimed.isEmpty()){
            claimMeta.setDisplayName(ChatColor.RED + "Claim All Milestones");
            claimMeta.setLore(Collections.singletonList(ChatColor.RED + "You have no rewards to claim!"));
        }else{
            claimMeta.setDisplayName(ChatColor.GREEN + "Claim All Milestones");
            claimMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
            claimMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
            claimMeta.setLore(Arrays.asList(ChatColor.GREEN + "Click here to claim all", ChatColor.GREEN + "available rewards!"));
        }
        claimItem.setItemMeta(claimMeta);
        inv.setItem(49, claimItem);
        return inv;
    }

    /**
     *
     * @param p Target player
     * @param page Page to open on. Set to 0 for default.
     * @return returns a list of display items to be used in the milestones inventory
     */
    private static ArrayList<ItemStack> rewardsList(Player p, int page){
        ArrayList<ItemStack> list = new ArrayList<>();
        if(page == 0){
            page = Math.floorDiv(lv - 1, 29) + 1;
        }else if(page < 0) throw new IllegalArgumentException("Page passed through rewardsList(Player p, int page) must be greater than or equal to 0!");
        if(page > 2) page = 2;
        for(int i = (page * 29) - 29; i<=page * 29; i++){
            list.add(new RewardsItem(i + 1, p));
        }
        return list;
    }
    protected static class RewardsItem extends ItemStack{
        private static final HashMap<Integer, RewardOverride> overrides = new HashMap<>();
        public static RewardsItem parseLevel(ItemStack item, Player p){
            return new RewardsItem(Integer.parseInt(item.getItemMeta().getDisplayName().replaceAll(ChatColor.GREEN + "Milestone ", "")), p);
        }

        private static short getColor(int level, int playerLevel){
            if(playerLevel > level) return (short) 5;
            else if(playerLevel == level) return (short) 4;
            else if(overrides.containsKey(level)) return (short) 0;
            else return (short) 14;
        }
        private static Material type(Integer milestoneLevel){
            if(overrides.containsKey(milestoneLevel)) return overrides.get(milestoneLevel).icon;
            else return Material.STAINED_GLASS_PANE;
        }

        boolean override;
        Integer level;
        IRewardGrant granter;
        String reward;
        public RewardsItem(int milestoneLevel, Player p){
            super(type(milestoneLevel), 1);
            int playerLevel = DesertMain.lv;
            override = overrides.containsKey(level);
            level = milestoneLevel;
            ChatColor textColor = ChatColor.RED;
            String displayName;
            ArrayList<String> lore = new ArrayList<>();
            lore.add(ChatColor.RED + "Error!");
            if(playerLevel > milestoneLevel){
                textColor = ChatColor.GREEN;
            }else if(playerLevel == milestoneLevel){
                textColor = ChatColor.YELLOW;
            }

            displayName = textColor + "Milestone " + milestoneLevel;
            if(milestoneLevel == 58){
                reward = (DesertMain.resets + 1) + MiscUtils.getOrdinalSuffix(DesertMain.resets + 1) + " milestones reset";
                lore.add(ChatColor.GRAY + "- Display case upgrade: " + MilestonesUtil.getDisplayCase(p) + ChatColor.GRAY + " ➞ " + MilestonesUtil.getDisplayCase(DesertMain.resets + 1, 0));
                if(MilestonesUtil.cosmetics.containsKey(DesertMain.resets)) lore.add(ChatColor.GRAY + "- New cosmetic: " + MilestonesUtil.cosmetics.get(DesertMain.resets));
                granter = (player, mLevel) -> false;
            }else{
                if (overrides.containsKey(level)) {
                    RewardOverride override = overrides.get(level);
                    reward = override.name;
                    granter = override.granter;
                } else {
                    switch (milestoneLevel % 5) {
                        case (0):
                            int gemsToGrant = 100 * (milestoneLevel / 5);
                            reward = gemsToGrant + " Gems";
                            granter = (player, mLevel) -> {
                                ConfigUtils.addGems(player, gemsToGrant);
                                return true;
                            };
                            break;
                        case (1):
                            int soulsToGrant = 7 * (Math.floorDiv(milestoneLevel, 6) + 1);
                            reward = soulsToGrant + " Souls";
                            granter = (player, mLevel) -> {
                                ConfigUtils.addSouls(player, soulsToGrant);
                                return true;
                            };
                            break;
                        case (2):
                            reward = "2 Trait Tokens";
                            Plugin traitsPl = Bukkit.getPluginManager().getPlugin("Traits");
                            granter = (player, mLevel) -> {
                                traitsPl.getConfig().set(player.getUniqueId() + ".traittokens", traitsPl.getConfig().getInt(player.getUniqueId() + ".traittokens" + 2));
                                traitsPl.saveConfig();
                                return true;
                            };
                            break;
                        case (3):
                            float toBoost = 1 + ((Math.floorDiv(milestoneLevel, 8) + 1) * 0.1f);
                            if (toBoost > 2f) toBoost = 2f;
                            reward = toBoost + "x EXP Multiplier";
                            lore.add(ChatColor.GRAY + "- An EXP multiplier that lasts for 1h.");
                            float finalToBoost = toBoost;
                            granter = (player, mLevel) -> {
                                if (DesertMain.booster.containsKey(player.getUniqueId())) {
                                    player.sendMessage(ChatColor.RED + "You already have a booster active!");
                                    return false;
                                }
                                DesertMain.booster.put(player.getUniqueId(), finalToBoost);
                                return true;
                            };
                            break;
                        case (4):
                            int hammer = 1;
                            if (milestoneLevel >= 15) hammer++;
                            if (milestoneLevel >= 35) hammer++;
                            if (hammer == 1) {
                                reward = "Wood Hammer";
                                granter = (player, mLevel) -> {
                                    if (player.getInventory().firstEmpty() == -1) {
                                        player.sendMessage(ChatColor.RED + "Full Inventory!");
                                        return false;
                                    } else {
                                        player.getInventory().addItem(Items.getWoodHammer());

                                        return true;
                                    }
                                };
                            } else if (hammer == 2) {
                                reward = "Stone Hammer";
                                granter = (player, mLevel) -> {
                                    if (player.getInventory().firstEmpty() == -1) {
                                        player.sendMessage(ChatColor.RED + "Full Inventory!");
                                        return false;
                                    } else {
                                        player.getInventory().addItem(Items.getStoneHammer());
                                        return true;
                                    }
                                };
                            } else {
                                reward = "Iron Hammer";
                                granter = (player, mLevel) -> {
                                    if (player.getInventory().firstEmpty() == -1) {
                                        player.sendMessage(ChatColor.RED + "Full Inventory!");
                                        return false;
                                    } else {
                                        player.getInventory().addItem(Items.getIronHammer());
                                        return true;
                                    }
                                };
                            }

                    }
                }
            }
            if(getType().equals(Material.STAINED_GLASS_PANE)) setDurability(getColor(milestoneLevel, playerLevel));
            reward = textColor + reward;
            lore.set(0, reward);
            if(playerLevel > milestoneLevel) {
                lore.add(textColor + "Progress: Complete!");
            }else if(playerLevel == milestoneLevel) lore.add(textColor + "Progress: " + DesertMain.currentProgress + "/" + DesertMain.xpToNext);
            ItemMeta meta = getItemMeta();
            if(DesertMain.unclaimed.contains(milestoneLevel)){
                if(getType() != Material.SKULL_ITEM) meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                lore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "Click to claim!");
            }
            meta.setDisplayName(displayName);
            meta.setLore(lore);
            setItemMeta(meta);
        }

        public void claim(Player player){
            player.closeInventory();
            if(level == 58){
                MilestonesUtil.resetMilestones(player);
            }else{
                if(granter.grant(player, level)) {
                    unclaimed.remove(level);
                    StringUtil.sendCenteredWrappedMessage(player, StringUtil.ChatWrapper.HORIZONTAL_LINE, ChatColor.GREEN.toString() + ChatColor.BOLD + "REWARD CLAIMED!", ChatColor.GREEN + "You got: " + ChatColor.YELLOW + reward);
                    confirmationSound(player);
                }else{
                    player.playSound(player.getLocation(), Sound.NOTE_BASS, 10, 1);
                }
            }
        }

        public static void confirmationSound(Player player){
            new BukkitRunnable(){
                @Override
                public void run() {
                    player.playNote(player.getLocation(), Instrument.PIANO, Note.natural(0, Note.Tone.F));
                }
            }.runTaskLater(pl, 5);
            new BukkitRunnable(){
                @Override
                public void run() {
                    player.playNote(player.getLocation(), Instrument.PIANO, Note.natural(0, Note.Tone.G));
                }
            }.runTaskLater(pl, 5);
            new BukkitRunnable(){
                @Override
                public void run() {
                    player.playNote(player.getLocation(), Instrument.PIANO, Note.sharp(1, Note.Tone.A));
                    player.playNote(player.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.D));
                    player.playNote(player.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.F));
                }
            }.runTaskLater(pl, 9);
            new BukkitRunnable(){
                @Override
                public void run() {
                    player.playNote(player.getLocation(), Instrument.PIANO, Note.sharp(1, Note.Tone.A));
                    player.playNote(player.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.D));
                    player.playNote(player.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.F));
                }
            }.runTaskLater(pl, 7);
        }

        protected static class RewardOverride{
            public final String name;
            public final Integer level;
            public final IRewardGrant granter;
            public final Material icon;
            protected RewardOverride(String milestoneName, Integer milestoneLevel, IRewardGrant milestoneGranter, Material milestoneIcon){
                if(milestoneLevel <= 0) throw new IllegalArgumentException(ChatColor.RED + "Hey! You can't override a milestone with a level less than or equal to 0!");
                name = milestoneName;
                level = milestoneLevel;
                granter = milestoneGranter;
                icon = milestoneIcon;
            }
        }


        protected static void addOverride(RewardOverride override){
            overrides.put(override.level, override);
        }
        /**
         * @param override RewardOverride item to override reward with
         * @param mod How often this reward should occurr.
         */
        protected static void addOverride(RewardOverride override, int mod){
            for(int i = override.level; i <= 58; i++){
                if(i % mod == 0 || i == override.level){
                    addOverride(new RewardOverride(override.name, i, override.granter, override.icon));
                }
            }
        }
        protected interface IRewardGrant{
            boolean grant(Player player, int mLevel);
        }
    }

}
