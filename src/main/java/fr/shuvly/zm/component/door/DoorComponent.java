package fr.shuvly.zm.component.door;

import fr.shuvly.zm.component.BaseComponent;
import fr.shuvly.zm.component.ComponentRegistry;
import fr.shuvly.zm.component.Purchasable;
import fr.shuvly.zm.component.door.animation.DoorAnimation;
import fr.shuvly.zm.component.interaction.InteractionTrigger;
import fr.shuvly.zm.map.zone.Zone;
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
    private final Set<Zone> targetZones = new HashSet<>();
    private final Set<String> unlocksDoors = new HashSet<>();
    private final DoorAnimation animation;

    private final ComponentRegistry componentRegistry;

    private boolean isOpened = false;


    public DoorComponent(
        String id,
        InteractionTrigger trigger,
        DoorType type,
        int cost,
        DoorAnimation animation,
        ComponentRegistry componentRegistry
    )
    {
        super(id, trigger);
        this.type = type;
        this.cost = cost;
        this.animation = animation;
        this.componentRegistry = componentRegistry;
    }


    public void addTargetZone(Zone zone)
    {
        this.targetZones.add(zone);
    }
    public void addUnlockDoor(String doorId) { this.unlocksDoors.add(doorId); }


    @Override
    public int getCost(ZmPlayer player)
    {
        return cost;
    }

    @Override
    public void onPurchase(ZmPlayer player)
    {
        this.isOpened = true;

        for (Zone zone : targetZones) {
            zone.setUnlocked(true);
        }

        for (String linkedDoorId : unlocksDoors) {
            final BaseComponent component = componentRegistry.getComponent(linkedDoorId);

            if (component instanceof DoorComponent linkedDoor && !linkedDoor.isOpened()) {
                linkedDoor.onPurchase(player);
            }
        }

        if (animation != null) {
            player.getPlayer().sendMessage(parse("Animation start lol"));
            animation.animateOpen(player.getPlayer().getWorld(), () -> {
                player.getPlayer().sendMessage(parse("Animation end lol"));
            });
        }
    }

    @Override
    public boolean canBePurchased(ZmPlayer player)
    {
        return !isOpened;
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
