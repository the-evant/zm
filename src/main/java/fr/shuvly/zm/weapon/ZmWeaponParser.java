package fr.shuvly.zm.weapon;

import fr.shuvly.zm.Zm;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.logging.Logger;

public class ZmWeaponParser
{

    private static final Zm MAIN = Zm.getInstance();
    private static final Logger LOGGER = MAIN.getLogger();


    private ZmWeaponParser() {}


    /**
     * Parses all weapon .yml files in the map's "weapons" folder.
     *
     * @param   mapFolder   The root folder of the map (e.g., plugins/zm/maps/zm_dev)
     * @return  A fully populated ZmWeaponRegistry
     */
    public static ZmWeaponRegistry parseAll(File mapFolder)
    {
        final ZmWeaponRegistry registry = new ZmWeaponRegistry();
        final File weaponsFolder = new File(mapFolder, "weapons");

        if (!weaponsFolder.exists() || !weaponsFolder.isDirectory()) {
            LOGGER.warning("No 'weapons' directory found in " + mapFolder.getName());
            return registry;
        }

        parseDirectory(weaponsFolder, registry);

        LOGGER.info("Successfully loaded " + registry.getRegisteredWeapons().size() + " weapons for map " + mapFolder.getName());
        return registry;
    }

    private static void parseDirectory(File directory, ZmWeaponRegistry registry)
    {
        final File[] files = directory.listFiles();

        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                parseDirectory(file, registry);
            } else if (file.getName().endsWith(".yml")) {
                try {
                    YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                    registry.loadWeaponFromYaml(config);
                } catch (Exception e) {
                    LOGGER.severe("Failed to parse weapon file '" + file.getName() + "': " + e.getMessage());
                }
            }
        }
    }

}
