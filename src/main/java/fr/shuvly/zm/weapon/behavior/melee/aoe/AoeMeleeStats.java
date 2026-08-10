package fr.shuvly.zm.weapon.behavior.melee.aoe;

import fr.shuvly.zm.weapon.behavior.melee.MeleeStats;
import org.bukkit.configuration.ConfigurationSection;

public record AoeMeleeStats(
    MeleeStats base,

    double aoeRadius
)
{

    public static AoeMeleeStats fromConfig(ConfigurationSection stats)
    {
        if (stats == null) {
            throw new IllegalArgumentException("Aoe melee stats cannot be null");
        }

        return new AoeMeleeStats(
            MeleeStats.fromConfig(stats),

            stats.getDouble("aoe_radius", 5)
        );
    }

}
