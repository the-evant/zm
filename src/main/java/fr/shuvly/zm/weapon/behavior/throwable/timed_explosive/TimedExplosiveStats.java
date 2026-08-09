package fr.shuvly.zm.weapon.behavior.throwable.timed_explosive;

import fr.shuvly.zm.weapon.behavior.throwable.ThrowableStats;
import org.bukkit.configuration.ConfigurationSection;

public record TimedExplosiveStats(
    ThrowableStats base,
    long fuseTicks,
    double explosionRadius,
    double damage
)
{

    public static TimedExplosiveStats fromConfig(ConfigurationSection stats)
    {
        return new TimedExplosiveStats(
            ThrowableStats.fromConfig(stats),

            stats.getLong("fuse_ticks", 60L),
            stats.getDouble("explosion_radius", 5.0),
            stats.getDouble("damage", 500.0)
        );
    }

}
