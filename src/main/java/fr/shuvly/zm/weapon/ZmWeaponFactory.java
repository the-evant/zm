package fr.shuvly.zm.weapon;

import org.bukkit.configuration.ConfigurationSection;

public interface ZmWeaponFactory
{

    ZmWeapon create(String id, boolean isInMysteryBox, ConfigurationSection config);
    String getType();

}
