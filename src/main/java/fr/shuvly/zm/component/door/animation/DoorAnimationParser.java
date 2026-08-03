package fr.shuvly.zm.component.door.animation;

import fr.shuvly.zm.exception.MapParseException;
import fr.shuvly.zm.map.region.Region;
import fr.shuvly.zm.map.region.RegionParser;
import fr.shuvly.zm.parser.VectorParser;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DoorAnimationParser
{

    private DoorAnimationParser() {}


    public static DoorAnimation parse(ConfigurationSection section, String doorId)
        throws MapParseException
    {
        if (section == null) {
            throw new MapParseException("DOOR '" + doorId + "' have no animation set.");
        }

        String type = section.getString("type");

        if (type == null) {
            throw new MapParseException("DOOR '" + doorId + "' have no animation type set.");
        }

        type = type.toUpperCase();

        if ("INSTANT".equals(type)) {
            final ConfigurationSection regionSec = section.getConfigurationSection("region");

            if (regionSec == null) {
                throw new MapParseException("DOOR '" + doorId + "': INSTANT animation requires 'blocks_region'.");
            }

            final Region region = RegionParser.parse(regionSec);

            return new InstantDoorAnimation(region);
        }

        if ("MULTI_PART".equals(type)) {
            int duration = section.getInt("duration", 20);
            List<Map<?, ?>> partsList = section.getMapList("parts");

            if (partsList.isEmpty()) {
                throw new MapParseException("DOOR '" + doorId + "': MULTIPART animation requires a 'parts' list.");
            }

            List<DoorAnimationPart> parsedParts = new ArrayList<>();
            MemoryConfiguration tempConfig = new MemoryConfiguration();

            for (int i = 0; i < partsList.size(); i++) {
                final ConfigurationSection partSec = tempConfig.createSection("part_" + i, partsList.get(i));
                final ConfigurationSection regionSec = partSec.getConfigurationSection("region");

                if (regionSec == null) {
                    throw new MapParseException("DOOR '" + doorId + "': Animation part " + i + " is missing 'region'.");
                }

                final Region region = RegionParser.parse(regionSec);

                final Vector pivot = VectorParser.parseVector(partSec.getConfigurationSection("pivot"));
                final Vector3f translation = VectorParser.parseVector3f(partSec.getConfigurationSection("translation"));
                final Vector3f rotation = VectorParser.parseVector3f(partSec.getConfigurationSection("rotation"));

                parsedParts.add(new DoorAnimationPart(region, pivot, translation, rotation));
            }

            return new MultiPartDoorAnimation(parsedParts, duration);
        }

        throw new MapParseException("DOOR '" + doorId + "': Unknown animation type: " + type);
    }

}
