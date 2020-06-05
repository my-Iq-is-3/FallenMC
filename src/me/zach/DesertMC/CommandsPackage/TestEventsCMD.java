package me.zach.DesertMC.CommandsPackage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import net.minecraft.server.v1_9_R1.CommandExecute;

public class TestEventsCMD extends CommandExecute implements CommandExecutor,Listener {

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(player.isOp()) {
				if(args[0].equalsIgnoreCase("entitydamagebyentityevent")) {
					try {
						Player damager = Bukkit.getPlayer(args[1]);
						Player damagee = Bukkit.getPlayer(args[2]);
						if(args[3].equalsIgnoreCase("custom")) {
							double damage = Double.parseDouble(args[4]);
							Bukkit.getPluginManager().callEvent(new EntityDamageByEntityEvent(damager,damagee,DamageCause.CUSTOM,damage));
						}else {
							player.sendMessage(ChatColor.RED + "Please only use 'custom' as the damage cause. This command is a work-in-progress.");
						}
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
			}else{
				player.sendMessage(ChatColor.RED + "Only admins can use this command.");
			}

		}
				
		
		return false;
	}

}
