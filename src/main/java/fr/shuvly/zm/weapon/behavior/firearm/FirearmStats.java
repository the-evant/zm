package fr.shuvly.zm.weapon.behavior;

import org.bukkit.configuration.ConfigurationSection;

public record FirearmStats(
    int clipSize,
    int maxReserve,
    int pelletCount,
    double spread,
    double minDamage,
    double maxDamage,
    double dropoffStart,
    double dropoffEnd,
    long fireRateTicks,
    long reloadTicks
)
{

    public static FirearmStats fromConfig(ConfigurationSection stats)
    {
        if (stats == null) {
            throw new IllegalArgumentException("Hitscan weapon missing 'stats' section");
        }

        return new FirearmStats(
            stats.getInt("clip_size", 8),
            stats.getInt("max_reserve", 80),
            stats.getInt("pellet_count", 1),
            stats.getDouble("spread", 0.0),
            stats.getDouble("damage.max", 20.0),
            stats.getDouble("damage.min", 20.0),
            stats.getDouble("damage.dropoff_start", 50.0),
            stats.getDouble("damage.dropoff_end", 51.0),
            stats.getLong("fire_rate_ticks", 5L),
            stats.getLong("reload_ticks", 40L)
        );
    }

}
