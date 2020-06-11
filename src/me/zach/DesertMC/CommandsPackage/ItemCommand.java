package me.zach.DesertMC.CommandsPackage;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import me.zach.DesertMC.mythicalitems.Mythical;
import net.minecraft.server.v1_9_R1.CommandExecute;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ItemCommand extends CommandExecute implements CommandExecutor, Listener {
    public static final ItemCommand INSTANCE = new ItemCommand();

    private static final HashMap<String,ItemStack> items = new HashMap<>();

    public static final HashMap<String,String> enchs = new HashMap<>();
    public static final HashMap<String,Double> increments = new HashMap<>();

    static {
        items.put("MagicWand",INSTANCE.getMagicWand());
        items.put("ScoutGoggles",INSTANCE.getScoutGoggles());
        items.put("VolcanicSword",INSTANCE.getVolcanicSword());
        items.put("Dagger",INSTANCE.getDagger());
        increments.put("no_mercy",0.5);

        enchs.put("no_mercy_1", getLore(1,"no_mercy"));
        enchs.put("no_mercy_2", getLore(1,"no_mercy"));
        enchs.put("no_mercy_3", getLore(1,"no_mercy"));
        enchs.put("no_mercy_4", getLore(1,"no_mercy"));
        enchs.put("no_mercy_5", getLore(1,"no_mercy"));
        enchs.put("no_mercy_6", getLore(1,"no_mercy"));
        enchs.put("no_mercy_7", getLore(1,"no_mercy"));
        enchs.put("no_mercy_8", getLore(1,"no_mercy"));
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args){
        if(commandSender instanceof Player){
            if(commandSender.hasPermission("item")){
                Player player = (Player) commandSender;
                if(args.length == 1){
                    try{



                            boolean isValid = false;
                            boolean isMythical = false;
                            if(args[0].equals("Mythical")){
                                isValid = true;
                                isMythical = true;
                                player.sendMessage(ChatColor.RED + "Usage: /item Mythical <id>");
                            }else if(items.get(args[0]) != null){
                                player.getInventory().addItem(items.get(args[0]));
                                isValid = true;
                            }

                            if(!isValid){
                                player.sendMessage(ChatColor.RED + "Please say a valid item.");
                            }




                    }catch(Exception e){
                        player.sendMessage(ChatColor.RED + "There was an error.");
                        e.printStackTrace();
                    }

                }else if(args.length == 0){
                    player.sendMessage(ChatColor.RED + "Usage: /item <item name>");
                    return false;
                }else if(args.length == 2){
                    if(args[0].equalsIgnoreCase("mythical")){
                        try{
                            int id = Integer.parseInt(args[1]);
                            player.getInventory().addItem(Mythical.getNewInstance().getByID(id));
                        }catch(NumberFormatException e){
                            player.sendMessage(ChatColor.RED + "Invalid ID." + ChatColor.GRAY + " (" + e + ").");
                        }
                    }
                }

            }else{
                commandSender.sendMessage(ChatColor.RED + "Only admins can use this command.");
                return false;
            }
        }else{
            commandSender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return false;
        }
        return false;
    }

    public ItemStack getScoutGoggles(){
        ItemStack scoutgoggles = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta sgm = (LeatherArmorMeta) scoutgoggles.getItemMeta();
        sgm.setDisplayName(ChatColor.GREEN + "Scout Goggles");
        ArrayList<String> sglore = new ArrayList<String>();
        sglore.add(ChatColor.DARK_GRAY + "While wearing, provides the ability to see invisible players.");
        sgm.setLore(sglore);
        sgm.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        sgm.setColor(Color.GREEN);
        sgm.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        sgm.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        scoutgoggles.setItemMeta(sgm);
        NBTItem scoutgoggleNBT = new NBTItem(scoutgoggles);
        scoutgoggleNBT.setByte("Unbreakable", (byte)1);
        NBTCompound customattr = scoutgoggleNBT.addCompound("CustomAttributes");
        customattr.setString("ID", "SCOUT_GOGGLES");
        customattr.setString("UUID", UUID.randomUUID().toString());
        customattr.setBoolean("CAN_ENCHANT", true);
        return scoutgoggleNBT.getItem();
    }

    public ItemStack getMagicWand(){
        ItemStack MagicWand = new ItemStack(Material.STICK);
        ItemMeta mwm = MagicWand.getItemMeta();
        mwm.setDisplayName(ChatColor.LIGHT_PURPLE + "Magic Wand");
        ArrayList<String> mwlore = new ArrayList<String>();
        mwlore.add("");
        mwlore.add(ChatColor.LIGHT_PURPLE + "Attack Ability: Unstable Magic");
        mwlore.add(ChatColor.DARK_GRAY + "On hit, can either apply a good effect");
        mwlore.add(ChatColor.DARK_GRAY + "or a bad effect to your opponent.");
        mwlore.add("");
        mwlore.add(ChatColor.GRAY + "Cooldown: " + ChatColor.RED + "5 Seconds");

        mwm.setLore(mwlore);
        mwm.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,1,true);
        mwm.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        mwm.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        mwm.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        MagicWand.setItemMeta(mwm);
        NBTItem mwnbt = new NBTItem(MagicWand);
        mwnbt.setByte("Unbreakable", (byte)1);
        NBTCompound customattr = mwnbt.addCompound("CustomAttributes");
        customattr.setString("ID", "MAGIC_WAND");
        customattr.setString("UUID", UUID.randomUUID().toString());
        customattr.setBoolean("CAN_ENCHANT", false);

        return mwnbt.getItem();
    }

    public ItemStack getVolcanicSword(){

        ItemStack vs = new ItemStack(Material.GOLD_SWORD);
        ItemMeta vsm = vs.getItemMeta();
        ArrayList<String> vslist = new ArrayList<>();

        vsm.setDisplayName(ChatColor.RED + "Volcanic Sword");

        vslist.add(" ");
        vslist.add(ChatColor.RED + "Streak Ability: Erupt");
        vslist.add(ChatColor.DARK_GRAY + "Every" + ChatColor.RED + " 5 " + ChatColor.DARK_GRAY + "kills with this item,");
        vslist.add(ChatColor.DARK_GRAY + "all players within a" + ChatColor.RED + " 5 " + ChatColor.DARK_GRAY + "block radius");
        vslist.add(ChatColor.DARK_GRAY + "are shot away in a" + ChatColor.RED + " massive " + ChatColor.DARK_GRAY + "explosion!");


        vsm.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        vsm.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        vsm.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        vsm.setLore(vslist);
        vs.setItemMeta(vsm);

        NBTItem nbtvs = new NBTItem(vs);
        nbtvs.setByte("Unbreakable", (byte) 1);
        NBTCompound nbtvscomp = nbtvs.addCompound("CustomAttributes");
        nbtvscomp.setString("ID", "VOLCANIC_SWORD");
        nbtvscomp.setString("UUID", UUID.randomUUID().toString());
        nbtvscomp.setBoolean("CAN_ENCHANT", true);


        return nbtvs.getItem();
    }

    public ItemStack getDagger(){
        ItemStack dagger = new ItemStack(Material.IRON_SWORD);
        ItemMeta dm = dagger.getItemMeta();
        ArrayList<String> dml = new ArrayList<>();

        dm.setDisplayName(ChatColor.BLUE + "Scout Dagger");
        dml.add(" ");
        dml.add(ChatColor.BLUE + "Attack Ability: Short-Range");
        dml.add(ChatColor.DARK_GRAY + "This item is incredibly short range!");
        dml.add(ChatColor.DARK_GRAY + "You can only hit players within " + ChatColor.BLUE + "2 blocks" + ChatColor.DARK_GRAY + " from you,");
        dml.add(ChatColor.DARK_GRAY + "but you deal " + ChatColor.BLUE + "5" + " damage.");
        dm.addItemFlags(ItemFlag.HIDE_ATTRIBUTES,ItemFlag.HIDE_ENCHANTS,ItemFlag.HIDE_UNBREAKABLE);
        dm.setLore(dml);
        dagger.setItemMeta(dm);

        NBTItem di = new NBTItem(dagger);
        NBTCompound customAttributes = di.addCompound("CustomAttributes");

        customAttributes.setString("ID", "DAGGER");
        customAttributes.setString("UUID", UUID.randomUUID().toString());
        customAttributes.setBoolean("CAN_ENCHANT", true);

        di.setByte("Unbreakable",(byte)1);


        return di.getItem();
    }

    private static String getLore(int level, String ench){
        String nomercy = ChatColor.DARK_PURPLE + "Enchantment: No Mercy{br}" +
                ChatColor.GRAY + "Every " + ChatColor.GREEN + "2 kills" + ChatColor.GRAY + ", you deal{br}" +
                ChatColor.GREEN + (increments.get("no_mercy") * level) + " damage" + ChatColor.GRAY +  " to every other player within{br}" +
                ChatColor.GREEN + "6 blocks" + ChatColor.GRAY + ".";

        switch(ench.toLowerCase()){

            case "no_mercy":
                return nomercy;

            default:
                return null;
        }
    }


}
