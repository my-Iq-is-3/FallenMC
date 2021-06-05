package me.zach.DesertMC.Utils.StringUtils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringUtil{
    public static List<String> wrapLore(String string){
        StringBuilder sb = new StringBuilder(string);
        int i = 0;
        while (i + 35 < sb.length() && (i = sb.lastIndexOf(" ", i + 35)) != -1){
            sb.replace(i, i + 1, "\n");
        }
        return Arrays.asList(sb.toString().split("\n"));
    }

    private final static int CENTER_PX = 154;
    private static final int MAX_CHAT_LENGTH = 310;

    /**
     * <p>Spigot Thread Link: https://www.spigotmc.org/threads/free-code-sending-perfectly-centered-chat-message.95872/</p>
     * @author @SirSpoodles
     */
    private static String getCenteredMessage(boolean wrap, ChatWrapper wrapper, String... lines){
        ArrayList<String> messageBuilder = new ArrayList<>();
        for(String message : lines){
            if (message == null || message.equals("")) return "";
            message = ChatColor.translateAlternateColorCodes('&', message);

            int messagePxSize = 0;
            boolean previousCode = false;
            boolean isBold = false;

            for (char c : message.toCharArray()){
                if (c == '§'){
                    previousCode = true;
                }else if (previousCode){
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
            while (compensated < toCompensate){
                sb.append(" ");
                compensated += spaceLength;
            }
            messageBuilder.add(sb + message);
        }
        if(wrap){
            String wrapperString = wrapper.toString();
            messageBuilder.add(0, wrapperString);
            messageBuilder.add(messageBuilder.size() - 1, wrapperString);
        }

        return String.join("\n", messageBuilder);
    }

    public static void sendCenteredMessage(Player player, String... lines){
        player.sendMessage(getCenteredMessage(lines));
    }

    public static String getCenteredMessage(String... lines){return getCenteredMessage(false, null, lines);}

    public static void sendCenteredWrappedMessage(Player player, ChatWrapper wrapper, String... lines) throws IllegalArgumentException{
        player.sendMessage(getCenteredWrappedMessage(wrapper, lines));
    }

    public static String getCenteredWrappedMessage(ChatWrapper wrapper, String... lines){
        return getCenteredMessage(true, wrapper);
    }

    public static String getUncenteredWrappedMessage(ChatWrapper wrapper, String text){
        return wrapper + text + wrapper;
    }

    public static void sendUncenteredWrappedMessage(Player player, ChatWrapper wrapper, String text){
        player.sendMessage(getUncenteredWrappedMessage(wrapper, text));
    }

    public static class ChatWrapper {
        public static final ChatWrapper HORIZONTAL_LINE = new ChatWrapper('-', ChatColor.GREEN, true, false);
        public static final ChatWrapper THICK_HORIZONTAL_LINE = new ChatWrapper('-', ChatColor.GREEN, true, true);
                

        public final char character;
        public final ChatColor color;
        private final String wrap;

        public ChatWrapper(char character, ChatColor color, boolean strikethrough, boolean bold){
            this.character = character;
            this.color = color;

            StringBuilder wrapBuilder = new StringBuilder();
            wrapBuilder.append(strikethrough && bold ? color + ChatColor.BOLD.toString() + ChatColor.STRIKETHROUGH.toString() : strikethrough ? color + ChatColor.STRIKETHROUGH.toString() : bold ? ChatColor.BOLD.toString() + color : color.toString());
            DefaultFontInfo fontInfo = DefaultFontInfo.getDefaultFontInfo(character);
            for(int i = 0; i<Math.floorDiv(MAX_CHAT_LENGTH, bold ? fontInfo.getBoldLength() : fontInfo.getLength()); i++){
                wrapBuilder.append(character);
            }
            wrap = wrapBuilder.toString();
        }

        public String toString(){
            return wrap;
        }
    }
}
