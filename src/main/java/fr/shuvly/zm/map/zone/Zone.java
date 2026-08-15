package fr.shuvly.zm.map.zone;

import fr.shuvly.zm.map.region.Region;
import org.bukkit.Location;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Zone
{

    private final String id;
    private final Region region;
    private boolean isUnlocked;
    private final Set<Zone> neighbours = new HashSet<>();


    public Zone(
        String id,
        Region region,
        boolean isUnlocked
    )
    {
        this.id = id;
        this.region = region;
        this.isUnlocked = isUnlocked;
    }


    public boolean contains(Location location)
    {
        return region.contains(location.getX(), location.getY(), location.getZ());
    }

    public void addAdjacentZone(Zone zone) { this.neighbours.add(zone); }
    public void setUnlocked(boolean unlocked) { this.isUnlocked = unlocked; }

    public String getId() { return id; }
    public Region getRegion() { return region; }
    public boolean isUnlocked() { return isUnlocked; }
    public Set<Zone> getNeighbours() { return Collections.unmodifiableSet(neighbours); }

}
