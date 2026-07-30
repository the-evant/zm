package fr.shuvly.zm.map.region.regions;

import fr.shuvly.zm.map.region.Region;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.List;

public record CompositeRegion(List<Region> regions)
    implements Region
{

    @Override
    public boolean contains(double x, double y, double z)
    {
        for (Region region : regions) {
            if (region.contains(x, y, z)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public RayTraceResult rayTrace(Vector start, Vector direction, double maxDistance)
    {
        for (Region region : regions) {
            final RayTraceResult result = region.rayTrace(start, direction, maxDistance);

            if (result != null) {
                return result;
            }
        }
        return null;
    }

}
