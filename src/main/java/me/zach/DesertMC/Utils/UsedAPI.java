package me.zach.DesertMC.Utils;

import java.net.MalformedURLException;
import java.net.URL;

public enum UsedAPI {
    NBTAPI("NBTAPI", "tr7zw", "https://github.com/tr7zw/Item-NBT-API"),
    NPCLIB("NPCLib", "JitseB", "https://github.com/JitseB/NPCLib"),
    BOSSBAR_API("BossBarAPI", "inventivetalent", "https://github.com/InventivetalentDev/BossBarAPI"),
    PARTICLE("ParticleEffect utility", "DarkBlade12", "https://github.com/DarkBlade12/ParticleEffect");

    public final URL url;
    public final String name;
    public final String author;
    UsedAPI(String name, String author, String url) throws IllegalArgumentException {
        this.name = name;
        this.author = author;
        try{
            this.url = new URL(url);
        }catch(MalformedURLException e){
            throw new IllegalArgumentException("Malformed URL creating UsedAPI." + name());
        }
    }
}
