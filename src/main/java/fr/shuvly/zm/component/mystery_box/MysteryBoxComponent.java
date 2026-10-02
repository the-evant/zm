package fr.shuvly.zm.component.mystery_box;

import fr.shuvly.zm.Zm;
import fr.shuvly.zm.component.BaseComponent;
import fr.shuvly.zm.component.Purchasable;
import fr.shuvly.zm.component.interaction.InteractionTrigger;
import fr.shuvly.zm.player.ZmPlayer;
import fr.shuvly.zm.util.WeightedRandom;
import fr.shuvly.zm.weapon.ZmWeapon;
import fr.shuvly.zm.weapon.ZmWeaponRegistry;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.UUID;

public class MysteryBoxComponent
    extends BaseComponent
    implements Purchasable
{

    private static final Zm MAIN = Zm.getInstance();

    private static final long COOLDOWN_TICKS = 60L;

    private final int cost;
    private final ZmWeaponRegistry weaponRegistry;

    private final MysteryBoxAnimator animator;

    private final List<String> blacklistedWeapons;

    private boolean isActive = false;
    private final MysteryBoxUsesRange usesRange;
    private int currentUses = 0;
    private int maxUsesBeforeMove;
    private MysteryBoxState state;
    private UUID currentOwner;
    private ZmWeapon currentWeapon;
    private Runnable onRelocateCallback;


    public MysteryBoxComponent(
        String id,
        InteractionTrigger trigger,
        int cost,
        Location weaponLocation,
        Vector direction,
        List<String> blacklistedWeapons,
        ZmWeaponRegistry weaponRegistry,
        MysteryBoxUsesRange usesRange
    )
    {
        super(id, trigger);

        this.cost = cost;
        this.weaponRegistry = weaponRegistry;
        this.blacklistedWeapons = blacklistedWeapons;
        this.state = MysteryBoxState.IDLE;
        this.usesRange = usesRange;

        this.animator = new MysteryBoxAnimator(
            weaponLocation,
            direction,
            super.getInteractionTrigger().getRegion().getBlocks(weaponLocation.getWorld())
        );
    }

    @Override
    public int getCost(ZmPlayer player)
    {
        if (!this.isActive) {
            return 0;
        }

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

        final List<ZmWeapon> boxWeapons = weaponRegistry.getRegisteredWeapons().values().stream()
            .filter(ZmWeapon::isInMysteryBox)
            .filter((w) -> !this.blacklistedWeapons.contains(w.getId()))
            .filter((w) -> zmPlayer.getInventory().getWeaponById(w.getId()) == null)
            .toList();

        if (boxWeapons.isEmpty()) {
            zmPlayer.getPlayer().sendMessage("<red>No weapons in the Mystery Box!</red>");
            // todo: play sound lol
            return;
        }

        zmPlayer.removePoints(getCost(zmPlayer));

        this.currentOwner = zmPlayer.getPlayer().getUniqueId();
        this.state = MysteryBoxState.ROLLING;

        this.currentUses++;

        zmPlayer.getPlayer().sendMessage("currentUses: " + this.currentUses);
        zmPlayer.getPlayer().sendMessage("maxuses: " + this.maxUsesBeforeMove);
        zmPlayer.getPlayer().sendMessage("isactive: " + this.isActive);

        if (this.currentUses > this.maxUsesBeforeMove && onRelocateCallback != null) {
            this.state = MysteryBoxState.TEDDY_BEAR;

            this.animator.playTeddyBearAnimation(
                zmPlayer,
                boxWeapons,
                () -> {
                    this.setActive(false);
                    this.reset();
                    onRelocateCallback.run();
                }
            );
            return;
        }

        this.currentWeapon = WeightedRandom.roll(boxWeapons).duplicate();

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
        if (!this.isActive) {
            return false;
        }

        if (this.state == MysteryBoxState.READY_FOR_PICKUP) {
            return isCurrentOwner(zmPlayer);
        }

        return state == MysteryBoxState.IDLE;
    }

    @Override
    public String getPromptText(ZmPlayer zmPlayer)
    {
        if (!this.isActive) {
            return "not active lol";
        }

        if (this.state == MysteryBoxState.IDLE) {
            return "Press [<key:key.swapOffhand>] to buy Mystery Box (Cost: " + this.cost + ")";
        }

        if (!isCurrentOwner(zmPlayer)) {
            return "!iscurrentowner";
        }

        if (state == MysteryBoxState.ROLLING) {
            return "<yellow>Rolling...</yellow>";
        }

        if (state == MysteryBoxState.TEDDY_BEAR) {
            return "teddy bear lol";
        }

        return zmPlayer.getPlayer().getUniqueId().equals(this.currentOwner)
            ? String.format(
                "Press [<key:key.swapOffhand>] to retrieve %s",
                this.currentWeapon.getItemTemplate().displayName()
            )
            : "<red>Weapon is being upgraded...</red>";
    }

    private void reset()
    {
        this.animator.cleanup();
        this.currentOwner = null;
        this.currentWeapon = null;

        this.state = MysteryBoxState.COOLDOWN;

        MAIN.getServer().getRegionScheduler().runDelayed(MAIN, animator.getWeaponLocation(), task -> {
            if (this.state == MysteryBoxState.COOLDOWN) {
                this.state = MysteryBoxState.IDLE;
            }
        }, COOLDOWN_TICKS);
    }

    private boolean isCurrentOwner(ZmPlayer zmPlayer)
    {
        return this.currentOwner != null && this.currentOwner.equals(zmPlayer.getPlayer().getUniqueId());
    }

    public void setRelocateCallback(Runnable callback) { this.onRelocateCallback = callback; }

    public void setActive(boolean active)
    {
        this.isActive = active;

        if (active) {
            this.maxUsesBeforeMove = usesRange.roll();
            this.currentUses = 0;
        }
    }

    public boolean isActive() { return isActive; }

}
