package fr.shuvly.zm.component.interaction;

import fr.shuvly.zm.map.region.Region;
import fr.shuvly.zm.player.ZmPlayer;
import org.bukkit.Location;

public record ZoneTrigger(
    Region triggerRegion
)
    implements InteractionTrigger
{

    @Override
    public boolean shouldTrigger(ZmPlayer player)
    {
        final Location loc = player.getPlayer().getLocation();

        return triggerRegion.contains(loc.x(), loc.y(), loc.z());
    }

}
