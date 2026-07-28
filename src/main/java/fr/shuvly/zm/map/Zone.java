package fr.shuvly.zm.map;

import fr.shuvly.zm.map.region.Region;
import org.bukkit.Location;

public class Zone
{

    private final String id;
    private final Region region;
    private boolean isUnlocked;


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


    public void setUnlocked(boolean unlocked) { this.isUnlocked = unlocked; }

    public String getId() { return id; }
    public Region getRegion() { return region; }
    public boolean isUnlocked() { return isUnlocked; }

}
