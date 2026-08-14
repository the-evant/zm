package fr.shuvly.zm.weapon.behavior.melee;

import org.bukkit.configuration.ConfigurationSection;

public record MeleeStats(
    double damage,
    double range,
    long cooldownTicks
)
{

    public static MeleeStats fromConfig(ConfigurationSection stats)
    {
        if (stats == null) {
            throw new IllegalArgumentException("Missing 'stats' section");
        }

        return new MeleeStats(
            stats.getDouble("damage", 150.0),
            stats.getDouble("range", 2.5),
            stats.getLong("cooldown_ticks", 15L)
        );
    }

}
