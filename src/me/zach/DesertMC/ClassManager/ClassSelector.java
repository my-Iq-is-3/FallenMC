package me.zach.DesertMC.ClassManager;

import java.util.ArrayList;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.Utils.Config.ConfigUtils;
import net.md_5.bungee.api.ChatColor;

public class ClassSelector{
	private Player player = null;
	public ClassSelector(Player player) {
		this.player = player;
	}
	
	public void openInventory() {
		Inventory inventory = DesertMain.getInstance.getServer().createInventory(null, 45, "Class Selector");
		for(int i=0;i<45;i++) {
			ItemStack empty = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
			ItemMeta meta = empty.getItemMeta();
			meta.setDisplayName(" ");
			empty.setItemMeta(meta);
			inventory.setItem(i, empty);
		}
		
		// TANK
		ItemStack tank = new ItemStack(Material.OBSIDIAN);
		ItemMeta tankmeta = tank.getItemMeta();
		tankmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		ArrayList<String> tanklore = new ArrayList<String>();
		
		

		if(ConfigUtils.findClass(player).equals("tank")) {
			tankmeta.setDisplayName(ChatColor.GREEN + "Tank Class");
			tanklore.add(ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "Currently selected class");
		}else {
			tankmeta.setDisplayName(ChatColor.BLUE + "Tank Class");
		}
		tanklore.add("");
		tanklore.add(ChatColor.YELLOW + "Click me to view your progress on the tank class!");
		tankmeta.setLore(tanklore);
		tank.setItemMeta(tankmeta);
		
		// SCOUT
		ItemStack scout = new ItemStack(Material.LEATHER_BOOTS);
		ItemMeta scoutmeta = scout.getItemMeta();
		scoutmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		ArrayList<String> scoutlore = new ArrayList<String>();
		if(ConfigUtils.findClass(player).equals("scout")) {
			scoutmeta.setDisplayName(ChatColor.GREEN + "Scout Class");
			scoutlore.add(ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "Currently selected class");
		} else{
			scoutmeta.setDisplayName(ChatColor.BLUE + "Scout class");
		}
		scoutlore.add("");
		scoutlore.add(ChatColor.YELLOW + "Click me to view your progress on the scout class!");
		scoutmeta.setLore(scoutlore);
		scout.setItemMeta(scoutmeta);
		
		
		// WIZARD
		ItemStack wizard = new ItemStack(Material.LEATHER_HELMET);
		LeatherArmorMeta wizardmeta = (LeatherArmorMeta)wizard.getItemMeta();
		wizardmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		
		wizardmeta.setColor(Color.fromRGB(0, 1, 254));
		ArrayList<String> wizardlore = new ArrayList<String>();
		if(ConfigUtils.findClass(player).equals("wizard")) {
			wizardmeta.setDisplayName(ChatColor.GREEN + "Wizard Class");
			wizardlore.add(ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "Currently selected class");
		}else {
			wizardmeta.setDisplayName(ChatColor.BLUE + "Wizard Class");
		}
		wizardlore.add("");
		wizardlore.add(ChatColor.YELLOW + "Click me to view your progress on the wizard class!");
		wizardmeta.setLore(wizardlore);
		wizard.setItemMeta(wizardmeta);
		
		// CORRUPTER
		ItemStack corrupt = new ItemStack(Material.BLAZE_ROD);
		ItemMeta corruptmeta = corrupt.getItemMeta();
		corruptmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		ArrayList<String> corruptlore = new ArrayList<String>();
		if(ConfigUtils.findClass(player).equals("corrupter")) {
			corruptmeta.setDisplayName(ChatColor.GREEN + "Corrupter Class");
			corruptlore.add(ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "Currently selected class");
		}else {
			corruptmeta.setDisplayName(ChatColor.BLUE + "Corrupter Class");
		}
		corruptlore.add("");
		corruptlore.add(ChatColor.YELLOW + "Click me to view your progress on the corrupter class!");
		corruptmeta.setLore(corruptlore);
		corrupt.setItemMeta(corruptmeta);
		
		/*
		 * 12: tank
		 * 14: scout
		 * 30: wizard
		 * 32: corrupter
		 */
		
		inventory.setItem(12, tank);
		inventory.setItem(14, scout);
		inventory.setItem(30, wizard);
		inventory.setItem(32, corrupt);
		player.openInventory(inventory);
	}
	


}
