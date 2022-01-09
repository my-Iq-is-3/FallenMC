package me.zach.DesertMC.GameMechanics.hitbox;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;

import java.util.List;

public interface Hitbox extends ConfigurationSerializable {

    boolean isInside(Location l);

//    String getName();
}
