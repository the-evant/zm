package fr.shuvly.zm.weapon;

import org.bukkit.configuration.ConfigurationSection;

@FunctionalInterface
public interface ZmWeaponFactory
{

    ZmWeapon create(String id, boolean isInMysteryBox, ConfigurationSection config);

}
