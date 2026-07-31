package fr.shuvly.zm.map;

import org.bukkit.Location;

import fr.shuvly.zm.component.ComponentRegistry;
import org.bukkit.World;

import java.util.List;
import java.util.Map;

public class ZmMap
{

    private final ZmMapInfo info;

    private final World world;

    private final Map<String, Zone> zones;
    private final ComponentRegistry componentRegistry;
    private final List<Location> spawnPoints;


    public ZmMap(
        World world,
        ZmMapInfo info,
        Map<String, Zone> zones,
        ComponentRegistry componentRegistry,
        List<Location> spawnPoints
    )
    {
        this.world = world;
        this.info = info;
        this.zones = zones;
        this.componentRegistry = componentRegistry;
        this.spawnPoints = spawnPoints;
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
    public World getWorld() { return world; }
    public List<Location> getSpawnPoints() { return spawnPoints; }
    public ComponentRegistry getComponentRegistry() { return componentRegistry; }

}
