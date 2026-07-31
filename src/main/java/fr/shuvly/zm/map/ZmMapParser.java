package fr.shuvly.zm.map;

import fr.shuvly.zm.component.ComponentParser;
import fr.shuvly.zm.component.ComponentRegistry;
import fr.shuvly.zm.exception.MapParseException;
import fr.shuvly.zm.map.region.Region;
import fr.shuvly.zm.map.region.RegionParser;
import fr.shuvly.zm.map.spawnpoints.ZmMapSpawnPoints;
import fr.shuvly.zm.map.spawnpoints.ZmMapSpawnPointsParser;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZmMapParser
{

    private ZmMapParser() {}


    public static ZmMapInfo parseInfo(File mapConfigFile)
        throws MapParseException
    {
        final YamlConfiguration config = YamlConfiguration.loadConfiguration(mapConfigFile);

        final String id = config.getString("map.id");
        final String displayName = config.getString("map.display_name");

        if (id == null || displayName == null) {
            throw new MapParseException("Map file " + mapConfigFile.getName() + " is missing 'id' or 'display_name'.");
        }

        return new ZmMapInfo(id, displayName, mapConfigFile.toString());
    }

    public static ZmMap parse(File mapConfigFile, World world)
        throws MapParseException
    {
        final YamlConfiguration config = YamlConfiguration.loadConfiguration(mapConfigFile);

        final ZmMapInfo info = parseInfo(mapConfigFile);

        final ConfigurationSection spawnsSection = config.getConfigurationSection("spawns");
        if (spawnsSection == null) {
            throw new MapParseException("Map file has no 'spawns' section.");
        }

        final ZmMapSpawnPoints spawnPoints = ZmMapSpawnPointsParser.parse(spawnsSection, world);

        final ConfigurationSection zonesSection = config.getConfigurationSection("zones");
        if (zonesSection == null) {
            throw new MapParseException("Map file has no 'zones' section.");
        }

        final Map<String, Zone> zones = new HashMap<>();

        for (String zoneId : zonesSection.getKeys(false)) {
            final ConfigurationSection zoneSec = zonesSection.getConfigurationSection(zoneId);

            if (zoneSec == null) {
                throw new MapParseException("Zone '" + zoneId + "' section is null.");
            }

            final boolean isUnlocked = zoneSec.getBoolean("unlocked", false);
            final Region region = RegionParser.parse(zoneSec.getConfigurationSection("region"));

            zones.put(zoneId, new Zone(zoneId, region, isUnlocked));
        }

        final ComponentRegistry componentRegistry = new ComponentRegistry();
        final ConfigurationSection componentsSection = config.getConfigurationSection("components");
        
        if (componentsSection != null) {
            ComponentParser.parse(componentsSection, zones, componentRegistry);
        }

        return new ZmMap(
            world,
            info,
            zones,
            componentRegistry,
            spawnPoints
        );
    }

}
