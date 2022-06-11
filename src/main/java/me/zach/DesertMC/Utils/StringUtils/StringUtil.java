package me.zach.DesertMC.Utils.StringUtils;

import com.google.common.base.CharMatcher;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringUtil{
    public static final ChatColor[] FRIENDLY_COLORS = new ChatColor[]{ChatColor.GOLD, ChatColor.YELLOW, ChatColor.AQUA, ChatColor.GREEN, ChatColor.LIGHT_PURPLE, ChatColor.AQUA, ChatColor.RED};
    public static final char BULLET = '\u2022';
    public static final int LORE_LENGTH = 30;
    public static String wrap(String string, int length){
        if(string == null || string.isEmpty()) return string;
        StringBuilder sb = new StringBuilder(string);
        int i = 0;
        while(true){
            i = jumpToLineFeed(sb, i);
            if(i + length > sb.length() || (i = sb.lastIndexOf(" ", i + length)) == -1) break;
            if(sb.charAt(i) != '\n') sb.setCharAt(i, '\n');
        }
        return sb.toString();
    }

    public static List<String> wrapLore(String string, int length){
        List<String> splitLore = new ArrayList<>(Arrays.asList(wrap(string, length).split("\n")));
        //maintaining ChatColors since I'm pretty sure item lore doesn't carry them over through list entries
        if(splitLore.size() > 1){
            String lastColors = ChatColor.getLastColors(splitLore.get(0));
            for(int j = 1; j < splitLore.size(); lastColors = ChatColor.getLastColors(splitLore.get(j)), j++){
                String line = splitLore.get(j);
                splitLore.set(j, lastColors + line);
            }
        }
        return splitLore;
    }

    public static List<String> wrapLore(String string){
        return wrapLore(string, LORE_LENGTH);
    }

    private static int jumpToLineFeed(StringBuilder builder, int from){
        int lfIndex = builder.lastIndexOf("\n", from + LORE_LENGTH);
        if(lfIndex != -1 && lfIndex > from) return jumpToLineFeed(builder, lfIndex);
        else return from;
    }

    /**
     * Trims any trailing whitepsaces off of a string.
     * @param string String to trim.
     * @return A string rid of any whitespaces at the end.
     */
    public static String trimTrailingWhitespace(String string){
        int i = string.length() - 1;
        for(;i>=0; i--){
            char c = string.charAt(i);
            if(!CharMatcher.whitespace().matches(c)) break;
        }
        return string.substring(0, i + 1);
    }

    private static final int CENTER_PX = 154;
    public static final int MAX_CHAT_LENGTH = 270;

    /**
     * <p>Spigot Thread Link: https://www.spigotmc.org/threads/free-code-sending-perfectly-centered-chat-message.95872/
     * <br>Slightly altered
     * </p>
     * @author @SirSpoodles
     */
    public static String getCenteredLine(String message){
        if(message == null) return message;
        message = message.trim();
        if(message.isEmpty()) return message;
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
        StringBuilder sb = new StringBuilder(ChatColor.RESET.toString());
        while(compensated < toCompensate){
            sb.append(" ");
            compensated += spaceLength;
        }
        return sb + message + sb + ChatColor.RESET;
    }

    public static String[] getCenteredMessageSafe(String[] lines){
        return getCenteredMessageSafe(String.join("\n", lines));
    }

    /**
     *
     * @return A centered message that is correctly wrapped.
     */
    public static String[] getCenteredMessageSafe(String lines){
        if(lines.isEmpty()) return new String[0];
        lines = wrap(lines, MAX_CHAT_LENGTH);
        return getCenteredMessage(lines.split("\n"));
    }

    private static String[] getCenteredMessage(ChatWrapper wrapper, String... lines){
        ArrayList<String> messageBuilder = new ArrayList<>();
        for(String message : lines){
            messageBuilder.add(getCenteredLine(message));
        }
        if(wrapper != null){
            messageBuilder.add(0, wrapper.toString());
            messageBuilder.add(wrapper.toString());
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

    public static String getClassColor(String clazz){
        if(clazz.equalsIgnoreCase("corrupter")) return ChatColor.RED.toString();
        else if(clazz.equalsIgnoreCase("wizard")) return ChatColor.BLUE.toString();
        else if(clazz.equalsIgnoreCase("scout")) return ChatColor.AQUA.toString();
        else if(clazz.equalsIgnoreCase("tank")) return ChatColor.DARK_GREEN.toString();
        else return ChatColor.WHITE.toString();
    }

    public static String stylizeClass(String clazz){
        clazz = capitalizeFirst(clazz);
        return getClassColor(clazz) + clazz;
    }

    public static String mergeLinesWithoutColorsCarrying(Iterable<String> lines){
        return String.join("\n" + ChatColor.RESET,  lines);
    }

    /**
     * Makes a string array into a series of items, with proper english grammar. Ex: The array {Tater tots, french fries, Power Wash Simulator 2000} would become the string "Tater tots, french fries, and Power Wash Simulator 2000".
     * @param strings The string array to be made into a series of items.
     * @param color The default chatcolor.
     * @return The string array made into a series of items.
     */
    public static String series(ChatColor color, String... strings){
        String colorString = color == null ? "" : color.toString();
        if(strings.length == 0) return colorString + "";
        else if(strings.length == 1) return strings[0];
        else if(strings.length == 2) return strings[0] + colorString + " and " + strings[1] + colorString;
        else{
            int last = strings.length - 1;
            strings[last] = " and " +  strings[last];
            return String.join(colorString + ", ", strings);
        }
    }

    public static String series(String... strings){
        return series(null, strings);
    }

    public static String[] toStringArray(Object[] objects){
        String[] strings = new String[objects.length];
        for(int i = 0; i<objects.length; i++){
            strings[i] = objects[i].toString();
        }
        return strings;
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
            for(int i = 0; length * (i + 1) < MAX_CHAT_LENGTH; i++){
                wrapBuilder.append(character);
            }
            wrapper = getCenteredLine(wrapBuilder.toString());
        }
        
        public String wrap(String text){
            return wrapper + ChatColor.RESET + "\n" + text + "\n" + ChatColor.RESET + wrapper;
        }

        public String toString(){
            return wrapper;
        }
    }
}