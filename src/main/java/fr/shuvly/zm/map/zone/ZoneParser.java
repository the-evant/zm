package fr.shuvly.zm.map.zone;

import fr.shuvly.zm.exception.MapParseException;
import fr.shuvly.zm.map.region.Region;
import fr.shuvly.zm.map.region.RegionParser;
import org.bukkit.configuration.ConfigurationSection;

public class ZoneParser
{

    public static Zone parse(String id, ConfigurationSection config)
        throws MapParseException
    {
        if (config == null) {
            throw new MapParseException("Configuration for zone '" + id + "' is null.");
        }

        final boolean unlocked = config.getBoolean("unlocked", false);
        final ConfigurationSection regionSec = config.getConfigurationSection("region");

        if (regionSec == null) {
            throw new MapParseException("Zone '" + id + "' is missing a 'region' section.");
        }

        final Region region = RegionParser.parse(regionSec);

        return new Zone(id, region, unlocked);
    }

}
