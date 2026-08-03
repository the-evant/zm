package fr.shuvly.zm.component.door;

import fr.shuvly.zm.component.ComponentRegistry;
import fr.shuvly.zm.component.door.animation.DoorAnimationParser;
import fr.shuvly.zm.component.door.animation.DoorAnimation;
import fr.shuvly.zm.component.interaction.InteractionTrigger;
import fr.shuvly.zm.component.interaction.TriggerParser;
import fr.shuvly.zm.exception.MapParseException;
import fr.shuvly.zm.map.Zone;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Map;

public class DoorComponentParser
{

    private DoorComponentParser() {}


    public static void parse(
        ConfigurationSection doorsSection,
        Map<String, Zone> zones,
        ComponentRegistry registry
    )
        throws MapParseException
    {
        if (doorsSection == null) {
            return;
        }

        for (String doorId : doorsSection.getKeys(false)) {
            final ConfigurationSection sec = doorsSection.getConfigurationSection(doorId);

            if (sec == null) {
                continue;
            }

            final List<String> connects = sec.getStringList("connects");

            final ConfigurationSection triggerSec = sec.getConfigurationSection("trigger");
            if (triggerSec == null) {
                throw new MapParseException("DOOR '" + doorId + "' trigger has not been found.");
            }

            final InteractionTrigger trigger = TriggerParser.parse(triggerSec);

            final int cost = sec.getInt("cost", 0); // default price of doors : 0 (free)
            final String typeStr = sec.getString("type", "DOOR").toUpperCase();
            
            final DoorType type;

            try {
                type = DoorType.valueOf(typeStr);
            } catch (IllegalArgumentException e) {
                throw new MapParseException("DOOR '" + doorId + "': Unknown door type: " + typeStr);
            }

            final ConfigurationSection animSec = sec.getConfigurationSection("animation");
            final DoorAnimation animation = DoorAnimationParser.parse(animSec, doorId);

            final DoorComponent door = new DoorComponent(doorId, trigger, type, cost, animation, registry);

            for (String zoneId : connects) {
                final Zone zone = zones.get(zoneId);

                if (zone != null) {
                    door.addTargetZone(zone);
                } else {
                    throw new MapParseException("DOOR '" + doorId + "': Zone '" + zoneId + "' does not exist.");
                }
            }

            for (Zone z1 : door.getTargetZones()) {
                for (Zone z2 : door.getTargetZones()) {
                    if (z1 != z2) {
                        z1.addAdjacentZone(z2);
                    }
                }
            }

            final List<String> unlocksDoors = sec.getStringList("unlocks_doors");

            for (String linkedDoorId : unlocksDoors) {
                if (!doorsSection.contains(linkedDoorId)) {
                    throw new MapParseException("DOOR '" + doorId + "' tries to unlock '" + linkedDoorId + "' but '" + linkedDoorId + "' does not exist in the config.");
                } else {
                    door.addUnlockDoor(linkedDoorId);
                }
            }

            registry.register(door);
        }
    }

}
