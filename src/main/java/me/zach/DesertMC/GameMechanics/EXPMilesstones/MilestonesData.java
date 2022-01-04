package me.zach.DesertMC.GameMechanics.EXPMilesstones;

import me.zach.DesertMC.Utils.Config.ConfigUtils;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MilestonesData {
    @BsonProperty("level")
    int level = 1;
    @BsonProperty("current_progress")
    int currentProgress = 0;
    @BsonProperty("xp_to_next")
    int xpToNext = 200;
    @BsonProperty("resets")
    int resets = 0;
    @BsonProperty("unclaimed")
    List<Integer> unclaimed = new ArrayList<>();

    public int getLevel(){
        return level;
    }

    public void setLevel(int level){
        this.level = level;
    }

    public int getCurrentProgress(){
        return currentProgress;
    }

    public void setCurrentProgress(int currentProgress){
        this.currentProgress = currentProgress;
    }

    public int getXpToNext(){
        return xpToNext;
    }

    public void setXpToNext(int xpToNext){
        this.xpToNext = xpToNext;
    }

    public int getResets(){
        return resets;
    }

    public void setResets(int resets){
        this.resets = resets;
    }

    public List<Integer> getUnclaimed(){
        return unclaimed;
    }

    public void setUnclaimed(List<Integer> unclaimed){
        this.unclaimed = new ArrayList<>(unclaimed);
    }

    public static MilestonesData get(Player player){
        return ConfigUtils.getData(player).getMilestonesData();
    }
}
