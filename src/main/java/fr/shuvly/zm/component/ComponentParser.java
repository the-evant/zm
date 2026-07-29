package fr.shuvly.zm.component;

import fr.shuvly.zm.component.door.DoorComponentParser;
import fr.shuvly.zm.map.Zone;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

public class ComponentParser
{

    private ComponentParser() {}


    public static void parse(
        ConfigurationSection componentsSection,
        Map<String, Zone> zones,
        ComponentRegistry registry
    )
    {
        if (componentsSection == null) {
            return;
        }

        final ConfigurationSection doorSec = componentsSection.getConfigurationSection("doors");

        if (doorSec != null) {
            DoorComponentParser.parse(doorSec, zones, registry);
        }
    }

}
