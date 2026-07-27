package fr.shuvly.zm.map.region.regions;

import fr.shuvly.zm.map.region.Region;
import org.bukkit.util.BoundingBox;

public record CuboidRegion(BoundingBox box)
    implements Region
{

    @Override
    public boolean contains(double x, double y, double z)
    {
        return box.contains(x, y, z);
    }

}
