package fr.shuvly.zm.component.door;

import fr.shuvly.zm.component.BaseComponent;
import fr.shuvly.zm.component.Purchasable;
import fr.shuvly.zm.component.interaction.InteractionTrigger;
import fr.shuvly.zm.component.interaction.ZoneTrigger;
import fr.shuvly.zm.map.region.Region;
import fr.shuvly.zm.map.Zone;
import fr.shuvly.zm.player.ZmPlayer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DoorComponent
    extends BaseComponent
    implements Purchasable
{

    private final DoorType type;
    private final int cost;
    private final Set<Zone> targetZones;

    private boolean isOpened = false;


    public DoorComponent(String id, InteractionTrigger trigger, DoorType type, int cost)
    {
        super(id, trigger);
        this.type = type;
        this.cost = cost;
        this.targetZones = new HashSet<>();
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

        // TODO: Play door open animation (remove blocks) and sound
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
