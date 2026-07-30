package fr.shuvly.zm.component.interaction;

import fr.shuvly.zm.map.region.Region;
import fr.shuvly.zm.player.ZmPlayer;
import org.bukkit.Location;
import org.bukkit.util.RayTraceResult;

public record LookTrigger(
    Region region,
    double maxDistance
)
    implements InteractionTrigger
{

    @Override
    public boolean shouldTrigger(ZmPlayer player)
    {
        final Location eyeLocation = player.getPlayer().getEyeLocation();

        final RayTraceResult result = this.region.rayTrace(
            eyeLocation.toVector(),
            eyeLocation.getDirection(),
            maxDistance
        );

        return result != null;
    }

}
