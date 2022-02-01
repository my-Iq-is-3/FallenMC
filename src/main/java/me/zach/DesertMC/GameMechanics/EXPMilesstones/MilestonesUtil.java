package me.zach.DesertMC.GameMechanics.EXPMilesstones;

import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.Utils.Config.ConfigUtils;
import me.zach.DesertMC.Utils.MiscUtils;
import me.zach.DesertMC.Utils.StringUtils.StringUtil;
import me.zach.DesertMC.cosmetics.Cosmetic;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.CommandExecute;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class MilestonesUtil implements CommandExecutor {
    private static final List<ChatColor> starColors = Arrays.asList(ChatColor.YELLOW, ChatColor.BLUE, ChatColor.GREEN, ChatColor.LIGHT_PURPLE, ChatColor.DARK_GREEN, ChatColor.AQUA, ChatColor.DARK_AQUA, ChatColor.GOLD);
    public static final HashMap<Integer, Cosmetic> cosmetics = new HashMap<>();
    public static final Set<UUID> confirming = new HashSet<>();
    public static final char STAR = '✪';
    static{
        cosmetics.put(0, Cosmetic.EXPLOSION);
        cosmetics.put(1, Cosmetic.FLAMING_ARROWS);
        cosmetics.put(2, Cosmetic.EVERYTHING);
        cosmetics.put(3, Cosmetic.DEATH_MESSAGES);
        cosmetics.put(4, Cosmetic.MUSICAL_ARROWS);
        cosmetics.put(5, Cosmetic.CUPID_ARROWS);
        cosmetics.put(6, Cosmetic.WATER_ARROWS);
        cosmetics.put(7, Cosmetic.RAINBOW);
    }

    public static String getDisplayCase(Player player){
        MilestonesData data = MilestonesData.get(player);
        return getDisplayCase(data.getResets(), data.getLevel());
    }

    public static String getDisplayCase(int resets, int lv){
        StringBuilder newCase;
        int colorIndex = starColors.size();
        int divResets = Math.floorDiv(resets, 6);
        if(divResets < starColors.size()){
            colorIndex = divResets;
        }
        if(resets % 6 == 0){
            ChatColor color = starColors.get(colorIndex);
            newCase = new StringBuilder(color + String.valueOf(lv));
        }else{
            colorIndex = Math.floorDiv(resets, 6);
            ChatColor color = starColors.get(colorIndex);
            newCase = new StringBuilder(color + "");
            for(int i = 0; i < resets % 6; i++){
                newCase.append(STAR);
            }
            newCase.append(" ").append(lv);
        }
        return newCase.toString();
    }

    public static void resetMilestones(Player player){
        ArrayList<String> msgCompiler = new ArrayList<>();
        msgCompiler.add(ChatColor.GREEN.toString() + ChatColor.BOLD + "MILESTONES RESET!");
        MilestonesData data = MilestonesData.get(player);
        int resets = data.getResets();
        if(!data.getUnclaimed().isEmpty()){
            msgCompiler.add(Arrays.toString(StringUtil.getCenteredMessage(ChatColor.RED + "You still have unclaimed milestones!", ChatColor.RED + "Are you sure you wish to reset?")));
        }else msgCompiler.add(ChatColor.GREEN.toString());
        TextComponent component = new TextComponent(TextComponent.fromLegacyText("Click to confirm your reset."));
        String displayCase = getDisplayCase(player);
        String newCase = getDisplayCase(resets + 1, 0);
        msgCompiler.add(Arrays.toString(StringUtil.getCenteredMessage(ChatColor.GRAY + "Display case upgrade: " + displayCase + ChatColor.DARK_GRAY + " ➞ " + newCase)));
        if(cosmetics.containsKey(resets))
            msgCompiler.add(Arrays.toString(StringUtil.getCenteredMessage(ChatColor.GOLD + "Cosmetic: " + cosmetics.get(resets))));

        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/confirmreset"));
        String yellow = net.md_5.bungee.api.ChatColor.YELLOW.toString();
        String bold = net.md_5.bungee.api.ChatColor.BOLD.toString();
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(yellow + "This will be your " + bold + (resets + 1) + MiscUtils.getOrdinalSuffix(resets + 1) + yellow + " reset.")));
        StringUtil.ChatWrapper wrapper = StringUtil.ChatWrapper.HORIZONTAL_LINE;
        player.sendMessage(wrapper + "\n" + String.join("\n", msgCompiler));
        component.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
        player.spigot().sendMessage(component);
        player.sendMessage("\n" + wrapper);
        confirming.add(player.getUniqueId());
        Bukkit.getScheduler().runTaskLater(DesertMain.getInstance, () -> {
            if(confirming.remove(player.getUniqueId()))
                player.sendMessage(ChatColor.RED + "EXP Milestones reset cancelled.");
        }, 1000);
    }

    private void confirmReset(Player p){
        confirming.remove(p.getUniqueId());
        int resets = MilestonesData.get(p).getResets();
        MilestonesData newData = new MilestonesData(); //resetting data to its initial state
        Cosmetic cosmetic = cosmetics.get(resets); //getting cosmetic
        resets++; //adding reset
        newData.setResets(resets);
        ConfigUtils.getData(p).setMilestonesData(newData);
        p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "EXP MILESTONES RESET!");
        if(cosmetic != null) cosmetic.grant(p);
        MilestonesInventory.RewardsItem.confirmationSound(p);
        refreshExpBar(p);
    }

    public static void refreshExpBar(Player player){
        MilestonesData data = MilestonesData.get(player);
        int level = data.getLevel();
        player.setLevel(level);
        player.setExp(level >= 59 ? 0.99999999f : data.getCurrentProgress() / (float) data.getXpToNext());
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player)) return false;
        Player p = (Player) commandSender;
        if(command.getName().equalsIgnoreCase("confirmreset")){
            if (confirming.contains(p.getUniqueId())) {
                confirmReset(p);
                return true;
            }else{
                p.sendMessage(ChatColor.RED + "This command is situation-specific!");
                return true;
            }
        }else return false;
    }
}
