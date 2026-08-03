package fr.shuvly.zm.map.region;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * Gets the outer axis-aligned bounding box enclosing this entire region.
     */
    BoundingBox getBoundingBox();

    /**
     * Collects all non-air blocks within this region's volume.
     * Uses block center coordinates (x + 0.5, y + 0.5, z + 0.5) to test inclusion.
     */
    default List<Block> getBlocks(World world)
    {
        final List<Block> blocks = new ArrayList<>();
        final BoundingBox bounds = getBoundingBox();

        final int minX = (int) Math.floor(bounds.getMinX());
        final int minY = (int) Math.floor(bounds.getMinY());
        final int minZ = (int) Math.floor(bounds.getMinZ());
        final int maxX = (int) Math.floor(bounds.getMaxX());
        final int maxY = (int) Math.floor(bounds.getMaxY());
        final int maxZ = (int) Math.floor(bounds.getMaxZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (contains(x + 0.5, y + 0.5, z + 0.5)) {
                        final Block block = world.getBlockAt(x, y, z);

                        if (block.getType() != Material.AIR) {
                            blocks.add(block);
                        }
                    }
                }
            }
        }
        return blocks;
    }

}
