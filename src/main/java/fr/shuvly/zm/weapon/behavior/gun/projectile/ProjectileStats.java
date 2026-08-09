package fr.shuvly.zm.weapon.behavior.gun.projectile;

import fr.shuvly.zm.weapon.behavior.gun.GunStats;
import org.bukkit.configuration.ConfigurationSection;

public record ProjectileStats(
    GunStats base,

    double projectileSpeed,
    double aoeRadius
)
{

    public static ProjectileStats fromConfig(ConfigurationSection stats)
    {
        if (stats == null) {
            throw new IllegalArgumentException("Projectile weapon missing 'stats' section");
        }

        return new ProjectileStats(
            GunStats.fromConfig(stats),

            stats.getDouble("projectile_speed", 2.0),
            stats.getDouble("aoe_radius", 4.0)
        );
    }

}
