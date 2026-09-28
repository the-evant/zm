package fr.shuvly.zm.component.mystery_box;

import fr.shuvly.zm.component.BaseComponent;
import fr.shuvly.zm.component.Purchasable;
import fr.shuvly.zm.component.interaction.InteractionTrigger;
import fr.shuvly.zm.player.ZmPlayer;
import fr.shuvly.zm.weapon.ZmWeapon;
import fr.shuvly.zm.weapon.ZmWeaponRegistry;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class MysteryBoxComponent
    extends BaseComponent
    implements Purchasable
{

    private final int cost;
    private final ZmWeaponRegistry weaponRegistry;

    private final MysteryBoxAnimator animator;

    private MysteryBoxState state;
    private UUID currentOwner;
    private ZmWeapon currentWeapon;


    public MysteryBoxComponent(
        String id,
        InteractionTrigger trigger,
        int cost,
        Location weaponLocation,
        Vector direction,
        ZmWeaponRegistry weaponRegistry
    )
    {
        super(id, trigger);
        this.cost = cost;
        this.weaponRegistry = weaponRegistry;
        this.state = MysteryBoxState.IDLE;
        this.animator = new MysteryBoxAnimator(weaponLocation, direction);
    }

    @Override
    public int getCost(ZmPlayer player)
    {
        if (this.state == MysteryBoxState.READY_FOR_PICKUP) {
            return 0;
        }

        return this.cost;
    }

    @Override
    public void onPurchase(ZmPlayer zmPlayer)
    {
        if (this.state == MysteryBoxState.READY_FOR_PICKUP) {
            retrieveWeapon(zmPlayer);
            return;
        }

        if (this.state != MysteryBoxState.IDLE) {
            return;
        }

        final List<ZmWeapon> boxWeapons = weaponRegistry.getRegisteredWeapons().values().stream()
            .filter(ZmWeapon::isInMysteryBox)
            .toList();

        if (boxWeapons.isEmpty()) {
            zmPlayer.getPlayer().sendMessage("<red>No weapons in the Mystery Box!</red>");
            // todo: play sound lol
            return;
        }

        zmPlayer.removePoints(getCost(zmPlayer));

        this.currentOwner = zmPlayer.getPlayer().getUniqueId();
        this.state = MysteryBoxState.ROLLING;

        this.currentWeapon = boxWeapons.get(ThreadLocalRandom.current().nextInt(boxWeapons.size())).duplicate();

        this.animator.startProcessing(
            zmPlayer,
            this.currentWeapon,
            boxWeapons,
            () -> this.state = MysteryBoxState.READY_FOR_PICKUP,
            this::reset
        );
    }

    private void retrieveWeapon(ZmPlayer zmPlayer)
    {
        if (!isCurrentOwner(zmPlayer)) {
            return;
        }

        if (this.currentWeapon != null) {
            zmPlayer.getInventory().giveWeapon(this.currentWeapon);
            zmPlayer.getPlayer().playSound(zmPlayer.getPlayer().getLocation(), Sound.ITEM_ARMOR_EQUIP_NETHERITE, 1.0f, 1.0f);
        }

        reset();
    }

    @Override
    public boolean canBePurchased(ZmPlayer zmPlayer)
    {
        if (this.state == MysteryBoxState.READY_FOR_PICKUP) {
            return isCurrentOwner(zmPlayer);
        }

        return state != MysteryBoxState.ROLLING;
    }

    @Override
    public String getPromptText(ZmPlayer zmPlayer)
    {
        if (this.state == MysteryBoxState.IDLE) {
            return "Press [<key:key.swapOffhand>] to buy Mystery Box (Cost: " + this.cost + ")";
        }

        if (!isCurrentOwner(zmPlayer)) {
            return null;
        }

        if (state == MysteryBoxState.ROLLING) {
            return "<yellow>Rolling...</yellow>";
        }

        return zmPlayer.getPlayer().getUniqueId().equals(this.currentOwner)
            ? String.format("Press [<key:key.swapOffhand>] to retrieve %s", "eu eu eu")
            : "<red>Weapon is being upgraded...</red>";
    }

    private void reset()
    {
        this.animator.cleanup();
        this.state = MysteryBoxState.IDLE;
        this.currentOwner = null;
        this.currentWeapon = null;
    }

    private boolean isCurrentOwner(ZmPlayer zmPlayer)
    {
        return this.currentOwner != null && this.currentOwner.equals(zmPlayer.getPlayer().getUniqueId());
    }

}
