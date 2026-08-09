package fr.shuvly.zm.weapon.behavior.gun;

import org.bukkit.configuration.ConfigurationSection;

public record GunStats(
    int clipSize,
    int maxReserve,
    double minDamage,
    double maxDamage,
    long fireRateTicks,
    long reloadTicks
)
{

    public static GunStats fromConfig(ConfigurationSection stats)
    {
        if (stats == null) {
            throw new IllegalArgumentException("Missing 'stats' section");
        }

        return new GunStats(
            stats.getInt("clip_size", 8),
            stats.getInt("max_reserve", 80),
            stats.getDouble("damage.min", 20.0),
            stats.getDouble("damage.max", 20.0),
            stats.getLong("fire_rate_ticks", 5L),
            stats.getLong("reload_ticks", 40L)
        );
    }

}
