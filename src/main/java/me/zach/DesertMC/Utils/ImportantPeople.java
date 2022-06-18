package me.zach.DesertMC.Utils;

import me.zach.DesertMC.Utils.Config.ConfigUtils;
import me.zach.DesertMC.Utils.RankUtils.Rank;
import me.zach.databank.DBCore;
import me.zach.databank.saver.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public enum ImportantPeople {
    //creators
    ARCHMLEM("7f9ad03e-23ec-4648-91c8-2e0820318a8b", "ArchMlem (archonic)"),
    ONE_IQ("a082eaf8-2e8d-4b23-a041-a33ba8d25d5d", "one_iq"),
    //admin
    EVERPIG("3ec4c07e-90c7-4807-a637-ec3f19d7276e", "Everpig"),
    //builders
    DAMPALIUS("dff8f3b3-e6eb-4f56-bd27-12d66bec48bd", "Dampalius"),
    LECTRO("383a081f-6dc0-4ac0-bb95-1f8d50c3e532", "xLectroLiqhtnin"),
    MACI("0dc45053-89a8-4c26-8157-30fd683a1ce4", "xMaci3jx"),
    PHLOOPY("027ae298-1bfe-404b-b48e-739a1851e39c", "phloopy_"),
    SHONEN("a5a780b6-84e1-4ce9-8af5-afe9a38be10f", "Shonen934");

    public final UUID uuid;
    public final String defaultName;

    public String getServerName(){
        OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
        if(p.hasPlayedBefore()){
            PlayerData data = ConfigUtils.getOrLoad(uuid);
            String name = p.getName();
            if(data != null){
                Rank rank = data.getRank();
                name = (rank == null ? ChatColor.YELLOW.toString() : rank.c) + name;
                if(data.getTitle() != null) name = data.getTitle().toString() + " " + name;
            }else name = ChatColor.YELLOW + name;
            return name;
        }else return ChatColor.YELLOW + defaultName;
    }

    ImportantPeople(String uuid, String defaultName){
        this.uuid = UUID.fromString(uuid);
        this.defaultName = defaultName;
    }
}
