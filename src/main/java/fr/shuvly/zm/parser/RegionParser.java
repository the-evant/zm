package fr.shuvly.zm.parser;

import fr.shuvly.zm.exception.MapParseException;
import fr.shuvly.zm.map.region.Region;
import fr.shuvly.zm.map.region.regions.CompositeRegion;
import fr.shuvly.zm.map.region.regions.CuboidRegion;
import fr.shuvly.zm.map.region.regions.CylinderRegion;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

                yield new CuboidRegion(
                    new BoundingBox(
                        min.getDouble("x"), min.getDouble("y"), min.getDouble("z"),
                        max.getDouble("x"), max.getDouble("y"), max.getDouble("z")
                    )
                );
            }

            case "CYLINDER" -> CylinderRegion.fromRadius(
                section.getDouble("center_x"), section.getDouble("center_z"),
                section.getDouble("radius"),
                section.getDouble("min_y"), section.getDouble("max_y")
            );

            case "COMPOSITE" -> {
                final List<Map<?, ?>> partsList = section.getMapList("parts");
                if (partsList.isEmpty()) {
                    throw new MapParseException("COMPOSITE region has no 'parts' defined.");
                }

                final List<Region> regions = new ArrayList<>();

                for (int i = 0; i < partsList.size(); i++) {
                    // Wrap the raw map back into a ConfigurationSection for recursive parsing
                    final ConfigurationSection partSection = Objects.requireNonNull(section.getRoot()).createSection("temp_part_" + i, partsList.get(i));
                    regions.add(parse(partSection));
                }
                yield new CompositeRegion(regions);
            }

            default -> throw new MapParseException("Unknown region type: " + type);
        };
    }

}
