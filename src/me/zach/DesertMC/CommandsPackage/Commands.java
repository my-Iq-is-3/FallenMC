package me.zach.DesertMC.CommandsPackage;

import de.tr7zw.nbtapi.NBTItem;
import me.zach.DesertMC.ClassManager.KitsOrTraits;
import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.GameMechanics.NPCStructure.NPCSuper;
import me.zach.DesertMC.GameMechanics.SoulShop;
import me.zach.DesertMC.Prefix;
import me.zach.DesertMC.GameMechanics.SPolice;
import me.zach.DesertMC.Utils.Config.ConfigUtils;
import me.zach.DesertMC.GameMechanics.Events;
import me.zach.DesertMC.Utils.TitleUtils;
import me.zach.DesertMC.Utils.nbt.EnchantmentUtil;
import net.minecraft.server.v1_8_R3.CommandExecute;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class Commands extends CommandExecute implements Listener, CommandExecutor, TabCompleter {
	public static HashMap<String, NPCSuper> npcsAndName = new HashMap<>();
	static{
		npcsAndName.put("STREAK_POLICE", SPolice.INSTANCE);
		npcsAndName.put("SOUL_BROKER", SoulShop.INSTANCE);
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
        	Player player = ((Player) sender).getPlayer();
        	DesertMain main = DesertMain.getInstance;
        	Plugin mainpl = DesertMain.getPlugin(DesertMain.class);

        	if(command.getName().equalsIgnoreCase("setspawn")) {
        		if(player.hasPermission("admin")){
        			main.getConfig().set("server.lobbyspawn", player.getLocation());
        			player.sendMessage(ChatColor.BLUE + "Spawn > "+ ChatColor.DARK_GRAY + "Set the spawn for Lobby");
        			main.saveConfig();
        		}else{
        			player.sendMessage(ChatColor.RED + "Only admins can use this command.");
				}
        		
        	}
        	if(command.getName().equalsIgnoreCase("seizehelditem")){
        		if(player.hasPermission("admin")){
        			try{

        				Player target = Bukkit.getServer().getPlayerExact(args[0]);
        				PlayerInventory targetInv = target.getInventory();
        				ItemStack seizedItem = SPolice.seize(targetInv.getItemInHand());
        				int itemSeizeSlot = targetInv.getHeldItemSlot();
        				targetInv.clear(itemSeizeSlot);
        				targetInv.setItem(itemSeizeSlot, seizedItem);
        				return true;
					}catch(NullPointerException ex){
        				player.sendMessage(ChatColor.RED + "You either didn't specify a target player, or the item they were holding wasn't eligible to be seized.");
					}
				}else{
        			player.sendMessage(ChatColor.RED + "Sorry, you can't use this command.");
				}
			}
        	if(command.getName().equalsIgnoreCase("spawnnpc")){
        		player.sendMessage("Command registered.");
        		if(player.hasPermission("admin")){
					if(args[0] != null){
						player.sendMessage("args[0] wasn't null");
						try{
							npcsAndName.get(String.join("_", args).toUpperCase()).createNPC((player.getLocation()));
							player.sendMessage("got npc");
						}catch(NullPointerException nul){
							player.sendMessage(ChatColor.RED + "Sorry, that NPC either doesn't exist of isn't registered.");
						}
					}else{
						player.sendMessage(ChatColor.RED + "Incorrect Usage!");
						return false;
					}
				}else{
        			player.sendMessage(ChatColor.RED + "Sorry, you don't have access to that command.");
				}
			}
        	if(command.getName().equalsIgnoreCase("hideplayer")){
        		if(player.hasPermission("admin")){
        			try{
        				player.hidePlayer(Bukkit.getPlayerExact(args[0]));
        				player.sendMessage(ChatColor.GREEN + "Hid " + args[0] + " from your view.");
					}catch (Exception e){
        				player.sendMessage(ChatColor.RED + "There was an error fetching that player.");
					}
				}else{
        			player.sendMessage(ChatColor.RED + "Only admins can use this command.");
				}
			}


        	if(command.getName().equalsIgnoreCase("enchantmentmod")){
        		if(player.hasPermission("admin")){
        			try{
						player.getInventory().setItemInHand(EnchantmentUtil.getInstance().addEnchantment(args[0],Integer.parseInt(args[1]),player.getInventory().getItemInHand(),player));
					}catch(Exception e){
        				player.sendMessage(ChatColor.RED + "An error occurred. " + e);
        				e.printStackTrace();
					}
				}
			}
        	if(command.getName().equalsIgnoreCase("selecttitle")){
        		try{
        			Prefix p = Prefix.valueOf(args[0].toUpperCase());
					if(TitleUtils.setTitle(player, p)){
						player.sendMessage(ChatColor.GREEN + "Prefix successfully set to \"" + p.toString() + ChatColor.GREEN + "\"");
						return true;
					}else{
						if(player.hasPermission("admin")){
							TitleUtils.addTitle(player, p);
							TitleUtils.setTitle(player, p);
							player.sendMessage(ChatColor.YELLOW + "You didn't have that title so I added it for you and selected it.");
							return true;
						}else player.sendMessage(ChatColor.RED + "Sorry, it seems you don't own that title.");

						return false;
					}
				}catch(Exception e){
        			if(e instanceof IllegalArgumentException){
        				player.sendMessage(ChatColor.RED + "It appears that title doesn't exist.");
        				return false;
        			}
        			else if(e instanceof ArrayIndexOutOfBoundsException){
        				player.sendMessage(ChatColor.RED + "Usage: /title <titletoequip>");
        				return false;
        			}else{
        				e.printStackTrace();
        				return false;
					}

				}
			}

        	if(command.getName().equalsIgnoreCase("showplayer")){
				if(player.hasPermission("admin")){
					try{
						player.showPlayer(Bukkit.getPlayerExact(args[0]));
						player.sendMessage(ChatColor.GREEN + "Showed " + args[0]);
					}catch (Exception e){
						player.sendMessage(ChatColor.RED + "There was an error fetching that player.");
					}
				}else{
					player.sendMessage(ChatColor.RED + "Only admins can use this command.");
				}
			}


        	if(command.getName().equalsIgnoreCase("resetclass")){
        		if(player.hasPermission("admin")){
        			ConfigUtils.resetclass(player,ConfigUtils.findClass(player));
        		}else{
        			player.sendMessage(ChatColor.RED + "Only admins can use this command");
				}
			}


        	if(command.getName().equalsIgnoreCase("classexp")) {
        		if(args[0].equalsIgnoreCase("wizard") || args[0].equalsIgnoreCase("tank") || args[0].equalsIgnoreCase("scout") || args[0].equalsIgnoreCase("corrupter")) {
        			if(player.hasPermission("admin")){
						try {
							int xptoadd = Integer.parseInt(args[1]);
							ConfigUtils.addXP(player, args[0], xptoadd);
							player.sendMessage(ChatColor.GREEN + "Added " + xptoadd + " XP to your " + args[0] + " class!");

						} catch(Exception e) {
							e.printStackTrace();
						}
					}else{
        				player.sendMessage(ChatColor.RED + "Only admins can use this command.");
					}


				}else {
        			player.sendMessage(ChatColor.RED + "Please specify a valid class.");
        		}
        	}

        	if(command.getName().equalsIgnoreCase("debug")){
        		if(player.hasPermission("admin")){
        			if(args.length == 0){
        				player.sendMessage(ChatColor.RED + "Usage: /debug <debugged thing>");
					}else{
        				if(args[0].equalsIgnoreCase("class")){
        					player.sendMessage(Prefix.DEBUG.toString());
        					player.sendMessage(ChatColor.GREEN + "Wizard Level: " + ConfigUtils.getLevel("wizard", player));
							player.sendMessage(ChatColor.GREEN + "Tank Level: " + ConfigUtils.getLevel("tank", player));
							player.sendMessage(ChatColor.GREEN + "Corrupter Level: " + ConfigUtils.getLevel("corrupter", player));
							player.sendMessage(ChatColor.GREEN + "Scout Level: " + ConfigUtils.getLevel("scout", player));

							player.sendMessage(ChatColor.GREEN + "Wizard XP: "  + ConfigUtils.getXP(player, "wizard"));
							player.sendMessage(ChatColor.GREEN + "Tank XP: "  + ConfigUtils.getXP(player, "tank"));
							player.sendMessage(ChatColor.GREEN + "Corrupter XP: "  + ConfigUtils.getXP(player, "corrupter"));
							player.sendMessage(ChatColor.GREEN + "Scout XP: "  + ConfigUtils.getXP(player, "scout"));
        				}
						if(args[0].equalsIgnoreCase("killstreak")){
							player.sendMessage(ChatColor.GREEN + "Current killstreak: " + ChatColor.RED + Events.ks.get(player.getUniqueId()));
						}
						if(args[0].equalsIgnoreCase("NBT")){
							if(player.getInventory().getItemInHand().getType().equals(Material.AIR)){
								player.sendMessage(ChatColor.RED + "Please hold an item.");
							}else{
								ItemStack helditem = player.getInventory().getItemInHand();
								NBTItem nbti = new NBTItem(helditem);
								player.sendMessage(nbti.toString());
							}

						}
        			}
				}else{
        			player.sendMessage(ChatColor.RED + "Only admins can use this command.");
				}
			}
        	
        	if(command.getName().equalsIgnoreCase("setks")){
        		if(!player.hasPermission("admin")){
        			player.sendMessage(ChatColor.RED + "Only admins can use this command.");
        			return false;
				}
        		try{
        			if(args.length == 0){
        				player.sendMessage(ChatColor.RED + "Usage: /setks <value>");
						return false;
					}
					Events.ks.put(player.getUniqueId(), Integer.parseInt(args[0]));

				}catch (NumberFormatException e){
        			player.sendMessage(ChatColor.RED + "Please enter a valid number.");
				}
			}

        	if(command.getName().equalsIgnoreCase("kot")) {
        		if(player.hasPermission("admin")){
					KitsOrTraits kot = new KitsOrTraits(player);
					kot.openInventory();

				}else{
        			player.sendMessage(ChatColor.RED + "Only admins can use this command.");
				}
        	}
        	

        	if(command.getName().equalsIgnoreCase("speed")) {
        		if(args.length == 1) {
	        		if(player.hasPermission("admin")) {
	        			if(args[0].equalsIgnoreCase("reset")) {
		        			player.setWalkSpeed(0.2f);
		        			player.setFlySpeed(0.125f);
		        			player.sendMessage(Prefix.SERVER +  " Reset your fly and walk speed!");
		        			return true;
		        		}
		        		try {
		        			
		        			if(player.isFlying()) {
		        				player.setFlySpeed(Float.parseFloat(args[0]) / 10);
		        				player.sendMessage(ChatColor.RED + "[SERVER] Set your fly speed to " + ChatColor.RED + args[0]);
		        			} else if(!player.isFlying()) {
		        				player.setWalkSpeed(Float.parseFloat(args[0]) / 10);
		        				player.sendMessage(ChatColor.RED + "[SERVER] Set your walk speed to " + ChatColor.RED + args[0]);
		        			}
						} catch (IllegalArgumentException e) {
							player.sendMessage(ChatColor.RED + "[SERVER] " + ChatColor.RED + "Invalid number \"" + args[0] + "\"! Must be from" + ChatColor.GREEN + " 1 through 10" + ChatColor.RED + "!");
						}
	        		} else {
	        			player.sendMessage(ChatColor.RED + "Only admins can use this command.");
	        		}
        		}
				

        	
        	}
        	if(command.getName().equalsIgnoreCase("invincible")) {
        		if(player.hasPermission("admin")) {
	        			if(!main.getConfig().getBoolean("players." + player.getUniqueId() + ".invincible")) {
		        			player.sendMessage(ChatColor.GREEN + "Made you invincible!");
		        			main.getConfig().set("players." + player.getUniqueId() + ".invincible", true);
						} else {
		        			main.getConfig().set("players." + player.getUniqueId() + ".invincible", false);
		        			player.sendMessage(ChatColor.RED + "Turned off your invincibilty!");
						}
					main.saveConfig();
				} else {
        			player.sendMessage(ChatColor.RED + "Only admins can use this command.");
        		}
        		
        	}
            
        }
		return true;
    }
	@Override
	public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
		List<String> args = new ArrayList<String>();
		if (strings.length == 1) {

			if (commandSender.hasPermission("admin") && command.getName().equalsIgnoreCase("spawnnpc")) {
				args = Arrays.asList("SOUL_BROKER", "STREAK_POLICE");
			}
		}
		return args;
	}
}
