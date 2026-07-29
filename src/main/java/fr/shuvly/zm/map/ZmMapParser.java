package fr.shuvly.zm.map;

import fr.shuvly.zm.component.ComponentParser;
import fr.shuvly.zm.component.ComponentRegistry;
import fr.shuvly.zm.exception.MapParseException;
import fr.shuvly.zm.map.region.Region;
import fr.shuvly.zm.map.region.RegionParser;
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


    public static ZmMap parse(File mapConfigFile)
    {
        final YamlConfiguration config = YamlConfiguration.loadConfiguration(mapConfigFile);

        final String id = config.getString("map.id");
        final String displayName = config.getString("map.display_name");
        final String worldName = config.getString("map.world_name");

        if (id == null || displayName == null || worldName == null) {
            throw new MapParseException("Map file " + mapConfigFile.getName() + " is missing 'id', 'display_name' or 'world_name'.");
        }

        final World world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new MapParseException("World '" + worldName + "' is not loaded on the server.");
        }

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
            new ZmMapInfo(id, displayName, worldName, mapConfigFile.toString()),
            List.of(),
            zones,
            componentRegistry
        );
    }

}
