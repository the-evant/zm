package fr.shuvly.zm.component.door;

import fr.shuvly.zm.component.ComponentFactory;
import fr.shuvly.zm.component.door.animation.DoorAnimationParser;
import fr.shuvly.zm.component.door.animation.DoorAnimation;
import fr.shuvly.zm.component.interaction.InteractionTrigger;
import fr.shuvly.zm.component.interaction.TriggerParser;
import fr.shuvly.zm.exception.MapParseException;
import fr.shuvly.zm.map.MapParsingContext;
import fr.shuvly.zm.map.zone.Zone;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class DoorComponentParser
    implements ComponentFactory<DoorComponent>
{

    @Override
    public DoorComponent parse(
        String id,
        ConfigurationSection config,
        MapParsingContext context
    )
    {
        final ConfigurationSection triggerSec = config.getConfigurationSection("trigger");

        if (triggerSec == null) {
            throw new MapParseException("DOOR '" + id + "' trigger has not been found.");
        }

        final InteractionTrigger trigger = TriggerParser.parse(triggerSec);
        final int cost = config.getInt("cost", 0);
        final String typeStr = config.getString("type", "DOOR").toUpperCase();

        final DoorType type;
        try {
            type = DoorType.valueOf(typeStr);
        } catch (IllegalArgumentException e) {
            throw new MapParseException("DOOR '" + id + "': Unknown door type: " + typeStr);
        }

        final ConfigurationSection animSec = config.getConfigurationSection("animation");
        final DoorAnimation animation = DoorAnimationParser.parse(animSec, id);

        final DoorComponent door = new DoorComponent(id, trigger, type, cost, animation, context.componentRegistry());

        final List<String> connects = config.getStringList("connects");
        for (String zoneId : connects) {
            final Zone zone = context.getZone(zoneId);
            door.addTargetZone(zone);
        }

        for (Zone z1 : door.getTargetZones()) {
            for (Zone z2 : door.getTargetZones()) {
                if (z1 != z2) {
                    z1.addAdjacentZone(z2);
                }
            }
        }

        final List<String> unlocksDoors = config.getStringList("unlocks_doors");

        for (String linkedDoorId : unlocksDoors) {
            if (config.getParent() != null && !config.getParent().contains(linkedDoorId)) {
                throw new MapParseException("DOOR '" + id + "' tries to unlock '" + linkedDoorId + "' but it does not exist.");
            }
            door.addUnlockDoor(linkedDoorId);
        }

        return door;
    }

}
