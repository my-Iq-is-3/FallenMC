package me.zach.DesertMC.cosmetics;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public interface CosmeticActivator{


    default void activateParticle(Player p){
        throw new Cosmetic.CosmeticActivationException("No specific activation method was defined for a particle effect!");
    }

    default void activateArrow(Arrow a, boolean fast){
        throw new Cosmetic.CosmeticActivationException("No specific activation method was defined for an arrow trail!");
    }

    default void activateStreak(Player p){
        throw new Cosmetic.CosmeticActivationException("No specific activation method was defined for a streak effect!");
    }

    default void activateDeath(Player p){
        throw new Cosmetic.CosmeticActivationException("No specific activation method was defined for a death effect!");
    }

    default void activateKill(Player p){
        throw new Cosmetic.CosmeticActivationException("No specific activation method was defined for a kill effect!");
    }
}
