package me.zach.DesertMC.Utils.StringUtils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringUtil{
    public static List<String> wrapLore(String string){
        StringBuilder sb = new StringBuilder(string);
        int breakIndex = sb.lastIndexOf("\n");
        int i = breakIndex == -1 ? 0 : breakIndex;
        while(i + 35 < sb.length() && (i = sb.lastIndexOf(" ", i + 45)) != -1){
            sb.setCharAt(i, '\n');
            i = sb.lastIndexOf("\n", i + 45);
        }
        //maintaining ChatColors since I'm pretty sure item lore doesn't carry them over through list entries
        List<String> splitLore = new ArrayList<>(Arrays.asList(sb.toString().split("\n")));
        String lastColors = ChatColor.getLastColors(splitLore.get(0));
        for(int j = 1; j<splitLore.size(); lastColors = ChatColor.getLastColors(splitLore.get(j)), j++){
            String line = splitLore.get(j);
            splitLore.set(j, lastColors + line);
        }
        return splitLore;
    }

    private static final int CENTER_PX = 154;
    private static final int MAX_CHAT_LENGTH = 250;

    /**
     * <p>Spigot Thread Link: https://www.spigotmc.org/threads/free-code-sending-perfectly-centered-chat-message.95872/</p>
     * <p>Slightly altered</p>
     * @author @SirSpoodles
     */
    private static String[] getCenteredMessage(ChatWrapper wrapper, String... lines){
        ArrayList<String> messageBuilder = new ArrayList<>();
        for(String message : lines){
            if(message == null || message.isEmpty()){
                messageBuilder.add(message);
                continue;
            }
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
            messageBuilder.add(sb + message + sb);
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
        return wrapper + text + wrapper;
    }

    public static void sendUncenteredWrappedMessage(Player player, ChatWrapper wrapper, String text){
        player.sendMessage(getUncenteredWrappedMessage(wrapper, text));
    }

    public static String capitalizeFirst(String str){
        if(str.isEmpty()) return str;
        StringBuilder builder = new StringBuilder(str);
        char upperChar = Character.toUpperCase(builder.charAt(0));
        builder.setCharAt(0, upperChar);
        return builder.toString();
    }

    public static class ChatWrapper {
        public static final ChatWrapper HORIZONTAL_LINE = new ChatWrapper('t', ChatColor.GREEN, true, false);
        public static final ChatWrapper THICK_HORIZONTAL_LINE = new ChatWrapper('-', ChatColor.GREEN, true, true);
                

        public final char character;
        public final ChatColor color;
        private final String wrapper;

        public ChatWrapper(char character, ChatColor color, boolean strikethrough, boolean bold){
            this.character = character;
            this.color = color;

            StringBuilder wrapBuilder = new StringBuilder();
            wrapBuilder.append(strikethrough && bold ? color + ChatColor.BOLD.toString() + ChatColor.STRIKETHROUGH.toString() : strikethrough ? color + ChatColor.STRIKETHROUGH.toString() : bold ? ChatColor.BOLD.toString() + color : color.toString());
            DefaultFontInfo fontInfo = DefaultFontInfo.getDefaultFontInfo(character);
            for(int i = 0; i<Math.floorDiv(MAX_CHAT_LENGTH, bold ? fontInfo.getBoldLength() : fontInfo.getLength()); i++){
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
