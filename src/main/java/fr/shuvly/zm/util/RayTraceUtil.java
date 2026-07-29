package fr.shuvly.zm.util;

import fr.shuvly.zm.component.BaseComponent;
import fr.shuvly.zm.component.ComponentRegistry;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class RayTraceUtil
{

    private static final double STEP_SIZE = 0.2;
    private static final double MAX_DISTANCE = 4.5;


    /**
     * Performs a raymarch from the player's eyes to find the first component they are looking at.
     *
     * @param   player      The player to raymarch from
     * @param   registry    The registry containing all components
     * @param   maxDistance The maximum distance to march
     * @return  The BaseComponent the player is looking at, or null if none
     */
    public static BaseComponent getTargetedComponent(
        Player player,
        ComponentRegistry registry,
        double maxDistance
    )
    {
        final Location eyeLocation = player.getEyeLocation();
        final Vector direction = eyeLocation.getDirection().normalize().multiply(STEP_SIZE);

        double currentDistance = 0.0;

        double x = eyeLocation.getX();
        double y = eyeLocation.getY();
        double z = eyeLocation.getZ();

        while (currentDistance <= maxDistance) {
            x += direction.getX();
            y += direction.getY();
            z += direction.getZ();
            currentDistance += STEP_SIZE;

            org.bukkit.Particle.DUST.builder()
                .color(Color.BLUE, 1.0f)
                .location(new Location(player.getWorld(), x, y, z))
                .count(1)
                .spawn();

            final BaseComponent hitComponent = registry.getComponentAt(x, y, z);

            if (hitComponent != null) {
                return hitComponent;
            }
        }

        return null;
    }

    public static BaseComponent getTargetedComponent(Player player, ComponentRegistry registry)
    {
        return getTargetedComponent(player, registry, MAX_DISTANCE);
    }

}
