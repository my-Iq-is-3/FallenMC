package me.zach.DesertMC.Utils.StringUtils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringUtil{
    public static final int LORE_LENGTH = 45;

    public static List<String> wrapLore(String string){
        StringBuilder sb = new StringBuilder(string);
        int firstLfIndex = sb.indexOf("\n");
        int i = firstLfIndex < LORE_LENGTH ? firstLfIndex : 0;
        while(i + LORE_LENGTH < sb.length() && (i = breakIndex(sb, i + LORE_LENGTH)) != -1){
            sb.setCharAt(i, '\n');
        }
        //maintaining ChatColors since I'm pretty sure item lore doesn't carry them over through list entries
        List<String> splitLore = new ArrayList<>(Arrays.asList(sb.toString().split("\n")));
        if(splitLore.size() > 1){
            String lastColors = ChatColor.getLastColors(splitLore.get(0));
            for(int j = 1; j < splitLore.size(); lastColors = ChatColor.getLastColors(splitLore.get(j)), j++){
                String line = splitLore.get(j);
                splitLore.set(j, lastColors + line);
            }
        }
        return splitLore;
    }

    private static int breakIndex(StringBuilder builder, int from){
        int spaceIndex = builder.indexOf(" ", from);
        int lfIndex = builder.indexOf("\n", from);
        if(spaceIndex == -1 || lfIndex == -1){
            //accounting for the index not being found
            if(spaceIndex == -1 && lfIndex == -1){
                return -1;
            }else{
                if(spaceIndex == -1) return lfIndex;
                else return spaceIndex;
            }
        }else return Math.min(lfIndex, spaceIndex);
    }

    private static final int CENTER_PX = 154;
    private static final int MAX_CHAT_LENGTH = 270;

    /**
     * <p>Spigot Thread Link: https://www.spigotmc.org/threads/free-code-sending-perfectly-centered-chat-message.95872/</p>
     * <p>Slightly altered</p>
     * @author @SirSpoodles
     */
    public static String getCenteredLine(String message){
        if(message == null || message.isEmpty()) return message;
        message = ChatColor.translateAlternateColorCodes('&', message);
        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for(char c : message.toCharArray()){
            if(c == '§'){
                previousCode = true;
            }else if(previousCode){
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            }else{
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while(compensated < toCompensate){
            sb.append(" ");
            compensated += spaceLength;
        }
        return sb + message + sb;
    }

    private static String[] getCenteredMessage(ChatWrapper wrapper, String... lines){
        ArrayList<String> messageBuilder = new ArrayList<>();
        for(String message : lines){
            messageBuilder.add(getCenteredLine(message));
        }
        if(wrapper != null){
            String centeredWrapper = getCenteredLine(wrapper.toString());
            messageBuilder.add(0, centeredWrapper);
            messageBuilder.add(centeredWrapper);
        }
        return messageBuilder.toArray(new String[0]);
    }

    public static void sendCenteredMessage(Player player, String... lines){
        player.sendMessage(getCenteredMessage(lines));
    }

    public static String[] getCenteredMessage(String... lines){return getCenteredMessage(null, lines);}

    public static void sendCenteredWrappedMessage(Player player, ChatWrapper wrapper, String... lines){
        player.sendMessage(getCenteredWrappedMessage(wrapper, lines));
    }

    public static String[] getCenteredWrappedMessage(ChatWrapper wrapper, String... lines){
        return getCenteredMessage(wrapper, lines);
    }

    public static String getUncenteredWrappedMessage(ChatWrapper wrapper, String text){
        return wrapper.wrap(text);
    }

    public static void sendUncenteredWrappedMessage(Player player, ChatWrapper wrapper, String text){
        player.sendMessage(getUncenteredWrappedMessage(wrapper, text));
    }

    public static String capitalizeFirst(String str){
        if(str.isEmpty()) return str;
        char upperChar = Character.toUpperCase(str.charAt(0));
        return upperChar + str.substring(1);
    }

    public static String stylizeClass(String clazz){
         clazz = capitalizeFirst(clazz);
         if(clazz.equals("Corrupter")) return ChatColor.RED + clazz;
         else if(clazz.equals("Wizard")) return ChatColor.BLUE + clazz;
         else if(clazz.equals("Scout")) return ChatColor.AQUA + clazz;
         else if(clazz.equals("Tank")) return ChatColor.DARK_GREEN + clazz;
         else return clazz;
    }

    public static class ChatWrapper {
        public static final ChatWrapper HORIZONTAL_LINE = new ChatWrapper('-', ChatColor.GREEN, true, false);
        public static final ChatWrapper THICK_HORIZONTAL_LINE = new ChatWrapper('-', ChatColor.GREEN, true, true);
                

        public final char character;
        public final ChatColor color;
        private final String wrapper;

        public ChatWrapper(char character, ChatColor color, boolean strikethrough, boolean bold){
            this.character = character;
            this.color = color;

            StringBuilder wrapBuilder = new StringBuilder();
            wrapBuilder.append(strikethrough && bold ? color + ChatColor.BOLD.toString() + ChatColor.STRIKETHROUGH : strikethrough ? color + ChatColor.STRIKETHROUGH.toString() : bold ? ChatColor.BOLD.toString() + color : color.toString());
            DefaultFontInfo fontInfo = DefaultFontInfo.getDefaultFontInfo(character);
            int length = bold ? fontInfo.getBoldLength() : fontInfo.getLength();
            for(int i = 0; i<Math.floorDiv(MAX_CHAT_LENGTH, length); i += length){
                wrapBuilder.append(character);
            }
            wrapper = wrapBuilder.toString();
        }
        
        public String wrap(String text){
            return wrapper + ChatColor.RESET + "\n" + text + "\n" + ChatColor.RESET + wrapper;
        }

        public String toString(){
            return wrapper;
        }
    }
}
