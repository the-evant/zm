package fr.shuvly.zm.weapon.behavior.projectile;

import org.bukkit.configuration.ConfigurationSection;

public record ProjectileStats(
    int clipSize,
    int maxReserve,
    double projectileSpeed,
    double aoeRadius,
    double maxDamage,
    double minDamage,
    long fireRateTicks,
    long reloadTicks
)
{

    public static ProjectileStats fromConfig(ConfigurationSection stats)
    {
        if (stats == null) {
            throw new IllegalArgumentException("Projectile weapon missing 'stats' section");
        }

        return new ProjectileStats(
            stats.getInt("clip_size", 1),
            stats.getInt("max_reserve", 20),
            stats.getDouble("projectile_speed", 2.0),
            stats.getDouble("aoe_radius", 4.0),
            stats.getDouble("damage.max", 1000.0),
            stats.getDouble("damage.min", 150.0),
            stats.getLong("fire_rate_ticks", 10L),
            stats.getLong("reload_ticks", 60L)
        );
    }

}
