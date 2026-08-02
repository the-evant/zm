package fr.shuvly.zm.map.region;

import fr.shuvly.zm.exception.MapParseException;
import fr.shuvly.zm.map.region.regions.CompositeRegion;
import fr.shuvly.zm.map.region.regions.CuboidRegion;
import fr.shuvly.zm.map.region.regions.CylinderRegion;
import fr.shuvly.zm.parser.VectorParser;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RegionParser
{

    private RegionParser() {}


    public static Region parse(ConfigurationSection section)
    {
        if (section == null) {
            throw new MapParseException("Region section is missing.");
        }

        String type = section.getString("type", "CUBOID").toUpperCase();

        return switch (type) {
            case "CUBOID" -> {
                final ConfigurationSection minSec = section.getConfigurationSection("min");
                final ConfigurationSection maxSec = section.getConfigurationSection("max");

                if (minSec == null || maxSec == null) {
                    throw new MapParseException("CUBOID region is missing 'min' or 'max' coordinates.");
                }

                final Vector min = VectorParser.parseVector(minSec);
                final Vector max = VectorParser.parseVector(maxSec);

                yield new CuboidRegion(
                    new BoundingBox(
                        Math.min(min.getX(), max.getX()), Math.min(min.getY(), max.getY()), Math.min(min.getZ(), max.getZ()),
                        Math.max(min.getX(), max.getX()) + 1.0, Math.max(min.getY(), max.getY()) + 1.0, Math.max(min.getZ(), max.getZ()) + 1.0
                    )
                );
            }

            case "CYLINDER" -> {
                double y1 = section.getDouble("min_y");
                double y2 = section.getDouble("max_y");

                yield CylinderRegion.fromRadius(
                    section.getDouble("center_x") + 0.5,
                    section.getDouble("center_z") + 0.5,
                    section.getDouble("radius"),
                    Math.min(y1, y2), Math.max(y1, y2) + 1.0
                );
            }

            case "COMPOSITE" -> {
                final List<Map<?, ?>> partsList = section.getMapList("parts");
                if (partsList.isEmpty()) {
                    throw new MapParseException("COMPOSITE region has no 'parts' defined.");
                }

                final List<Region> regions = new ArrayList<>();

                for (Map<?, ?> partMap : partsList) {
                    final MemoryConfiguration tempConfig = new MemoryConfiguration();

                    tempConfig.createSection("temp", partMap);
                    regions.add(parse(tempConfig.getConfigurationSection("temp")));
                }
                yield new CompositeRegion(regions);
            }

            default -> throw new MapParseException("Unknown region type: " + type);
        };
    }

}
