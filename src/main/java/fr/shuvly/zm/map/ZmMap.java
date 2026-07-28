package fr.shuvly.zm.map;

import org.bukkit.Location;

import java.util.List;
import java.util.Map;

public class ZmMap
{

    private final String name;          // "nacht_der_untoten"
    private final String displayName;   // "Nacht der Untoten"
    private final String worldName;     // "zm_nacht"

    private final List<Location> spawnPoints;
    private final Map<String, Zone> zones;


    public ZmMap(
        String name,
        String displayName,
        String worldName,
        List<Location> spawnPoints,
        Map<String, Zone> zones
    )
    {
        this.name = name;
        this.displayName = displayName;
        this.worldName = worldName;
        this.spawnPoints = spawnPoints;
        this.zones = zones;
    }


    public Zone getZoneAt(Location location)
    {
        for (Zone zone : zones.values()) {
            if (zone.contains(location)) {
                return zone;
            }
        }
        return null;
    }


    public String getName() { return name; }
    public String getDisplayName() { return displayName; }
    public String getWorldName() { return worldName; }
    public List<Location> getSpawnPoints() { return spawnPoints; }

}
