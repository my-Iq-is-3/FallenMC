package me.gabriel.Traits;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import net.md_5.bungee.api.ChatColor;

public class Commands implements Listener, CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;
		Plugin plugin = TraitsMain.getPlugin(TraitsMain.class);

		if (cmd.getName().equalsIgnoreCase("traits") && player.hasPermission("admin")) {

			TraitsInventory.openTraitInventory(player);
		}


		if (cmd.getName().equalsIgnoreCase("traitsconfig") && player.hasPermission("admin")) {
			if (args.length != 4) {
				player.sendMessage(ChatColor.RED
						+ "Invalid usage! Usage: /config <get/set> <type> <path> <value> (THE ONLY TYPES PERMITTED ARE int, boolean, and String. Also, if you are trying to get a value you can put whatever you want for <value>.)");
			} else {
				args[2].replaceAll("myUUID.", player.getUniqueId().toString() + ".");

				if (args[0].equalsIgnoreCase("get")) {
					if (plugin.getConfig().get(args[2]) != null) {
						if (args[1].equalsIgnoreCase("int")) {
							player.sendMessage(ChatColor.GREEN + "Config path  \"" + ChatColor.BLUE + args[2]
									+ ChatColor.GREEN + "\" returned successfully. Value: " + ChatColor.BLUE
									+ plugin.getConfig().getInt(args[2]));
						} else if (args[1].equalsIgnoreCase("boolean")) {
							player.sendMessage(ChatColor.GREEN + "Config path  \"" + ChatColor.BLUE + args[2]
									+ ChatColor.GREEN + "\" returned successfully. Value: " + ChatColor.BLUE
									+ plugin.getConfig().getBoolean(args[2]));
						} else if (args[1].equalsIgnoreCase("string")) {
							player.sendMessage(ChatColor.GREEN + "Config path  \"" + ChatColor.BLUE + args[2]
									+ ChatColor.GREEN + "\" returned successfully. Value: " + ChatColor.BLUE
									+ plugin.getConfig().getString(args[2]));

						} else {
							player.sendMessage(ChatColor.RED
									+ "Invalid usage! Usage: /config <get/set> <type> <path> <value> (THE ONLY TYPES PERMITTED ARE int, boolean, and String. Also, if you are trying to get a value you can put whatever you want for <value>.)");
						}
					} else {
						player.sendMessage(ChatColor.RED + "uhh hey maybe try again with a path that isn't null??");
					}
				} else if (args[0].equalsIgnoreCase("set")) {
					if (args[1].equalsIgnoreCase("int")) {
						
						plugin.getConfig().set(args[2], Integer.parseInt(args[3]));
						player.sendMessage(ChatColor.GREEN + "Value set successfully.");
						plugin.saveConfig();
					} else if (args[1].equalsIgnoreCase("boolean")) {
						plugin.getConfig().set(args[2], Boolean.parseBoolean(args[3]));
						player.sendMessage(ChatColor.GREEN + "Value set successfully.");
						plugin.saveConfig();

					} else if (args[1].equalsIgnoreCase("string")) {
						plugin.getConfig().set(args[2], args[3]);
						player.sendMessage(ChatColor.GREEN + "Value set successfully.");
						plugin.saveConfig();

					} else {
						player.sendMessage(ChatColor.RED
								+ "Invalid usage! Usage: /config <get/set> <type> <path> <value> (THE ONLY TYPES PERMITTED ARE int, boolean, and String. Also, if you are trying to get a value you can put whatever you want for <value>.)");
					}
				} else {
					player.sendMessage(ChatColor.RED
							+ "Invalid usage! Usage: /config <get/set> <path> <type> <value> (THE ONLY TYPES PERMITTED ARE int, boolean, and String. Also, if you are trying to get a value you can put whatever you want for <type>.)");
				}
			}
		}
		return true;
	}

}
