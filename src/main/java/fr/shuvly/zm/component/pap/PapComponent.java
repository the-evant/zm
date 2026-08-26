package fr.shuvly.zm.component.pap;

import fr.shuvly.zm.component.BaseComponent;
import fr.shuvly.zm.component.Purchasable;
import fr.shuvly.zm.component.interaction.InteractionTrigger;
import fr.shuvly.zm.player.ZmPlayer;
import fr.shuvly.zm.player.inventory.ZmInventory;
import fr.shuvly.zm.player.inventory.ZmInventorySlot;
import fr.shuvly.zm.weapon.PapUpgradeConfig;
import fr.shuvly.zm.weapon.ZmWeapon;
import fr.shuvly.zm.weapon.ZmWeaponRegistry;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Optional;
import java.util.UUID;

import static fr.shuvly.core.common.constant.TextParser.parse;

public class PapComponent
    extends BaseComponent
    implements Purchasable
{

    private final int cost;
    private final int repapCost;
    private final int upgradeTime;
    private final int pickupTimeout;
    private final ZmWeaponRegistry weaponRegistry;

    private final PapAnimator animator;

    private PapState state = PapState.IDLE;
    private UUID currentOwner;
    private ZmInventorySlot originalSlot;
    private ZmWeapon upgradedWeaponResult;


    public PapComponent(
        String id,
        InteractionTrigger trigger,
        int cost,
        int repapCost,
        int upgradeTime,
        int pickupTimeout,
        Location weaponCompartment,
        Vector direction,
        ZmWeaponRegistry weaponRegistry
    ) {
        super(id, trigger);
        this.cost = cost;
        this.repapCost = repapCost;
        this.upgradeTime = upgradeTime;
        this.pickupTimeout = pickupTimeout;
        this.weaponRegistry = weaponRegistry;

        this.animator = new PapAnimator(weaponCompartment, direction);
    }


    private int resolveCost(PapUpgradeConfig config, boolean isRepap)
    {
        if (isRepap && config.cost() != -1) {
            return config.cost();
        }

        return config.cost() != -1 ? config.cost() : this.cost;
    }

    private int resolveUpgradeTime(PapUpgradeConfig config)
    {
        return config.upgradeTimeTicks() != -1 ? config.upgradeTimeTicks() : this.upgradeTime;
    }

    private int resolveTimeout(PapUpgradeConfig config)
    {
        return config.pickupTimeoutTicks() != -1 ? config.pickupTimeoutTicks() : this.pickupTimeout;
    }


    @Override
    public boolean canBePurchased(ZmPlayer zmPlayer)
    {
        if (this.state == PapState.READY_FOR_PICKUP) {
            return zmPlayer.getPlayer().getUniqueId().equals(this.currentOwner);
        }

        return true;
    }

    @Override
    public int getCost(ZmPlayer zmPlayer)
    {
        if (this.state == PapState.READY_FOR_PICKUP) {
            return 0;
        }

        return getHeldPackableWeapon(zmPlayer).map(weapon -> {
            final PapUpgradeConfig config = weapon.getPapConfig().orElseThrow();
            // todo: in the future, check if the weapon is ALREADY packed to pass 'true' to isRepap
            boolean isRepap = false;
            return resolveCost(config, isRepap);
        }).orElse(0);
    }

    @Override
    public void onPurchase(ZmPlayer zmPlayer)
    {
        if (this.state == PapState.READY_FOR_PICKUP) {
            retrieveWeapon(zmPlayer);
            return;
        }

        if (this.state != PapState.IDLE) {
            return;
        }

        final Optional<ZmWeapon> heldWeaponOpt = getHeldPackableWeapon(zmPlayer);
        if (heldWeaponOpt.isEmpty()) {
            return;
        }

        final ZmWeapon currentWeapon = heldWeaponOpt.get();
        final PapUpgradeConfig config = currentWeapon.getPapConfig().orElseThrow();

        final ZmWeapon prototype = this.weaponRegistry.getWeapon(config.weaponId());

        if (prototype == null) { // should NEVER happen
            zmPlayer.getPlayer().sendMessage(parse("<red>CONFIG ERROR!!!!!!!!!,!!!!</red>"));
            return;
        }

        zmPlayer.removePoints(getCost(zmPlayer));

        this.upgradedWeaponResult = prototype.duplicate();
        this.upgradedWeaponResult.refillAmmo();

        final ZmInventory inventory = zmPlayer.getInventory();

        this.originalSlot = inventory.getSlotHolding(currentWeapon.getInstanceUuid()).orElse(null);
        if (this.originalSlot != null) {
            inventory.removeWeapon(this.originalSlot);
        }

        this.currentOwner = zmPlayer.getPlayer().getUniqueId();
        this.state = PapState.PROCESSING;

        int processTicks = resolveUpgradeTime(config) / 50;
        int timeoutTicks = resolveTimeout(config) / 50;

        this.animator.startProcessing(
            currentWeapon.getItemStack(),
            this.upgradedWeaponResult.getItemStack(),
            processTicks,
            () -> {
                this.state = PapState.READY_FOR_PICKUP;
                this.animator.startTimeoutSequence(timeoutTicks, this::resetMachine);
            }
        );
    }

    private void retrieveWeapon(ZmPlayer zmPlayer)
    {
        if (!zmPlayer.getPlayer().getUniqueId().equals(this.currentOwner)) {
            return;
        }

        if (this.upgradedWeaponResult != null) {
            zmPlayer.getInventory().giveWeapon(this.upgradedWeaponResult);
            zmPlayer.getPlayer().playSound(zmPlayer.getPlayer().getLocation(), Sound.ITEM_ARMOR_EQUIP_NETHERITE, 1.0f, 1.0f);
        }

        resetMachine();
    }

    private void resetMachine()
    {
        this.animator.cleanup();
        this.state = PapState.IDLE;
        this.currentOwner = null;
        this.originalSlot = null;
        this.upgradedWeaponResult = null;
    }

    @Override
    public String getPromptText(ZmPlayer player)
    {
        return switch (this.state) {
            case IDLE -> getHeldPackableWeapon(player)
                .map(w -> String.format(
                    "Press [<key:key.swapOffhand>] to Pack-A-Punch %s [Cost: %d]",
                    w.getItemTemplate().displayName(),
                    getCost(player)
                )).orElse("pd");

            case PROCESSING -> "<yellow>Packing weapon...</yellow>";

            case READY_FOR_PICKUP -> player.getPlayer().getUniqueId().equals(this.currentOwner)
                ? String.format("Press [<key:key.swapOffhand>] to retrieve %s", this.upgradedWeaponResult.getItemTemplate().displayName())
                : "<red>Weapon is being upgraded...</red>";
        };
    }

    private Optional<ZmWeapon> getHeldPackableWeapon(ZmPlayer zmPlayer)
    {
        final ItemStack held = zmPlayer.getPlayer().getInventory().getItemInMainHand();
        final UUID weaponUuid = ZmWeapon.getWeaponUuid(held);

        if (weaponUuid == null) {
            return Optional.empty();
        }

        final ZmWeapon weapon = zmPlayer.getInventory().getWeaponByUuid(weaponUuid);

        if (weapon == null || !weapon.isPackAPunchable()) {
            return Optional.empty();
        }

        return Optional.of(weapon);
    }

}
