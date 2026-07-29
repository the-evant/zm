package fr.shuvly.zm.map.region;

import fr.shuvly.zm.exception.MapParseException;
import fr.shuvly.zm.map.region.regions.CompositeRegion;
import fr.shuvly.zm.map.region.regions.CuboidRegion;
import fr.shuvly.zm.map.region.regions.CylinderRegion;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.util.BoundingBox;

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
                final ConfigurationSection min = section.getConfigurationSection("min");
                final ConfigurationSection max = section.getConfigurationSection("max");

                if (min == null || max == null) {
                    throw new MapParseException("CUBOID region is missing 'min' or 'max' coordinates.");
                }

                double x1 = min.getDouble("x");
                double y1 = min.getDouble("y");
                double z1 = min.getDouble("z");

                double x2 = max.getDouble("x");
                double y2 = max.getDouble("y");
                double z2 = max.getDouble("z");

                yield new CuboidRegion(
                    new BoundingBox(
                        Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2),
                        Math.max(x1, x2) + 1.0, Math.max(y1, y2) + 1.0, Math.max(z1, z2) + 1.0
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
