package me.zach.DesertMC.Utils.stringutil;

import java.util.List;

public class ListUtil {
    /**
     *
     * @param l the list in which I am finding the object
     * @param thing the object you are trying to get the index of
     * @return The index (0 based), and if it doesn't exist, {@<code>-1</code>}
     */
    public static int getFirstIndex(List<?> l,Object thing){
        int index = 0;
        for(Object o : l){

            if(thing == o){
                return index;
            }
            index++;
        }
        return -1;
    }

}
