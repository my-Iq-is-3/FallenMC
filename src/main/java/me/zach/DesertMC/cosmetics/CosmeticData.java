package me.zach.DesertMC.cosmetics;

import me.zach.DesertMC.Utils.Config.ConfigUtils;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Cosmetic data storage class. Most manipulation for this class intended for a human is located in {@link Cosmetic} enum.
 */
public class CosmeticData {
    @BsonProperty("selected")
    Map<String, Cosmetic> selected = new HashMap<>();
    @BsonProperty("unlocked")
    Set<Cosmetic> unlocked = new HashSet<>();

    /**
     * Returns raw data for all selected cosmetics. Recommended only for use by MongoDB's automatic object mapping.
     * @return Each CosmeticType enum name mapped to selected cosmetic for that type
     */
    public Map<String, Cosmetic> getSelected(){
        return selected;
    }

    /**
     * Sets raw data for all selected cosmetics. Recommended only for use by MongoDB's automatic object mapping.
     * @param selected Each CosmeticType enum name mapped to selected cosmetic for that type
     */
    public void setSelected(Map<String, Cosmetic> selected){
        this.selected = selected;
    }

    public Set<Cosmetic> getUnlocked(){
        return unlocked;
    }

    public void setUnlocked(Set<Cosmetic> unlocked){
        this.unlocked = unlocked;
    }

    public Cosmetic cosmeticGet(Cosmetic.CosmeticType type){
        return selected.getOrDefault(type.name(), null);
    }

    public void cosmeticSet(Cosmetic cosmetic){
        selected.put(cosmetic.cosmeticType.name(), cosmetic);
    }

    public void unselectCosmetic(Cosmetic cosmetic){
        selected.remove(cosmetic.cosmeticType.name());
    }

    public static CosmeticData get(Player player){
        return get(player.getUniqueId());
    }

    public static CosmeticData get(UUID uuid){
        return ConfigUtils.getData(uuid).getCosmeticData();
    }
}
