package fr.shuvly.zm.map;

import fr.shuvly.zm.component.ComponentLoader;
import fr.shuvly.zm.component.ComponentRegistry;
import fr.shuvly.zm.exception.MapParseException;
import fr.shuvly.zm.map.spawnpoints.ZmMapSpawnPoints;
import fr.shuvly.zm.map.spawnpoints.ZmMapSpawnPointsParser;
import fr.shuvly.zm.map.zone.Zone;
import fr.shuvly.zm.map.zone.ZoneParser;
import fr.shuvly.zm.perk.ZmPerkRegistry;
import fr.shuvly.zm.weapon.ZmWeaponLoader;
import fr.shuvly.zm.weapon.ZmWeaponRegistry;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ZmMapLoader
{

    private ZmMapLoader() {}


    public static ZmMapInfo parseInfo(
        ConfigurationSection mapConfig,
        File mapFolder
    )
        throws MapParseException
    {
        final String id = mapConfig.getString("map.id");
        final String displayName = mapConfig.getString("map.display_name");

        if (id == null || displayName == null) {
            throw new MapParseException("Map file " + mapFolder.getName() + " is missing 'map.id' or 'map.display_name'.");
        }

        return new ZmMapInfo(id, displayName, mapFolder.getAbsolutePath());
    }

    public static ZmMapInfo parseInfo(File mapConfigFile)
        throws MapParseException
    {
        final YamlConfiguration config = YamlConfiguration.loadConfiguration(mapConfigFile);

        return parseInfo(config, mapConfigFile.getParentFile());
    }

    public static ZmMap load(File mapDir, World world)
        throws MapParseException
    {
        if (!mapDir.exists() || !mapDir.isDirectory()) {
            throw new MapParseException("Map directory does not exist: " + mapDir.getPath());
        }

        final File configFile = new File(mapDir, "config.yml");
        if (!configFile.exists()) {
            throw new MapParseException("Map directory is missing 'config.yml'.");
        }

        final YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        final ZmMapInfo info = parseInfo(config, mapDir);

        final ConfigurationSection spawnsSection = config.getConfigurationSection("spawns");
        if (spawnsSection == null) {
            throw new MapParseException("Map file has no 'spawns' section.");
        }
        final ZmMapSpawnPoints spawnPoints = ZmMapSpawnPointsParser.parse(spawnsSection, world);

        final File zonesFile = new File(mapDir, "zones.yml");
        if (!zonesFile.exists()) {
            throw new MapParseException("Map directory is missing 'zones.yml'.");
        }

        final YamlConfiguration zonesConfig = YamlConfiguration.loadConfiguration(zonesFile);
        final Map<String, Zone> zones = new HashMap<>();

        for (String zoneId : zonesConfig.getKeys(false)) {
            final ConfigurationSection zoneSec = zonesConfig.getConfigurationSection(zoneId);
            zones.put(zoneId, ZoneParser.parse(zoneId, zoneSec));
        }

        final ZmWeaponRegistry weaponRegistry = new ZmWeaponRegistry();
        final File weaponsDir = new File(mapDir, "weapons");
        ZmWeaponLoader.loadAllWeapons(weaponsDir, weaponRegistry);

        final ComponentRegistry componentRegistry = new ComponentRegistry();
        final ZmPerkRegistry perkRegistry = new ZmPerkRegistry();

        final MapParsingContext context = new MapParsingContext(
            world, info, zones,
            weaponRegistry, componentRegistry, perkRegistry
        );

        final File componentsDir = new File(mapDir, "components");
        ComponentLoader.loadComponents(componentsDir, context);

        return new ZmMap(
            world,
            info,
            zones,
            componentRegistry,
            weaponRegistry,
            spawnPoints
        );
    }
}
