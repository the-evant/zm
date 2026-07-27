package fr.shuvly.zm.map.region.regions;

import fr.shuvly.zm.map.region.Region;

public record CylinderRegion(
    double centerX,
    double centerZ,
    double radiusSquared,
    double minY,
    double maxY
)
    implements Region
{

    public static CylinderRegion fromRadius(
        double centerX,
        double centerZ,
        double radius,
        double minY,
        double maxY
    )
    {
        return new CylinderRegion(centerX, centerZ, radius * radius, minY, maxY);
    }


    @Override
    public boolean contains(double x, double y, double z)
    {
        if (y < minY || y > maxY) {
            return false;
        }

        double dx = x - centerX;
        double dz = z - centerZ;

        return (dx * dx + dz * dz) <= radiusSquared;
    }

}
