package fr.shuvly.zm.component;

import fr.shuvly.zm.component.door.DoorComponentParser;
import fr.shuvly.zm.component.wallbuy.WallbuyComponentParser;
import fr.shuvly.zm.map.Zone;
import fr.shuvly.zm.weapon.ZmWeaponRegistry;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

public class ComponentParser
{

    private ComponentParser() {}


    public static void parse(
        ConfigurationSection componentsSection,
        Map<String, Zone> zones,
        ZmWeaponRegistry weaponRegistry,
        ComponentRegistry componentRegistry
    )
    {
        if (componentsSection == null) {
            return;
        }

        final ConfigurationSection doorSec = componentsSection.getConfigurationSection("doors");

        if (doorSec != null) {
            DoorComponentParser.parse(doorSec, zones, componentRegistry);
        }

        final ConfigurationSection wallbuySec = componentsSection.getConfigurationSection("wallbuys");

        if (wallbuySec != null) {
            WallbuyComponentParser.parse(wallbuySec, weaponRegistry, componentRegistry);
        }
    }

}
