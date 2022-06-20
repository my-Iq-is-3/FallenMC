package me.zach.DesertMC.CommandsPackage;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import itempackage.Items;
import me.zach.DesertMC.ClassManager.CoruManager.CorrupterTierMenu;
import me.zach.DesertMC.ClassManager.KothyMenu;
import me.zach.DesertMC.ClassManager.ScoutManager.ScoutTierMenu;
import me.zach.DesertMC.ClassManager.TankManager.TankTierMenu;
import me.zach.DesertMC.ClassManager.WizardManager.WizardTierMenu;
import me.zach.DesertMC.DesertMain;
import me.zach.DesertMC.GameMechanics.EXPMilesstones.MilestonesEvents;
import me.zach.DesertMC.GameMechanics.EXPMilesstones.MilestonesInventory;
import me.zach.DesertMC.GameMechanics.EXPMilesstones.MilestonesUtil;
import me.zach.DesertMC.GameMechanics.NPCStructure.NPCSuper;
import me.zach.DesertMC.GameMechanics.NPCStructure.SimpleNPC;
import me.zach.DesertMC.GameMechanics.npcs.SoulBroker;
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
import me.zach.DesertMC.Utils.gui.GUIHolder;
import me.zach.DesertMC.Utils.nbt.NBTUtil;
import me.zach.DesertMC.cosmetics.CosmeticData;
import me.zach.DesertMC.shops.ShopInventory;
import me.zach.DesertMC.shops.ShopItem;
import me.zach.DesertMC.cosmetics.Cosmetic;
import me.zach.databank.DBCore;
import me.zach.databank.saver.PlayerData;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;


public class Commands implements Listener, CommandExecutor {
	Set<UUID> soulsCd = new HashSet<>();
	Set<UUID> gemsCd = new HashSet<>();
	Set<UUID> expCd = new HashSet<>();
	final StringUtil.ChatWrapper COSMETIC_WRAPPER = new StringUtil.ChatWrapper('=', ChatColor.GOLD, true, true);
	static final long GEMS_COOLDOWN_TICKS = 30 * 20 * 60; //30m
	static final long SOULS_COOLDOWN_TICKS = 15 * 20 * 60; //15m
	static final long BOOSTER_EXPIRATION_TICKS = 45 * 20 * 60; //45m
	static final long BOOSTER_COOLDOWN_TICKS = 60 * 20 * 60; //60m
	private static final String[] colorsMessage;
	static{
		ArrayList<String> colorsList = new ArrayList<>();
		colorsList.add(ChatColor.GREEN + "With your rank, you can"  + ChatColor.YELLOW + " include " + ChatColor.AQUA + "colors " + ChatColor.GREEN + "in your messages!");
		colorsList.add("Placing a color code in your messages will make any");
		colorsList.add("text after that color code your color!");
		colorsList.add(ChatColor.GRAY + "Colors:");
		for(String name : RankEvents.friendlyCC.keySet()){
			ChatColor color = RankEvents.friendlyCC.get(name);
			String colorName = color.name();
			colorsList.add(name + ChatColor.DARK_GRAY + " - " + ChatColor.WHITE + colorName + " or !" + RankEvents.colorShortcuts.get(color));
		}
		colorsMessage = StringUtil.getCenteredWrappedMessage(new StringUtil.ChatWrapper('-', ChatColor.WHITE, true, true), colorsList.toArray(new String[0]));
	}

	private boolean openTierMenu(Player player, String clazz){
		if(clazz.equalsIgnoreCase("scout")){
			player.openInventory(new ScoutTierMenu(player).getInventory(true));
		}else if(clazz.equalsIgnoreCase("tank")){
			player.openInventory(new TankTierMenu(player).getInventory(true));
		}else if(clazz.equalsIgnoreCase("corrupter")){
			player.openInventory(new CorrupterTierMenu(player).getInventory(true));
		}else if(clazz.equalsIgnoreCase("wizard")){
			player.openInventory(new WizardTierMenu(player).getInventory(true));
		}else return false;
		return true;
	}

	final Set<UUID> dataGetCooldown = new HashSet<>();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
        	Player player = ((Player) sender);
        	Plugin mainpl = DesertMain.getInstance;
			if(command.getName().equalsIgnoreCase("tiermenu")){
				if(args.length == 1){
					String clazz = args[0];
					return openTierMenu(player, clazz);
				}else{
					return openTierMenu(player, ConfigUtils.findClass(player));
				}
			}else if(command.getName().equalsIgnoreCase("pay")){
				if(dataGetCooldown.contains(player.getUniqueId())){
					player.sendMessage(ChatColor.RED + "You must wait before doing this!");
				}else{
					if(args.length == 2){
						if(dataGetCooldown.contains(player.getUniqueId())){
							player.sendMessage(ChatColor.RED + "You must wait before doing this!");
						}
						try{
							int amount = Integer.parseInt(args[1]);
							if(amount <= 0){
								player.sendMessage(ChatColor.RED + "Invalid amount! Usage: /pay <player> <amount>");
							}else{
								PlayerData from = ConfigUtils.getData(player);
								if(from.getGems() < amount){
									player.sendMessage(ChatColor.RED + "You don't have that many gems!");
									return true;
								}
								OfflinePlayer target = Bukkit.getPlayer(args[0]);
								if(target == null){
									target = Bukkit.getOfflinePlayer(args[0]);
									dataGetCooldown.add(player.getUniqueId());
								}
								if(target != null && target.hasPlayedBefore()){
									boolean hardGet = false;
									PlayerData data = ConfigUtils.getData(target.getUniqueId());
									if(data == null){
										data = DBCore.getInstance().getSaveManager().getPlayerDataDirectly(player.getUniqueId());
										hardGet = true;
									}
									if(data == null){
										player.sendMessage(ChatColor.RED + "Couldn't find that player in our records!");
										return true;
									}
									from.setGems(from.getGems() - amount);
									data.setGems(data.getGems() + amount);
									player.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "PAY SUCCESS " + ChatColor.GREEN + amount + " Gems to the order of " + target.getName());
									if(hardGet){
										data.setGemsGottenWhileAway(data.getGemsGottenWhileAway() + amount);
										DBCore.getInstance().getSaveManager().getDatabank().set(data);
										dataGetCooldown.add(player.getUniqueId());
									}
									MiscUtils.confirmationSound(player);
									if(target.isOnline()){
										Player targetPlayer = target.getPlayer();
										MiscUtils.confirmationSound(target.getPlayer());
										targetPlayer.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "MONEY GET " + ChatColor.GREEN + "Received " + amount + " Gems from " + player.getName() + "!");
									}
								}else player.sendMessage(ChatColor.RED + "Could not find that player in our records!");
							}
						}catch(NumberFormatException ex){
							player.sendMessage(ChatColor.RED + "Invalid amount! Usage: /pay <player> <amount>");
						}
					}else return false;
				}
			}else if(command.getName().equalsIgnoreCase("die")){
				if(args.length > 0) return false;
				Events.executeKill(player);
			}else if(command.getName().equalsIgnoreCase("shoptest")){
				if(MiscUtils.isAdmin(player)){
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
						ItemStack thisIsATest = MiscUtils.generateItem(Material.REDSTONE_COMPARATOR, ChatColor.YELLOW + "This is a test!", StringUtil.wrapLore(ChatColor.GRAY + "This is a test of our versatile Shop system. Hopefully it's working!\n" + ChatColor.DARK_GRAY + "Each price should be 200 more than the last."), (byte) -1, 1);
						ShopInventory shop = new ShopInventory(player.getName() + "'s Shop Test", Arrays.asList(items), player, DyeColor.YELLOW.getData(), thisIsATest);
						player.openInventory(shop.getInventory());
						return true;
					}else return false;
				}else{
					player.sendMessage(ChatColor.RED + "You don't have permission to use this command");
				}
			}else if(command.getName().equalsIgnoreCase("credits")){
				if(dataGetCooldown.contains(player.getUniqueId())){
					player.sendMessage(ChatColor.RED + "You must wait before doing this!");
				}else{
					int color = ThreadLocalRandom.current().nextInt(StringUtil.FRIENDLY_COLORS.length);
					StringUtil.ChatWrapper wrapper = new StringUtil.ChatWrapper('=', StringUtil.FRIENDLY_COLORS[color], true, true);
					player.sendMessage(wrapper.toString());
					player.spigot().sendMessage(DesertMain.credits.toArray(new BaseComponent[0]));
					player.sendMessage(wrapper.toString());
					dataGetCooldown.add(player.getUniqueId());
					Bukkit.getScheduler().runTaskLater(DesertMain.getInstance, () -> dataGetCooldown.remove(player.getUniqueId()), 100);
				}
				return true;
			}else if(command.getName().equalsIgnoreCase("booster")){
				if(args.length > 0){
					try{
						float multipler = Float.parseFloat(args[0]);
						UUID uuid = player.getUniqueId();
						if(MiscUtils.isAdmin(player)){
							Float previous = DesertMain.booster.put(player.getUniqueId(), multipler);
							player.sendMessage(previous == null ? ChatColor.YELLOW + "Added " + multipler + "x EXP booster" : ChatColor.YELLOW + "Added " + multipler + "x EXP booster, replacing " + previous + "x");
							Bukkit.getScheduler().runTaskLater(mainpl, () -> {
								Float booster = DesertMain.booster.get(uuid);
								if(booster != null && booster == multipler){
									DesertMain.booster.remove(uuid);
									player.sendMessage(ChatColor.YELLOW + "Your " + booster + "x " + "EXP booster has expired.");
								}
							}, BOOSTER_EXPIRATION_TICKS);
							Bukkit.getScheduler().runTaskLater(mainpl, () -> expCd.remove(uuid), BOOSTER_COOLDOWN_TICKS);
						}else player.sendMessage(ChatColor.RED + "You can't use this!");
					}catch(NumberFormatException | NullPointerException ex){
						return false;
					}
				}
			}else if(command.getName().equalsIgnoreCase("hologram")){
				if(MiscUtils.isAdmin(player)){
					if(args.length > 0){
						String holoName = RankEvents.colorMessage(String.join(" ", args));
						if(!player.getInventory().addItem(MiscUtils.getHologramWand(holoName)).isEmpty()){
							player.sendMessage(ChatColor.RED + "Full inventory!");
						}
					}else return false;
				}else player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
			}else if(command.getName().equalsIgnoreCase("setspawn")) {
        		if(MiscUtils.isAdmin(player)){
        			String type;
        			if(args.length == 0) type = "lobby";
        			else type = args[0];
        			mainpl.getConfig().set("server.spawn." + type, player.getLocation());
        			player.sendMessage(Prefix.SERVER + ChatColor.GRAY.toString() + ": Set the spawn for " + StringUtil.capitalizeFirst(type));
        			mainpl.saveConfig();
        		}else{
        			player.sendMessage(ChatColor.RED + "Only admins can use this command.");
				}
        	}else if(command.getName().equalsIgnoreCase("blocknotifications")){
        		if(ConfigUtils.getLevel(ConfigUtils.findClass(player), player.getUniqueId()) > 5){
        			ConfigUtils.toggleBlockNotifications(player, false);
				}else player.sendMessage(ChatColor.RED + "You have not unlocked the Traveller class tier yet!");
			}

        	else if(command.getName().equalsIgnoreCase("colors")){
        		if(ConfigUtils.getRank(player) != null){
					player.sendMessage(colorsMessage);
        			return true;
				}else{
        			player.sendMessage(ChatColor.RED + "You must have a rank to use this command!");
        			return false;
				}
			}

        	else if(command.getName().equalsIgnoreCase("gems")){
				UUID uuid = player.getUniqueId();
				if(MiscUtils.isAdmin(player)){
					player.sendMessage(ChatColor.GREEN + "Gave you 1000 gems");
					ConfigUtils.addGems(player,1000);
					Bukkit.getScheduler().runTaskLater(mainpl, () -> gemsCd.remove(uuid), GEMS_COOLDOWN_TICKS);
				}else player.sendMessage(ChatColor.RED + "You can't use this!");
			}else if(command.getName().equalsIgnoreCase("souls")){
				UUID uuid = player.getUniqueId();
				if(MiscUtils.isAdmin(player)){
					player.sendMessage(ChatColor.LIGHT_PURPLE + "Gave you 30 souls");
					ConfigUtils.addSouls(player, 30);
					Bukkit.getScheduler().runTaskLater(mainpl, () -> soulsCd.remove(uuid), SOULS_COOLDOWN_TICKS);
				}else player.sendMessage(ChatColor.RED + "You can't use this!");
			}

			else if(command.getName().equalsIgnoreCase("cosmetic")){
				if(args.length >= 2){
					if(args[0].equalsIgnoreCase("set")){
						Cosmetic toSet = Cosmetic.getFromName(String.join(" ", args).replace("set ", ""));
						if(toSet != null) {
							if(toSet.select(player))
								player.sendMessage(ChatColor.GREEN + "Successfully selected cosmetic " + ChatColor.GOLD + toSet + ChatColor.GRAY + " (" + toSet.cosmeticType + ")");
							else if(MiscUtils.isAdmin(player)){
								player.sendMessage(ChatColor.GREEN + "You haven't unlocked that one yet, so I added it and selected it for you. Hi admin!");
								toSet.grant(player);
								toSet.select(player);
							}else{
								player.sendMessage(ChatColor.RED + "You haven't unlocked that cosmetic yet!");
							}
						}else player.sendMessage(ChatColor.RED + "That cosmetic doesn't exist!");
					}else if(args[0].equalsIgnoreCase("grant")){
						if(MiscUtils.isAdmin(player)){
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
							cList.add(ChatColor.AQUA + " Selected " + type.displayName + ": " + ChatColor.GOLD + name);
						}
						String msg = COSMETIC_WRAPPER.wrap(StringUtil.mergeLinesWithoutColorsCarrying(cList));
						player.sendMessage(msg);
					}else if(args[0].equalsIgnoreCase("unlocked")){
						Set<Cosmetic> unlocked = CosmeticData.get(player).getUnlocked();
						if(unlocked.isEmpty()) player.sendMessage(ChatColor.YELLOW + "You haven't unlocked any cosmetics yet.");
						else{
							//sort cosmetics into different categories for each cosmetic type and send that to the player
							Cosmetic.CosmeticType[] types = Cosmetic.CosmeticType.values();
							List<Cosmetic>[] unlockedSorted = new List[types.length];
							for(Cosmetic cosmetic : unlocked){
								List<Cosmetic> bin = unlockedSorted[cosmetic.cosmeticType.ordinal()];
								if(bin == null)
									unlockedSorted[cosmetic.cosmeticType.ordinal()] = bin = new ArrayList<>();
								bin.add(cosmetic);
							}
							List<String> message = new ArrayList<>();
							message.add(StringUtil.getCenteredLine(ChatColor.GREEN + "Your unlocked cosmetics"));
							for(int i = 0, unlockedSortedLength = unlockedSorted.length; i < unlockedSortedLength; i++){
								message.add(ChatColor.YELLOW + " " + MiscUtils.makePlural(types[i].toString()) + ":");
								List<Cosmetic> cosmetics = unlockedSorted[i];
								if(cosmetics == null || cosmetics.isEmpty()) message.add(ChatColor.GRAY + "  " + StringUtil.BULLET + " None");
								else{
									for(Cosmetic cosmetic : cosmetics)
										message.add(ChatColor.GRAY + "  " + StringUtil.BULLET + " " + cosmetic);
								}
							}
							player.sendMessage(COSMETIC_WRAPPER.wrap(StringUtil.mergeLinesWithoutColorsCarrying(message)));
						}
					}
				}else{
					player.sendMessage(ChatColor.RED + "Invalid Usage! /cosmetic <set|list|unlocked> <cosmetic to set>");
				}
				return true;
			}else if(command.getName().equalsIgnoreCase("rank")){
        		if(MiscUtils.isAdmin(player)){
        			if(args.length == 2){
        				try{
							Player target = Bukkit.getPlayer(args[0]);
							if(target != null){
								Rank rank = Rank.valueOf(args[1].toUpperCase());
								if(rank == Rank.COOWNER || rank == Rank.ADMIN){
									if(!MiscUtils.isCoolPerson(player.getUniqueId())){
										player.sendMessage(ChatColor.RED + "Nice try. You can't give other people COOWNER or ADMIN.");
										return true;
									}
								}
								PlayerData data = ConfigUtils.getData(target);
								data.setRank(rank);
								player.sendMessage(ChatColor.GREEN + "Rank set successfully.");
							}else{
								player.sendMessage(ChatColor.RED + "That player wasn't found. Usage: /rank <player> <rank>");
							}
						}catch(IllegalArgumentException noRankFound){
        					player.sendMessage(ChatColor.RED + "Rank not found! Usage: /rank <player> <rank>");
        					return true;
						}
					}else{
        				player.sendMessage(ChatColor.RED + "Invalid usage! Usage: /rank <player> <rank>");
        				return true;
					}
				}else player.sendMessage(ChatColor.RED + "Only admins can use this command.");
			}
        	if(command.getName().equalsIgnoreCase("seizehelditem")){
        		if(MiscUtils.isAdmin(player)){
        			try{

        				Player target = Bukkit.getServer().getPlayer(args[0]);
        				PlayerInventory targetInv = target.getInventory();
						ItemStack toSeize = targetInv.getItemInHand();
						NBTItem nbt = new NBTItem(toSeize);
						Double wph = NBTUtil.getCustomAttr(nbt, "WEIGHT_ADD", Double.class, null);
						Double weight = NBTUtil.getCustomAttr(nbt, "WEIGHT", Double.class, null);
        				ItemStack seizedItem = StreakPolice.seize(toSeize);
        				int itemSeizeSlot = targetInv.getHeldItemSlot();
        				targetInv.clear(itemSeizeSlot);
        				targetInv.setItem(itemSeizeSlot, seizedItem);
        				player.sendMessage(ChatColor.GREEN + "Item seized successfully");
						String[] lines;
						if(wph == 0.2){
							lines = StringUtil.getCenteredMessage("", ChatColor.RED + ChatColor.BOLD.toString() + "YOUR ITEM HAS BEEN SEIZED!", ChatColor.RED + "Talk to the Streak Police in the Cafe to get it back!", ChatColor.GRAY + "Hover for more info");
						}else{
							lines = StringUtil.getCenteredMessage("", ChatColor.RED + ChatColor.BOLD.toString() + "YOUR ITEM HAS BEEN SEIZED!", ChatColor.RED + "Talk to the Streak Police in the Cafe to get it back!", "");
						}
						TextComponent[] texts = new TextComponent[lines.length];
						for(int i = 0; i<texts.length; i++){
							TextComponent component = new TextComponent(lines[i]);
							component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(StringUtil.wrap(ChatColor.GRAY + "An item's weight is a percent chance for it to get seized when you kill a player. Each time you hit, your weapon's weight add (WPH) is added to your weapon.\n\n" + ChatColor.GRAY + "Your " + ChatColor.stripColor(toSeize.getItemMeta().getDisplayName()) + " had a weight of " + ChatColor.RED + ((int) Math.round(weight)) + "% " + ChatColor.GRAY + "when it was seized.", 35))}));
							texts[i] = component;
						}
						target.sendMessage(texts);
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
        		if(MiscUtils.isAdmin(player)){
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
			}else if(command.getName().equalsIgnoreCase("wand")){
				ItemStack item = MiscUtils.generateItem(Material.WOOD_AXE, ChatColor.WHITE + "WorldEdit Wand", StringUtil.wrapLore(ChatColor.GRAY + "\nA usable WorldEdit wand.\nRight click: first selection\nLeft click: second selection"), (byte) -1, 1, "WORLDEDIT_WAND");
				NBTItem nbt = new NBTItem(item);
				NBTUtil.checkCustomAttr(nbt).setBoolean("USABLE", true);
				player.getInventory().addItem(nbt.getItem());
			} if(command.getName().equalsIgnoreCase("hideplayer")){
        		if(MiscUtils.isAdmin(player)){
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
        		if(MiscUtils.isAdmin(player)){
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
							if (MiscUtils.isAdmin(player)) {
								TitleUtils.addTitle(player, p);
								TitleUtils.setTitle(player, p);
								player.sendMessage(ChatColor.YELLOW + "You didn't have that title so I added it for you and selected it. Hi admin!");
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
				if(MiscUtils.isAdmin(player)){
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
        		if(MiscUtils.isAdmin(player)){
        			ConfigUtils.resetclass(player,ConfigUtils.findClass(player));
        		}else{
        			player.sendMessage(ChatColor.RED + "Only admins can use this command");
				}
			}


        	if(command.getName().equalsIgnoreCase("classexp")) {
        		if(args[0].equalsIgnoreCase("wizard") || args[0].equalsIgnoreCase("tank") || args[0].equalsIgnoreCase("scout") || args[0].equalsIgnoreCase("corrupter")) {
        			if(MiscUtils.isAdmin(player)){
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
        		if(MiscUtils.isAdmin(player)){
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
        	else if(command.getName().equalsIgnoreCase("setks")){
        		if(!MiscUtils.isAdmin(player)){
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
        	else if(command.getName().equalsIgnoreCase("kothy")) {
        		if(MiscUtils.isAdmin(player)){
					KothyMenu kot = new KothyMenu(player);
					player.openInventory(kot.getInventory());
				}else{
        			player.sendMessage(ChatColor.RED + "Only admins can use this command. Talk to Kothy at the cafe!");
				}
        	}else if(command.getName().equalsIgnoreCase("dealconfirm")){
				if(args.length == 1){
					String res = args[0];
					if(res.equals("yes")){
						SoulBroker soulBroker = (SoulBroker) DesertMain.getInstance.getNPC("SoulBroker");
						Integer price = soulBroker.BOTTLE_CONFIRMING.remove(player.getUniqueId());
						if(price == null){
							player.sendMessage(ChatColor.RED + "This command is situation-specific!");
						}else{
							if(player.getInventory().firstEmpty() == -1){
								soulBroker.npcMessage(player, "Shiny bottles don't fit in full inventories. Somebody should've, uhhh... taught you this stuff.");
							}else if(ConfigUtils.deductSouls(player, price)){
								ConfigUtils.getData(player).addSoulsSpent(price);
								ItemStack bottle = Items.getSpiritBottle();
								player.getInventory().addItem(bottle);
								soulBroker.npcMessage(player, "Congrats, kid. The bottle's all yours. As always, it was a pleasure.");
							}else{
								soulBroker.npcMessage(player, "Kid, all your shiny, bottle-related ambitions are gonna stay pipe dreams until you've got the souls to pay for them. Maybe you'll have some more by the time this thing shows up again.");
							}
						}
					}else if(res.equals("no")){
						SoulBroker npc = (SoulBroker) DesertMain.getInstance.getNPC("SoulBroker");
						if(npc.BOTTLE_CONFIRMING.remove(player.getUniqueId()) == null){
							player.sendMessage(ChatColor.RED + "This command is situation-specific!");
						}else npc.npcMessage(player, "Eh, it's no problem, really. This bottle seems a little off anyways. I mean, why is it glowing? Bottles don't glow. Look it up.");
					}else return false;
				}else return false;
			}else if(command.getName().equalsIgnoreCase("speed")) {
        		if(args.length == 1) {
	        		if(MiscUtils.isAdmin(player)) {
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
				

        	
        	}else if(command.getName().equalsIgnoreCase("ks")){
				if(MiscUtils.isAdmin(player)){
					if(args.length == 1){
						int ks = Integer.parseInt(args[0]);
						Events.ks.put(player.getUniqueId(), ks);
						player.sendMessage(ChatColor.GREEN + "Set your killstreak to " + ks + ".");
					}else if(args.length == 2){
						Player target = Bukkit.getPlayer(args[0]);
						if(target == null) player.sendMessage(ChatColor.RED + "Unknown target! Usage: /ks <optional;target> <killstreak>");
						else{
							Events.ks.put(target.getUniqueId(), Integer.parseInt(args[1]));
							player.sendMessage(ChatColor.GREEN + "Set " + target.getName() + "'s killstreak to " + args[1]);
						}
					}
				}
			}else if(command.getName().equalsIgnoreCase("invincible")) {
        		if(MiscUtils.isAdmin(player)) {
					if(!Events.invincible.contains(player.getUniqueId())) {
						player.sendMessage(ChatColor.GREEN + "Made you invincible!");
						Events.invincible.add(player.getUniqueId());
					}else{
						Events.invincible.remove(player.getUniqueId());
						player.sendMessage(ChatColor.RED + "Turned off your invincibility!");
					}
				} else {
        			player.sendMessage(ChatColor.RED + "Only admins can use this command.");
        		}
        		
        	}else if(command.getName().equalsIgnoreCase("entityremoval")){
				if(MiscUtils.isAdmin(player)){
					if(args.length > 0){
						String name = ChatColor.stripColor(String.join(" ", args)).toUpperCase();
						if(name.trim().isEmpty()) return false;
						for(World world : Bukkit.getWorlds()){
							for(Entity entity : world.getEntities()){
								if(entity.getCustomName() != null){
									if(ChatColor.stripColor(entity.getCustomName()).toUpperCase().startsWith(name)){
										entity.remove();
										player.sendMessage(ChatColor.GREEN + "- Removed " + entity.getType().getName() + " \"" + entity.getCustomName() + ChatColor.GREEN + "\"");
									}
								}
							}
						}
					}else return false;
				}
			}
        }
		return true;
    }
}
