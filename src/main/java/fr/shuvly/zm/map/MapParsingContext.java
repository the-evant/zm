package fr.shuvly.zm.map;

import fr.shuvly.zm.component.ComponentRegistry;
import fr.shuvly.zm.exception.MapParseException;
import fr.shuvly.zm.map.zone.Zone;
import fr.shuvly.zm.perk.ZmPerkRegistry;
import fr.shuvly.zm.weapon.ZmWeaponRegistry;
import org.bukkit.World;

import java.util.Map;

public record MapParsingContext(
    World world,
    ZmMapInfo mapInfo,
    Map<String, Zone> loadedZones,
    ZmWeaponRegistry weaponRegistry,
    ComponentRegistry componentRegistry,
    ZmPerkRegistry perkRegistry
)
{

    public Zone getZone(String id)
    {
        final Zone zone = loadedZones.get(id);

        if (zone == null) {
            throw new MapParseException("Zone '" + id + "' does not exist.");
        }
        return zone;
    }

}
