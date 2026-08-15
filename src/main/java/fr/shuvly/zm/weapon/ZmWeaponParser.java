package fr.shuvly.zm.weapon;

import fr.shuvly.zm.exception.MapParseException;
import fr.shuvly.zm.weapon.behavior.gun.firearm.FirearmBehavior;
import fr.shuvly.zm.weapon.behavior.gun.projectile.ProjectileBehavior;
import fr.shuvly.zm.weapon.behavior.melee.aoe.AoeMeleeBehavior;
import fr.shuvly.zm.weapon.behavior.melee.standard.StandardMeleeBehavior;
import fr.shuvly.zm.weapon.behavior.throwable.timed_explosive.TimedExplosiveBehavior;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

public class ZmWeaponParser
{

    private static final Map<String, ZmWeaponFactory> FACTORIES = Map.of(
        "FIREARM", FirearmBehavior::new,
        "PROJECTILE", ProjectileBehavior::new,
        "TIMED_EXPLOSIVE", TimedExplosiveBehavior::new,
        "MELEE", StandardMeleeBehavior::new,
        "AOE_MELEE", AoeMeleeBehavior::new
    );


    private ZmWeaponParser() {}


    public static ZmWeapon parse(String weaponId, ConfigurationSection config)
        throws MapParseException
    {
        final String behavior = config.getString("behavior");

        if (behavior == null) {
            throw new MapParseException("Weapon '" + weaponId + "' is missing 'behavior'.");
        }

        final ZmWeaponFactory factory = FACTORIES.get(behavior.toUpperCase());

        if (factory == null) {
            throw new MapParseException("Unknown behavior '" + behavior + "' for weapon '" + weaponId + "'.");
        }

        return factory.create(weaponId, config);
    }

}
