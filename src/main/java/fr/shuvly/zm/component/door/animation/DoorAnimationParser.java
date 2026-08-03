package fr.shuvly.zm.component.door.animation;

import fr.shuvly.zm.component.door.DoorType;
import fr.shuvly.zm.component.door.animation.transform.MultiTransformDoorAnimation;
import fr.shuvly.zm.exception.MapParseException;
import fr.shuvly.zm.map.region.Region;
import fr.shuvly.zm.map.region.RegionParser;
import fr.shuvly.zm.parser.VectorParser;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.util.BoundingBox;
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

        final String type = section.getString("type");

        if (type == null) {
            throw new MapParseException("DOOR '" + doorId + "' have no animation type set.");
        }

        int duration = section.getInt("duration", 1000);

        switch (type.toUpperCase()) {
            case "INSTANT" -> {
                final ConfigurationSection regionSec = section.getConfigurationSection("region");

                if (regionSec == null) {
                    throw new MapParseException("DOOR '" + doorId + "': INSTANT animation requires 'region'.");
                }

                return new InstantDoorAnimation(RegionParser.parse(regionSec));
            }

            case "TRANSFORM" -> {
                final DoorAnimationPart singlePart = parsePart(section, doorId, "main");
                return new MultiTransformDoorAnimation(List.of(singlePart), duration);
            }

            case "MULTI_TRANSFORM" -> {
                final List<Map<?, ?>> partsList = section.getMapList("parts");

                if (partsList.isEmpty()) {
                    throw new MapParseException("DOOR '" + doorId + "': MULTI_TRANSFORM animation requires a 'parts' list.");
                }

                final List<DoorAnimationPart> parsedParts = new ArrayList<>();
                final MemoryConfiguration tempConfig = new MemoryConfiguration();

                for (int i = 0; i < partsList.size(); i++) {
                    final ConfigurationSection partSec = tempConfig.createSection("part_" + i, partsList.get(i));
                    parsedParts.add(parsePart(partSec, doorId, "part " + i));
                }

                return new MultiTransformDoorAnimation(parsedParts, duration);
            }

            default -> throw new MapParseException("DOOR '" + doorId + "': Unknown animation type: " + type);
        }
    }

    private static DoorAnimationPart parsePart(
        ConfigurationSection partSec,
        String doorId,
        String context
    )
        throws MapParseException
    {
        final ConfigurationSection regionSec = partSec.getConfigurationSection("region");
        if (regionSec == null) {
            throw new MapParseException("DOOR '" + doorId + "': Animation " + context + " is missing 'region'.");
        }

        final Region region = RegionParser.parse(regionSec);

        final ConfigurationSection transSec = partSec.getConfigurationSection("translation");
        final ConfigurationSection rotationSec = partSec.getConfigurationSection("rotation");
        final ConfigurationSection scaleSec = partSec.getConfigurationSection("scale");

        if (transSec == null && rotationSec == null && scaleSec == null) {
            throw new MapParseException("DOOR '" + doorId + "': Animation " + context +
                " must specify at least a 'translation', a 'rotation', or a 'scale'.");
        }

        final Vector3f translation = transSec != null
            ? VectorParser.parseVector3f(transSec)
            : new Vector3f(0f, 0f, 0f);

        final Vector3f rotation = rotationSec != null
            ? VectorParser.parseVector3f(rotationSec)
            : new Vector3f(0f, 0f, 0f);

        final Vector3f scale = scaleSec != null
            ? VectorParser.parseVector3f(scaleSec)
            : new Vector3f(1f, 1f, 1f);

        final String scaleAnchorStr = partSec.getString("scale_anchor", "BLOCK_CENTER");
        final DoorAnimationScaleAnchor scaleAnchor;

        try {
            scaleAnchor = DoorAnimationScaleAnchor.valueOf(scaleAnchorStr);
        } catch (IllegalArgumentException e) {
            throw new MapParseException("DOOR '" + doorId + "': Unknown scale anchor: " + scaleAnchorStr);
        }

        final ConfigurationSection pivotSec = partSec.getConfigurationSection("pivot");
        final Vector pivot;

        if (pivotSec != null) {
            pivot = VectorParser.parseVector(pivotSec);
        } else {
            final BoundingBox box = region.getBoundingBox();
            pivot = new Vector(
                Math.floor(box.getCenterX()),
                Math.floor(box.getCenterY()),
                Math.floor(box.getCenterZ())
            );
        }

        final int delay = partSec.getInt("delay", 0);

        final String modeStr = partSec.getString("completion_mode", "DISPLAY").toUpperCase();
        final DoorAnimationCompletionMode completionMode;
        try {
            completionMode = DoorAnimationCompletionMode.valueOf(modeStr);
        } catch (IllegalArgumentException e) {
            throw new MapParseException("DOOR '" + doorId + "': Unknown completion_mode: " + modeStr);
        }

        return new DoorAnimationPart(region, pivot, translation, rotation, scale, delay, completionMode, scaleAnchor);
    }

}
