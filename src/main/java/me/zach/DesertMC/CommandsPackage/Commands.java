package me.zach.DesertMC.CommandsPackage;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import me.zach.DesertMC.ClassManager.KothyMenu;
import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.GameMechanics.EXPMilesstones.MilestonesInventory;
import me.zach.DesertMC.GameMechanics.NPCStructure.NPCSuper;
import me.zach.DesertMC.GameMechanics.hitbox.BoxHitbox;
import me.zach.DesertMC.GameMechanics.hitbox.CircleHitbox;
import me.zach.DesertMC.GameMechanics.hitbox.HitboxManager;
import me.zach.DesertMC.Prefix;
import me.zach.DesertMC.GameMechanics.npcs.StreakPolice;
import me.zach.DesertMC.Utils.Config.ConfigUtils;
import me.zach.DesertMC.GameMechanics.Events;
import me.zach.DesertMC.Utils.MiscUtils;
import me.zach.DesertMC.Utils.RankUtils.Rank;
import me.zach.DesertMC.Utils.RankUtils.RankEvents;
import me.zach.DesertMC.Utils.StringUtils.StringUtil;
import me.zach.DesertMC.Utils.TitleUtils;
import me.zach.DesertMC.Utils.ench.CustomEnch;
import me.zach.DesertMC.shops.ShopInventory;
import me.zach.DesertMC.shops.ShopItem;
import me.zach.DesertMC.cosmetics.Cosmetic;
import net.minecraft.server.v1_8_R3.CommandExecute;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;


public class Commands extends CommandExecute implements Listener, CommandExecutor {
	public static HashMap<UUID, Location> hitboxAwait = new HashMap<>();
	private static final String[] colorsMessage;
	static{
		ArrayList<String> colorsList = new ArrayList<>();
		colorsList.add(ChatColor.GREEN + "With your rank, you can"  + ChatColor.YELLOW + " include " + ChatColor.AQUA + "colors " + ChatColor.GREEN + "in your messages!");
		colorsList.add("Placing a color code in your messages will make any text after that color code your color!");
		colorsList.add(ChatColor.GRAY + "Colors:");
		for(String name : RankEvents.friendlyCC.keySet()){
			ChatColor color = RankEvents.friendlyCC.get(name);
			String colorName = color.name();
			colorsList.add(name + ChatColor.DARK_GRAY + " - " + ChatColor.WHITE + colorName + " or !" + RankEvents.colorShortcuts.get(color));
		}
		colorsMessage = StringUtil.getCenteredWrappedMessage(new StringUtil.ChatWrapper('-', ChatColor.WHITE, true, true), colorsList.toArray(new String[0]));
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
        	Player player = ((Player) sender);
        	Plugin mainpl = DesertMain.getInstance;
			if(command.getName().equalsIgnoreCase("shoptest")){
				if(args.length > 0){
					ShopItem[] items = new ShopItem[args.length];
					for(int i = 0, price = 200; i < args.length; i++, price += 200){
						String str = args[i];
						try{
							Material material = Material.valueOf(str.toUpperCase());
							ShopItem item = new ShopItem(price) {
								protected ItemStack get(){
									return MiscUtils.generateItem(material, "", StringUtil.wrapLore(ChatColor.GRAY + "A regular ol' item. Hopefully " + player.getName() + " picked something snazzy!"), (byte) -1, 1);
								}
							};
							items[i] = item;
						}catch(IllegalArgumentException materialNotFound){
							return false;
						}
					}
					ItemStack thisIsATest = MiscUtils.generateItem(Material.REDSTONE_COMPARATOR, ChatColor.YELLOW + "This is a test!", StringUtil.wrapLore(ChatColor.GRAY +  "This is a test of our versatile Shop system. Hopefully it's working!\n" + ChatColor.DARK_GRAY + "Each price should be 200 more than the last."), (byte) -1, 1);
					ShopInventory shop = new ShopInventory(player.getName() + "'s Shop Test", Arrays.asList(items), player, DyeColor.YELLOW.getData(), thisIsATest);
					player.openInventory(shop.getInventory());
					return true;
				}else return false;
			}else if(command.getName().equalsIgnoreCase("booster")){
				if(args.length > 0){
					try{
						float multipler = Float.parseFloat(args[0]);
						Float previous = DesertMain.booster.put(player.getUniqueId(), multipler);
						player.sendMessage(previous == null ? ChatColor.GREEN + "Added " + multipler + "x EXP booster" : ChatColor.GREEN + "Added " + multipler + "x EXP booster, replacing " + previous);
					}catch(NumberFormatException | NullPointerException ex){
						return false;
					}
				}
			}else if(command.getName().equalsIgnoreCase("hologram")){
				if(player.hasPermission("admin")){
					if(args.length > 0){
						String holoName = RankEvents.colorSupporterMessage(String.join(" ", args));
						if(!player.getInventory().addItem(MiscUtils.getHologramWand(holoName)).isEmpty()){
							player.sendMessage(ChatColor.RED + "Full inventory!");
						}
					}else return false;
				}else player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
			}
        	if(command.getName().equalsIgnoreCase("setspawn")) {
        		if(player.hasPermission("admin")){
        			String type;
        			if(args.length == 0) type = "lobby";
        			else type = args[0];
        			mainpl.getConfig().set("server.spawn." + type, player.getLocation());
        			player.sendMessage(Prefix.SERVER + ChatColor.GRAY.toString() + ": Set the spawn for " + StringUtil.capitalizeFirst(type));
        			mainpl.saveConfig();
        		}else{
        			player.sendMessage(ChatColor.RED + "Only admins can use this command.");
				}
        	}

        	if(command.getName().equalsIgnoreCase("blocknotifications")){
        		if(ConfigUtils.getLevel(ConfigUtils.findClass(player), player.getUniqueId()) > 5){
        			ConfigUtils.toggleBlockNotifications(player, false);
				}else player.sendMessage(ChatColor.RED + "You have not unlocked the Traveller class tier yet!");
			}

        	if(command.getName().equalsIgnoreCase("colors")){
        		if(RankEvents.rankSession.containsKey(player.getUniqueId())){
					player.sendMessage(colorsMessage);
        			return true;
				}else{
        			player.sendMessage(ChatColor.RED + "You must have a rank to use this command!");
        			return false;
				}
			}
			if(command.getName().equalsIgnoreCase("cosmetic")){
				if(args.length >= 2){
					if(args[0].equalsIgnoreCase("set")){
						Cosmetic toSet = Cosmetic.getFromName(String.join(" ", args).replace("set ", ""));
						if(toSet != null) {
							if(toSet.select(player))
								player.sendMessage(ChatColor.GREEN + "Successfully selected cosmetic " + ChatColor.GOLD + toSet + ChatColor.GRAY + " (" + toSet.cosmeticType + ")");
							else if(player.hasPermission("admin")){
								player.sendMessage(ChatColor.GREEN + "You haven't unlocked that one yet, so I added it and selected it for you. Hi admin!");
								toSet.grant(player);
								toSet.select(player);
							}else{
								player.sendMessage(ChatColor.RED + "You haven't unlocked that cosmetic yet! To see your unlocked cosmetics and more, type /cosmetic to open the menu!");
							}
						}else player.sendMessage(ChatColor.RED + "That cosmetic doesn't exist!");
					}else if(args[0].equalsIgnoreCase("grant")){
						if(player.hasPermission("admin")){
							Cosmetic toSet = Cosmetic.getFromName(String.join(" ", args).replace("grant ", ""));
							if(toSet == null) player.sendMessage(ChatColor.RED + "That cosmetic doesn't exist!");
							else toSet.grant(player);
						}else player.sendMessage(ChatColor.RED + "Only admins can do that!");
					}
				}else if(args.length >= 1){
					if(args[0].equalsIgnoreCase("list")){
						ArrayList<String> cList = new ArrayList<>();
						for (Cosmetic.CosmeticType type : Cosmetic.CosmeticType.values()) {
							Cosmetic selected = Cosmetic.getSelected(player, type);
							String name = ChatColor.DARK_GRAY + "None";
							if (selected != null) name = selected.displayName;
							cList.add(ChatColor.AQUA + "  Selected " + type.displayName + ": " + ChatColor.GOLD + name);
						}
						StringUtil.ChatWrapper wrapper = new StringUtil.ChatWrapper('=', ChatColor.GOLD, true, true);
						StringUtil.sendUncenteredWrappedMessage(player, wrapper, String.join("\n", cList));
					}
				}else{
					player.sendMessage(ChatColor.RED + "Invalid Usage! " + ChatColor.YELLOW + "Type /cosmetic to open the menu." + ChatColor.DARK_GRAY + "\nAdditionally, you can use the non-menu command: /cosmetic <set|list> <cosmetic to set>");
				}
				return true;
			}
        	if(command.getName().equalsIgnoreCase("rank")){
        		if(player.hasPermission("admin")){
        			if(args.length == 2){
        				try{
        					if(Rank.valueOf(args[1]).equals(Rank.COOWNER) || Rank.valueOf(args[1]).equals(Rank.ADMIN)){
								if (!player.getUniqueId().toString().equals("7f9ad03e-23ec-4648-91c8-2e0820318a8b") || player.getUniqueId().toString().equals("a082eaf8-2e8d-4b23-a041-a33ba8d25d5d")){
									player.sendMessage(ChatColor.RED + "Nice try. You can't give other people COOWNER or ADMIN.");
									return true;
								}
							}
        					mainpl.getConfig().set("players." + Bukkit.getPlayer(args[0]).getUniqueId() + ".rank", Rank.valueOf(args[1]).name());
        					mainpl.saveConfig();
        					player.sendMessage(ChatColor.GREEN + "Rank set successfully.");
						}catch(IllegalArgumentException noRankFound){
        					player.sendMessage(ChatColor.RED + "Rank not found! Usage: /rank <player> <rank>");
        					return true;
						}
					}else{
        				player.sendMessage(ChatColor.RED + "Invalid usage! Usage: /rank <player> <rank>");
        				return true;
					}
				}
			}

        	if(command.getName().equalsIgnoreCase("hitbox")){
				if(player.hasPermission("admin")){
					if(args[0].equalsIgnoreCase("rect")){
						if(hitboxAwait.containsKey(player.getUniqueId())){
							HitboxManager.set(args[1],new BoxHitbox(hitboxAwait.get(player.getUniqueId()),player.getLocation()));
							player.sendMessage(ChatColor.GREEN + "Hitbox created.");
							hitboxAwait.remove(player.getUniqueId());
						}else {
							player.sendMessage(ChatColor.GREEN + "Use /hitbox rect <name> at the next location.");
							hitboxAwait.put(player.getUniqueId(), player.getLocation());
						}
					}
					if(args[0].equalsIgnoreCase("sphere")){
						HitboxManager.set(args[2],new CircleHitbox(player.getLocation(),Integer.parseInt(args[1])));
						player.sendMessage(ChatColor.GREEN + "Hitbox created.");
					}
				}
			}

        	if(command.getName().equalsIgnoreCase("seizehelditem")){
        		if(player.hasPermission("admin")){
        			try{

        				Player target = Bukkit.getServer().getPlayerExact(args[0]);
        				PlayerInventory targetInv = target.getInventory();
        				ItemStack seizedItem = StreakPolice.seize(targetInv.getItemInHand());
        				int itemSeizeSlot = targetInv.getHeldItemSlot();
        				targetInv.clear(itemSeizeSlot);
        				targetInv.setItem(itemSeizeSlot, seizedItem);
        				player.sendMessage(ChatColor.GREEN + "Item seized successfully");
        				return true;
					}catch(NullPointerException ex){
        				player.sendMessage(ChatColor.RED + "You either didn't specify a target player, or the item they were holding wasn't eligible to be seized.");
					}
				}else{
        			player.sendMessage(ChatColor.RED + "Sorry, you can't use this command.");
				}
			}
        	if(command.getName().equalsIgnoreCase("expmilestones")){
        		player.openInventory(MilestonesInventory.getInventory(player));
			}
        	if(command.getName().equalsIgnoreCase("addweight")){
        		if(player.hasPermission("admin")){
					ItemStack item = player.getItemInHand();
					NBTItem nbt = new NBTItem(item);
					NBTCompound customAttributes = nbt.getCompound("CustomAttributes");
					double toAdd = Double.parseDouble(args[0]);
					if(customAttributes.hasKey("WEIGHT")){
						customAttributes.setDouble("WEIGHT", customAttributes.getDouble("WEIGHT") + toAdd);
					}else customAttributes.setDouble("WEIGHT", toAdd);
					player.setItemInHand(nbt.getItem());
					player.sendMessage(ChatColor.GREEN + "Added weight successfully");
					return true;
				}else{
        			player.sendMessage(ChatColor.RED + "You don't have permission to use this command");
				}
			}
        	if(command.getName().equalsIgnoreCase("spawnnpc")){
        		if(player.hasPermission("admin")){
					if(args.length > 1){
						StringBuilder className = new StringBuilder();
						String[] npcNameArr = new String[args.length - 1];
						System.arraycopy(args, 1, npcNameArr, 0, npcNameArr.length);
						for(String npcNameWord : npcNameArr) className.append(StringUtil.capitalizeFirst(npcNameWord));
						for(String pack : DesertMain.NPC_PACKAGES){
							try{
								@SuppressWarnings("unchecked")
								Class<? extends NPCSuper> npcClass = (Class<? extends NPCSuper>) Class.forName(pack + "." + className);
								NPCSuper npcObj = npcClass.newInstance();
								npcObj.createNPC(player.getLocation());
								boolean save = Boolean.parseBoolean(args[0]);
								if(save){
									try{
										npcObj.saveCurrent(mainpl);
										player.sendMessage(ChatColor.GREEN + "NPC successfully saved.");
									}catch(Exception ex){
										player.sendMessage(ChatColor.RED + "There was an error saving your NPC.");
										Bukkit.getLogger().log(Level.WARNING, "Couldn't save NPC " + className + ":", ex);
									}
								}
								return true;
							}catch(Exception ex){
								if(ex instanceof ClassCastException){
									player.sendMessage(ChatColor.RED + "The file or your requested NPC was loaded, but it wasn't the correct type to be initialized as an NPC. Please inform a developer about this immediately, if you're not one. If you are one, get off your lazy ass and get crackin' fixing this error!");
									Bukkit.getLogger().log(Level.SEVERE, "Problem casting NPC " + pack + "." + className + " to NPCSuper", ex);
									return true;
								}else if(ex instanceof InstantiationException || ex instanceof IllegalAccessException){
									Bukkit.getLogger().log(Level.WARNING, "Error spawning NPC " + pack + "." + className, ex);
									player.sendMessage(ChatColor.RED + "We encountered an error spawning your NPC. If this keeps happening, please alert a server dev.");
									return true;
								}else if(!(ex instanceof ClassNotFoundException)){
									player.sendMessage(ChatColor.RED + "An unknown error occurred loading your NPC.");
									Bukkit.getLogger().log(Level.WARNING, "Could not spawn NPC " + className, ex);
								}
							}
						}
						player.sendMessage(ChatColor.RED + "Sorry, that NPC wasn't found.");
						return true;
					}else{
						return false;
					}
				}else{
        			player.sendMessage(ChatColor.RED + "Sorry, you don't have access to that command.");
				}
			}
        	if(command.getName().equalsIgnoreCase("hideplayer")){
        		if(player.hasPermission("admin")){
        			try{
        				player.hidePlayer(Bukkit.getPlayer(args[0]));
        				player.sendMessage(ChatColor.GREEN + "Hid " + args[0] + " from your view.");
					}catch (Exception e){
        				player.sendMessage(ChatColor.RED + "There was an error fetching that player.");
					}
				}else{
        			player.sendMessage(ChatColor.RED + "Only admins can use this command.");
				}
			}

        	if(command.getName().equalsIgnoreCase("testench")){
        		if(player.hasPermission("admin")){
        			try{
						player.getInventory().setItemInHand(CustomEnch.fromID(args[0]).apply(player.getInventory().getItemInHand(),Integer.parseInt(args[1])));
        			}catch(Exception e){
        				player.sendMessage(ChatColor.RED + "An error occurred. " + e);
        				e.printStackTrace();
					}
				}
			}
        	if(command.getName().equalsIgnoreCase("selecttitle")){
        		try{
        			if(args.length != 0) {
						Prefix p = Prefix.valueOf(args[0].toUpperCase());
						if (TitleUtils.setTitle(player, p)) {
							player.sendMessage(ChatColor.GREEN + "Prefix successfully set to \"" + p + ChatColor.GREEN + "\"");
							return true;
						} else {
							if (player.hasPermission("admin")) {
								TitleUtils.addTitle(player, p);
								TitleUtils.setTitle(player, p);
								player.sendMessage(ChatColor.YELLOW + "You didn't have that title so I added it for you and selected it.");
								return true;
							} else player.sendMessage(ChatColor.RED + "Sorry, it seems you don't own that title.");
							return true;
						}
					}else{
						player.sendMessage(ChatColor.RED + "Usage: /title <titletoequip>");
						return true;
					}
				}catch(Exception e){
        			if(e instanceof IllegalArgumentException){
        				player.sendMessage(ChatColor.RED + "It appears that title doesn't exist.");
        				return true;
        			}else{
        				e.printStackTrace();
        				return false;
					}

				}
			}

        	if(command.getName().equalsIgnoreCase("showplayer")){
				if(player.hasPermission("admin")){
					try{
						player.showPlayer(Bukkit.getPlayer(args[0]));
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

        	if(command.getName().equalsIgnoreCase("kothy")) {
        		if(player.hasPermission("admin")){
					KothyMenu kot = new KothyMenu(player);
					player.openInventory(kot.getInventory());
				}else{
        			player.sendMessage(ChatColor.RED + "Only admins can use this command. Talk to Kothy at the cafe!");
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
							player.sendMessage(ChatColor.RED + "[SERVER]" + ChatColor.RED + " Invalid number \"" + args[0] + "\"! Must be from" + ChatColor.GREEN + " 1 through 10" + ChatColor.RED + "!");
						}
	        		} else {
	        			player.sendMessage(ChatColor.RED + "Only admins can use this command.");
	        		}
        		}
				

        	
        	}
        	if(command.getName().equalsIgnoreCase("invincible")) {
        		if(player.hasPermission("admin")) {
	        			if(!Events.invincible.contains(player.getUniqueId())) {
		        			player.sendMessage(ChatColor.GREEN + "Made you invincible!");
		        			Events.invincible.add(player.getUniqueId());
						}else{
		        			Events.invincible.remove(player.getUniqueId());
		        			player.sendMessage(ChatColor.RED + "Turned off your invincibility!");
						}
					mainpl.saveConfig();
				} else {
        			player.sendMessage(ChatColor.RED + "Only admins can use this command.");
        		}
        		
        	}
        }
		return true;
    }
}
