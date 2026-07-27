package fr.shuvly.zm.map.region.regions;

import fr.shuvly.zm.map.region.Region;

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

}
