package fr.shuvly.zm.weapon.behavior.gun;

import org.bukkit.configuration.ConfigurationSection;

public record GunStats(
    int clipSize,
    int maxReserve,
    double minDamage,
    double maxDamage,
    long fireRateTicks,
    long reloadTicks,
    GunFireMode fireMode,
    int burstShots,
    long burstDelayTicks
)
{

    public static GunStats fromConfig(ConfigurationSection stats)
    {
        if (stats == null) {
            throw new IllegalArgumentException("Missing 'stats' section");
        }

        final String fireModeStr =  stats.getString("fire_mode", "SINGLE");
        final GunFireMode fireMode;

        try {
            fireMode = GunFireMode.valueOf(fireModeStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown fire mode: " + fireModeStr);
        }

        return new GunStats(
            stats.getInt("clip_size", 8),
            stats.getInt("max_reserve", 80),
            stats.getDouble("damage.min", 20.0),
            stats.getDouble("damage.max", 20.0),
            stats.getLong("fire_rate_ticks", 5L),
            stats.getLong("reload_ticks", 40L),
            fireMode,
            stats.getInt("burst_shots", 3),
            stats.getLong("burst_delay_ticks", 2L)
        );
    }

}
