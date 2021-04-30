package me.zach.DesertMC.GameMechanics.EXPMilesstones;

import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.Utils.MiscUtils;
import me.zach.DesertMC.cosmetics.Cosmetic;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.CommandExecute;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class MilestonesUtil extends CommandExecute implements CommandExecutor {
    private static final Plugin pl = DesertMain.getInstance;
    private static final ArrayList<ChatColor> starColors = new ArrayList<>();
    public static final HashMap<Integer, Cosmetic> cosmetics = new HashMap<>();
    public static final HashMap<UUID, String> confirming = new HashMap<>();
    public static final char STAR = '✪';
    static{
        starColors.addAll(Arrays.asList(ChatColor.YELLOW, ChatColor.BLUE, ChatColor.GREEN, ChatColor.LIGHT_PURPLE, ChatColor.DARK_GREEN, ChatColor.AQUA, ChatColor.DARK_AQUA, ChatColor.GOLD));
        cosmetics.put(0, Cosmetic.EXPLOSION);
    }

    public static String getNewCase(Player player){
        String displayCase = getDisplayCase(player);
        StringBuilder newCase = new StringBuilder();
        if(displayCase == null){
            newCase = new StringBuilder(starColors.get(0) + "0");
        }else{
            if((DesertMain.resets + 1) % 5 == 0){
                int colorIndex = starColors.size();
                try{
                    colorIndex = Math.floorDiv(DesertMain.resets, 5);
                }catch(IndexOutOfBoundsException ignored){}
                ChatColor color = starColors.get(colorIndex);
                newCase = new StringBuilder(color + "0");
            }else{
                int colorIndex = starColors.size();
                try{
                    colorIndex = Math.floorDiv(DesertMain.resets, 5);
                }catch(IndexOutOfBoundsException ignored){}
                ChatColor color = starColors.get(colorIndex);
                newCase = new StringBuilder(color + "");

                for(int i = 0; i < DesertMain.resets; i++){
                    newCase.append(STAR);
                }
                newCase.append(" 0");
            }
        }
        return newCase.toString();
    }

    public static String getDisplayCase(Player player){
        return pl.getConfig().getString("players." + player.getUniqueId() + ".displaycase");
    }

    public static void resetMilestones(Player player){
        int resets = DesertMain.resets;
        if(!DesertMain.unclaimed.isEmpty()){
            player.sendMessage(ChatColor.RED + "You still have unclaimed milestones! Are you sure you wish to reset?");
        }else player.sendMessage(ChatColor.GREEN + "You are resetting your milestones. This will be your " + (resets + 1) + MiscUtils.getOrdinalSuffix(resets + 1) + " reset.");
        TextComponent component = new TextComponent(TextComponent.fromLegacyText(ChatColor.YELLOW + "Click to confirm your reset."));
        String displayCase = getDisplayCase(player);
        String newCase = getNewCase(player);
        player.sendMessage(ChatColor.GRAY + "Display case upgrade: " + displayCase + ChatColor.DARK_GRAY + " ➞ " + newCase);
        if(cosmetics.containsKey(resets))
            player.sendMessage(ChatColor.GOLD + "Cosmetic: " + cosmetics.get(resets));

        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/confirmreset"));
        player.spigot().sendMessage(component);
        confirming.put(player.getUniqueId(), newCase);
        new BukkitRunnable(){
            public void run(){
                confirming.remove(player.getUniqueId());
                player.sendMessage(ChatColor.RED + "EXP Milestones reset cancelled.");
            }
        }.runTaskLater(pl, 600);
    }

    private void confirmReset(Player p){
        String newCase = confirming.get(p.getUniqueId());
        confirming.remove(p.getUniqueId());
        DesertMain.lv = 1;
        DesertMain.currentProgress = 0;
        DesertMain.xpToNext = 200;
        DesertMain.unclaimed.clear();
        p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "EXP MILESTONES RESET!");
         setDisplayCase(newCase, p);
         try{
             cosmetics.get(DesertMain.resets).grant(p);
         }catch(NullPointerException ignored){}
        DesertMain.resets++;
        MilestonesInventory.RewardsItem.confirmationSound(p);
    }

    public static void setDisplayCase(String toSet, Player player){
        pl.getConfig().set("players." + player.getUniqueId() + ".displaycase", toSet);
        pl.saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player)) return false;
        Player p = (Player) commandSender;
        if(command.getName().equalsIgnoreCase("confirmreset")){
            if (confirming.containsKey(p.getUniqueId())) {
                confirmReset(p);
                return true;
            }else{
                p.sendMessage(ChatColor.RED + "This command is situation-specific!");
                return true;
            }
        }else return false;

    }


}
