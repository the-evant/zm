package fr.shuvly.zm.weapon;

import fr.shuvly.zm.exception.MapParseException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ZmWeaponLoader
{

    private ZmWeaponLoader() {}


    /**
     * Scans the weapons directory, parses all weapon configurations,
     * and registers them into the provided registry.
     */
    public static void loadAllWeapons(File weaponsDir, ZmWeaponRegistry registry)
    {
        if (weaponsDir == null || !weaponsDir.exists() || !weaponsDir.isDirectory()) {
            return;
        }

        final File[] files = weaponsDir.listFiles((_, name) -> name.endsWith(".yml"));

        if (files == null) {
            return;
        }

        for (File file : files) {
            final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            final String weaponId = config.getString("id", "UNKNOWN");

            try {
                final ZmWeapon weapon = ZmWeaponParser.parse(weaponId, config);

                registry.register(weaponId, weapon);
            } catch (MapParseException e) {
                System.err.println("Failed to load weapon '" + weaponId + "': " + e.getMessage());
            }
        }
    }

}
