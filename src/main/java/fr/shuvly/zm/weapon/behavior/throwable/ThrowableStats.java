package fr.shuvly.zm.weapon.behavior.throwable;

import org.bukkit.configuration.ConfigurationSection;

public record ThrowableStats(
    int maxAmount,
    long throwCooldownTicks
)
{

    public static ThrowableStats fromConfig(ConfigurationSection stats)
    {
        if (stats == null) {
            throw new IllegalArgumentException("Missing 'stats' section");
        }

        return new ThrowableStats(
            stats.getInt("max_amount", 4),
            stats.getLong("throw_cooldown_ticks", 20L)
        );
    }

}
