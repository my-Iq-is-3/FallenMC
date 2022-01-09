package me.zach.DesertMC.holo;

import de.tr7zw.nbtapi.NBTEntity;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;

public class Hologram {
    private ArmorStand stand;
    private String content;
    private Entity riding;
    private final Location location;

    public String getContent(){
        return content;
    }

    public void setContent(String content){
        this.content = content;
        stand.setCustomName(content);
    }

    public Hologram(String name, Location location){
        this.location = location;
        this.content = name;
    }

    public Hologram(String name, Entity riding){
        this(name, riding.getLocation());
        this.riding = riding;
    }

    private ArmorStand getStand(String name, Location location){
        ArmorStand stand = location.getWorld().spawn(location, ArmorStand.class);
        stand.setCustomName(name);
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setMarker(true);
        stand.setBasePlate(false);
        stand.setCustomNameVisible(true);
        NBTEntity nbt = new NBTEntity(stand);
        nbt.setBoolean("Invulnerable", true);
        return stand;
    }

    public void remove(){
        if(riding != null) riding.eject();
        stand.remove();
        stand = null;
    }

    public void create(){
        if(stand == null){
            Location location = this.location == null ? riding.getLocation() : this.location;
            stand = getStand(content, location);
            if(riding != null){
                riding.setPassenger(stand);
            }
        }
    }
}
