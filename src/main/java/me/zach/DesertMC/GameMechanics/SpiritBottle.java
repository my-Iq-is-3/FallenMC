package me.zach.DesertMC.GameMechanics;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import me.zach.DesertMC.GameMechanics.hitbox.HitboxListener;
import me.zach.DesertMC.Utils.MiscUtils;
import me.zach.DesertMC.Utils.StringUtils.StringUtil;
import me.zach.DesertMC.Utils.nbt.NBTUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.fallenmc.risenboss.main.utils.RisenUtils;

public class SpiritBottle implements Listener, CommandExecutor {
    @EventHandler
    public void interact(PlayerInteractEvent event){
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if(NBTUtil.getCustomAttrString(item, "ID").equals("SPIRIT_BOTTLE")){
            if(RisenUtils.isBoss(player.getUniqueId()) || HitboxListener.isInSafeZone(player.getLocation())){
                player.sendMessage(ChatColor.RED + "You can't use this right now!");
                return;
            }
            StringUtil.sendCenteredMessage(player,StringUtil.ChatWrapper.THICK_HORIZONTAL_LINE.toString(), ChatColor.AQUA + "If you do this, your current killstreak and location", ChatColor.AQUA + "will be saved in the bottle you're holding.", ChatColor.AQUA + "When someone drinks it later, it will restore", ChatColor.AQUA + "them to that state!","",
                    ChatColor.RED + "NOTE: Doing this will respawn you immediately.", ChatColor.RED + "Click the button to confirm.", "");
            TextComponent component = new TextComponent(StringUtil.getCenteredLine(ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "[BOTTLE ME]"));
            component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bottleme"));
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(ChatColor.LIGHT_PURPLE + "Bottles your killstreak of " + ChatColor.BOLD + Events.ks.get(player.getUniqueId()) + ChatColor.LIGHT_PURPLE + " with location " + MiscUtils.cleanCoordinates(player.getLocation()))}));
            player.sendMessage(component, new TextComponent("\n"));

        }
    }

    @EventHandler
    public void spiritBottleDrink(PlayerItemConsumeEvent event){
        ItemStack item = event.getItem();
        if(item == null) return;
        NBTItem nbt = new NBTItem(item);
        if(!nbt.hasKey("CustomAttributes")) return;
        NBTCompound customAttr = nbt.getCompound("CustomAttributes");
        String id = customAttr.getString("ID");
        if(id == null) return;
        if(id.equals("FILLED_SPIRIT_BOTTLE")){
            Player player = event.getPlayer();
            if(RisenUtils.isBoss(player.getUniqueId())){
                player.sendMessage(ChatColor.RED + "You can't use this right now!");
                event.setCancelled(true);
                return;
            }
            World world = player.getWorld();
            if(world.getUID().toString().equals(customAttr.getString("WORLD"))){
                int killstreak = customAttr.getInteger("KILLSTREAK");
                Events.ks.put(player.getUniqueId(), killstreak);
                Location location = player.getLocation().clone();
                location.setX(customAttr.getDouble("XLOC"));
                location.setY(customAttr.getDouble("YLOC"));
                location.setZ(customAttr.getDouble("ZLOC"));
                player.teleport(location);
                player.sendMessage(ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + " YOU DRANK A SPIRIT BOTTLE!\n" + ChatColor.YELLOW + " Your soul was whisked away to a previous state:\n" + ChatColor.YELLOW + "   Killstreak: " + ChatColor.BOLD + killstreak + "\n" + ChatColor.YELLOW + "   Location: " + MiscUtils.cleanCoordinates(location));
                player.playSound(player.getLocation(), Sound.WITHER_IDLE, 10, 1);
                event.setCancelled(true);
                player.setItemInHand(null);
            }else{
                player.sendMessage(ChatColor.RED + "The Spirit Bottle does a heck of a lot, but it can't transport you over multiple realities." + ChatColor.GRAY + "\nTry again in the correct world.");
                event.setCancelled(true);
            }
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(command.getName().equalsIgnoreCase("bottleme")){
            if(sender instanceof Player){
                Player player = (Player) sender;
                ItemStack item = player.getItemInHand();
                if(NBTUtil.getCustomAttrString(item, "ID").equals("SPIRIT_BOTTLE")){
                    int ks = Events.ks.get(player.getUniqueId());
                    Location location = player.getLocation();
                    ItemStack filled = MiscUtils.generateItem(Material.POTION, ChatColor.LIGHT_PURPLE + "Spirit Bottle", StringUtil.wrapLore("§7Drink this bottle to whisk your player's soul magically to another state!\n\nKillstreak: " + ChatColor.RED + ks + "\n" + ChatColor.GRAY + "Location: " + ChatColor.YELLOW + MiscUtils.cleanCoordinates(location) + "\n" + ChatColor.DARK_GRAY + "Captured by " + player.getName(), 35), (byte) 8193, 1, "FILLED_SPIRIT_BOTTLE");
                    ItemMeta meta = filled.getItemMeta();
                    meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                    filled.setItemMeta(meta);
                    NBTItem nbt = new NBTItem(filled);
                    NBTCompound attributes = NBTUtil.checkCustomAttr(nbt);
                    attributes.setDouble("XLOC", location.getX());
                    attributes.setDouble("YLOC", location.getY());
                    attributes.setDouble("ZLOC", location.getZ());
                    attributes.setString("WORLD", location.getWorld().getUID().toString());
                    attributes.setInteger("KILLSTREAK", ks);
                    attributes.setBoolean("USABLE", true);
                    player.setItemInHand(nbt.getItem());
                    Events.respawn(player);
                    player.sendTitle(ChatColor.LIGHT_PURPLE + "SPIRIT BOTTLE'D", ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + ks + ChatColor.LIGHT_PURPLE + " at location " + MiscUtils.cleanCoordinates(location));
                    MiscUtils.playPianoMelody(player, "DFA    DFA      )FC     GD(      DFA   DFA   DFA");
                }else{
                    player.sendMessage(ChatColor.RED + "Sorry, this command is situation-specific.\n" + ChatColor.DARK_GRAY + "Maybe you switched items?");
                }
            }else sender.sendMessage("Only players can use this command.");
            return true;
        }else return false;
    }
}
