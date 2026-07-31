package fr.shuvly.zm.map.region.regions;

import fr.shuvly.zm.map.region.Region;
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
        // if player is in the region, no need for complex raytrace stuff
        if (contains(start.getX(), start.getY(), start.getZ())) {
            return new RayTraceResult(start.clone());
        }

        double ox = start.getX();
        double oy = start.getY();
        double oz = start.getZ();

        double dx = direction.getX();
        double dy = direction.getY();
        double dz = direction.getZ();

        double tHit = -1.0; // distance to closest hit

        // 1 - intersect with the infinite cylinder walls (XZ plane) //
        double deltaX = ox - centerX;
        double deltaZ = oz - centerZ;

        // quadratic equation coefficients: `at^2 + bt + c = 0` (term sp. math lol)
        double a = (dx * dx) + (dz * dz);
        double b = 2.0 * ((dx * deltaX) + (dz * deltaZ));
        double c = (deltaX * deltaX) + (deltaZ * deltaZ) - radiusSquared;

        // if `a` is extremely close to 0, the ray is pointing straight up or down
        if (a > 1e-6) {
            double discriminant = (b * b) - (4 * a * c);

            if (discriminant >= 0) {
                double sqrtDisc = Math.sqrt(discriminant);
                double t1 = (-b - sqrtDisc) / (2 * a);
                double t2 = (-b + sqrtDisc) / (2 * a);

                // we want smallest positive distance
                double t = -1;

                if (t1 >= 0 && t1 <= maxDistance) {
                    t = t1;
                } else if (t2 >= 0 && t2 <= maxDistance) {
                    t = t2;
                }

                if (t != -1) {
                    // we hit the infinite cylinder. Now, is the hit between our Y caps?
                    double hitY = oy + (t * dy);
                    if (hitY >= minY && hitY <= maxY) {
                        tHit = t;
                    }
                }
            }
        }

        // 2 - intersect with the flat caps (top & bottom) //
        // if the player is looking down at the top, or up at the bottom.
        if (Math.abs(dy) > 1e-6) {
            // bottom cap (plane y = minY)
            tHit = getTHit(maxDistance, ox, oy, oz, dx, dy, dz, tHit, minY);

            // top Cap (plane y = maxY)
            tHit = getTHit(maxDistance, ox, oy, oz, dx, dy, dz, tHit, maxY);
        }

        if (tHit != -1.0) {
            // compute exact 3D coordinate where the player's view hits the cylinder
            final Vector hitPosition = start.clone().add(direction.clone().multiply(tHit));

            return new RayTraceResult(hitPosition);
        }

        return null;
    }

    private double getTHit(
        double maxDistance,
        double ox, double oy, double oz,
        double dx, double dy, double dz,
        double tHit,
        double minY
    )
    {
        double tBottom = (minY - oy) / dy;
        if (tBottom >= 0 && tBottom <= maxDistance && (tHit == -1.0 || tBottom < tHit)) {
            double hitX = ox + (tBottom * dx);
            double hitZ = oz + (tBottom * dz);

            // is hit point within the circle of the base?
            if (Math.pow(hitX - centerX, 2) + Math.pow(hitZ - centerZ, 2) <= radiusSquared) {
                tHit = tBottom;
            }
        }
        return tHit;
    }

}
