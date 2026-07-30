package fr.shuvly.zm.component.interaction;

import fr.shuvly.zm.exception.MapParseException;
import fr.shuvly.zm.map.region.Region;
import fr.shuvly.zm.map.region.RegionParser;
import org.bukkit.configuration.ConfigurationSection;

public class TriggerParser
{

    private TriggerParser() {}


    public static InteractionTrigger parse(ConfigurationSection section)
    {
        final ConfigurationSection regionSec = section.getConfigurationSection("region");

        if (regionSec == null) {
            throw new MapParseException("Region has not been found.");
        }

        final Region region = RegionParser.parse(regionSec);

        final String type = section.getString("type", "ZONE");

        return switch (type) {
            case "ZONE" -> new ZoneTrigger(region);
            case "LOOK" -> {
                final double maxDistance = section.getDouble("max_distance", 5.0);
                yield new LookTrigger(region, maxDistance);
            }
            default -> throw new MapParseException("Unknown trigger type: " + type);
        };
    }

}
