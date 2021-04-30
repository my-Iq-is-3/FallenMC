package me.zach.DesertMC.Utils.StringUtils;

import java.util.Arrays;
import java.util.List;

public class StringUtil {
    public static List<String> wrapLore(String string){
        StringBuilder sb = new StringBuilder(string);
        int i = 0;
        while (i + 35 < sb.length() && (i = sb.lastIndexOf(" ", i + 35)) != -1) {
            sb.replace(i, i + 1, "\n");
        }
        return Arrays.asList(sb.toString().split("\n"));
    }
}
