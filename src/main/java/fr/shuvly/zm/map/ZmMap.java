package fr.shuvly.zm.map;

import org.bukkit.Location;

import java.util.List;
import java.util.Map;

public class ZmMap
{

    private final ZmMapInfo info;
    private final List<Location> spawnPoints;
    private final Map<String, Zone> zones;


    public ZmMap(
        ZmMapInfo info,
        List<Location> spawnPoints,
        Map<String, Zone> zones
    )
    {
        this.info = info;
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


    public ZmMapInfo getInfo() { return info; }
    public List<Location> getSpawnPoints() { return spawnPoints; }

}
