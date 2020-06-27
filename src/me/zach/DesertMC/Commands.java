package me.zach.DesertMC;

import de.tr7zw.nbtapi.NBTItem;
import me.zach.DesertMC.GUImanager.KitsOrTraits;
import me.zach.DesertMC.Utils.Config.ConfigUtils;
import me.zach.DesertMC.GameMechanics.ClassEvents.PlayerManager.Events;
import me.zach.DesertMC.Utils.nbt.EnchantmentUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;


public class Commands extends net.minecraft.server.v1_9_R1.CommandExecute implements Listener, CommandExecutor{
	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
        	Player player = ((Player) sender).getPlayer();
        	DesertMain main = DesertMain.getInstance;
        	Plugin mainpl = DesertMain.getPlugin(DesertMain.class);
        	if(command.getName().equalsIgnoreCase("setspawn")) {
        		if(player.hasPermission("admin")) {
        			main.getConfig().set("server.lobbyspawn", player.getLocation());
        			player.sendMessage(ChatColor.BLUE + "Spawn > "+ ChatColor.DARK_GRAY + "Set the spawn for Lobby");
        			main.saveConfig();
        		}else{
        			player.sendMessage(ChatColor.RED + "Only admins can use this command.");
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
						player.getInventory().setItemInMainHand(EnchantmentUtil.getInstance().addEnchantment(args[0],Integer.parseInt(args[1]),player.getInventory().getItemInMainHand(),player));
					}catch(Exception e){
        				player.sendMessage(ChatColor.RED + "An error occurred. " + e);
        				e.printStackTrace();
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
							if(player.getInventory().getItemInMainHand().getType().equals(Material.AIR)){
								player.sendMessage(ChatColor.RED + "Please hold an item.");
							}else{
								ItemStack helditem = player.getInventory().getItemInMainHand();
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
}
