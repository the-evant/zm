package fr.shuvly.zm.weapon.behavior.gun.firearm;

import fr.shuvly.zm.weapon.behavior.gun.GunStats;
import org.bukkit.configuration.ConfigurationSection;

public record FirearmStats(
    GunStats base,

    int pelletCount,
    double spread,
    double dropoffStart,
    double dropoffEnd
)
{

    public static FirearmStats fromConfig(ConfigurationSection stats)
    {
        if (stats == null) {
            throw new IllegalArgumentException("Firearm weapon missing 'stats' section");
        }

        return new FirearmStats(
            GunStats.fromConfig(stats),

            stats.getInt("pellet_count", 1),
            stats.getDouble("spread", 0.0),
            stats.getDouble("damage.dropoff_start", 50.0),
            stats.getDouble("damage.dropoff_end", 51.0)
        );
    }

}
