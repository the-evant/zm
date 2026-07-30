package fr.shuvly.zm.map.region;

import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public interface Region
{

    /**
     * Evaluates if the given coordinates fall inside this region.
     */
    boolean contains(double x, double y, double z);

    /**
     * Performs a mathematical raytrace against this region's geometry.
     */
    RayTraceResult rayTrace(Vector start, Vector direction, double maxDistance);

}
