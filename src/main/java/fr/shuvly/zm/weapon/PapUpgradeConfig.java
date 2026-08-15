package fr.shuvly.zm.weapon;

import org.bukkit.configuration.ConfigurationSection;

public record PapUpgradeConfig(
    String weaponId,
    int upgradeTimeTicks,
    int pickupTimeoutTicks,
    int cost
)
{

    /**
     * Parses the Pack-a-Punch configuration section.
     *
     * @param   config  The "pap" configuration section.
     * @return  A new PapUpgradeConfig, or null if the section doesn't exist.
     */
    public static PapUpgradeConfig fromConfig(ConfigurationSection config)
    {
        if (config == null) {
            return null;
        }

        return new PapUpgradeConfig(
            config.getString("weapon_id"),
            config.getInt("upgrade_time", -1),
            config.getInt("pickup_timeout", -1),
            config.getInt("cost", -1)
        );
    }

}
