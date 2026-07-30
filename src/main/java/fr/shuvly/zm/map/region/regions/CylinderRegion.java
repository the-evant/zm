package fr.shuvly.zm.map.region.regions;

import fr.shuvly.zm.map.region.Region;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

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

    @Override
    public RayTraceResult rayTrace(Vector start, Vector direction, double maxDistance)
    {
        double radius = Math.sqrt(radiusSquared);

        final BoundingBox wrapperBox = new BoundingBox(
            centerX - radius, minY, centerZ - radius,
            centerX + radius, maxY, centerZ + radius
        );
        final RayTraceResult hit = wrapperBox.rayTrace(start, direction, maxDistance);

        if (hit == null) {
            return null;
        }

        // if it hits a cuboid region that fits the cylinder, then perform a more precise check to determine if the ray
        // actually hits the cylinder shape.

        final Vector pos = hit.getHitPosition();
        final double dx = pos.getX() - centerX;
        final double dz = pos.getZ() - centerZ;

        if (dx * dx + dz * dz <= radiusSquared) {
            return hit;
        }

        return null;
    }

}
