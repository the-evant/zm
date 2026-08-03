package fr.shuvly.zm.component.door;

import fr.shuvly.zm.component.BaseComponent;
import fr.shuvly.zm.component.Purchasable;
import fr.shuvly.zm.component.door.animation.DoorAnimation;
import fr.shuvly.zm.component.interaction.InteractionTrigger;
import fr.shuvly.zm.component.interaction.ZoneTrigger;
import fr.shuvly.zm.map.region.Region;
import fr.shuvly.zm.map.Zone;
import fr.shuvly.zm.player.ZmPlayer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static fr.shuvly.core.common.constant.TextParser.parse;

public class DoorComponent
    extends BaseComponent
    implements Purchasable
{

    private final DoorType type;
    private final int cost;
    private final Set<Zone> targetZones;
    private final DoorAnimation animation;

    private boolean isOpened = false;


    public DoorComponent(
        String id,
        InteractionTrigger trigger,
        DoorType type,
        int cost,
        DoorAnimation animation
    )
    {
        super(id, trigger);
        this.type = type;
        this.cost = cost;
        this.targetZones = new HashSet<>();
        this.animation = animation;
    }


    public void addTargetZone(Zone zone)
    {
        this.targetZones.add(zone);
    }
    public void addTargetZones(Set<Zone> zones)
    {
        this.targetZones.addAll(zones);
    }

    @Override
    public int getCost()
    {
        return cost;
    }

    @Override
    public void onPurchase(ZmPlayer player)
    {
        if (isOpened) {
            return;
        }

        this.isOpened = true;

        for (Zone zone : targetZones) {
            zone.setUnlocked(true);
        }

        if (animation != null) {
            player.getPlayer().sendMessage(parse("Animation start lol"));
            animation.animateOpen(player.getPlayer().getWorld(), () -> {
                player.getPlayer().sendMessage(parse("Animation end lol"));
                // Optional: Code to run after the animation finishes
                // e.g., play a "door locked" clunk sound if needed
            });
        }
    }

    @Override
    public String getPromptText(ZmPlayer player)
    {
        if (isOpened) {
            return null;
        }

        final String action = (type == DoorType.DOOR) ? "open door" : "clear debris";
        return "Press [<key:key.swapOffhand>] to " + action + " [Cost: " + cost + "]";
    }


    public boolean isOpened() { return isOpened; }
    public DoorType getType() { return type; }
    public Set<Zone> getTargetZones() { return Collections.unmodifiableSet(targetZones); }

}
