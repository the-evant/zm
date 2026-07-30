package fr.shuvly.zm.map.region.regions;

import fr.shuvly.zm.map.region.Region;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public record CuboidRegion(BoundingBox box)
    implements Region
{

    @Override
    public boolean contains(double x, double y, double z)
    {
        return box.contains(x, y, z);
    }

    @Override
    public RayTraceResult rayTrace(Vector start, Vector direction, double maxDistance)
    {
        return box.rayTrace(start, direction, maxDistance);
    }

}
