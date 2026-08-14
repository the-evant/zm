package fr.shuvly.zm.component.wallbuy;

import fr.shuvly.zm.component.BaseComponent;
import fr.shuvly.zm.component.Purchasable;
import fr.shuvly.zm.component.interaction.InteractionTrigger;
import fr.shuvly.zm.player.ZmPlayer;
import fr.shuvly.zm.weapon.ZmWeapon;
import fr.shuvly.zm.weapon.ZmWeaponRegistry;

public class WallbuyComponent
    extends BaseComponent
    implements Purchasable
{

    private final String weaponId;
    private final String papWeaponId;
    private final int weaponCost;
    private final int ammoCost;
    private final int papAmmoCost;
    private final ZmWeaponRegistry weaponRegistry;


    public WallbuyComponent(
        String id,
        InteractionTrigger trigger,
        String weaponId,
        String papWeaponId,
        int weaponCost,
        int ammoCost,
        int papAmmoCost,
        ZmWeaponRegistry weaponRegistry
    ) {
        super(id, trigger);
        this.weaponId = weaponId;
        this.papWeaponId = papWeaponId;
        this.weaponCost = weaponCost;
        this.ammoCost = ammoCost;
        this.papAmmoCost = papAmmoCost;
        this.weaponRegistry = weaponRegistry;
    }


    private WallbuyState getState(ZmPlayer player)
    {
        if (player.getInventory().getWeaponById(papWeaponId) != null) {
            return WallbuyState.PAP_AMMO;
        }

        if (player.getInventory().getWeaponById(weaponId) != null) {
            return WallbuyState.AMMO;
        }

        return WallbuyState.WEAPON;
    }

    @Override
    public int getCost(ZmPlayer player)
    {
        return switch (getState(player)) {
            case WEAPON -> weaponCost;
            case AMMO -> ammoCost;
            case PAP_AMMO -> papAmmoCost;
        };
    }

    @Override
    public void onPurchase(ZmPlayer player)
    {
        final WallbuyState state = getState(player);

        switch (state) {
            case WEAPON -> {
                final ZmWeapon prototype = weaponRegistry.getWeapon(weaponId);
                if (prototype != null) {
                    final ZmWeapon clone = prototype.duplicate();
                    player.getInventory().giveWeapon(clone);
                }
            }
            case AMMO -> {
                final ZmWeapon weapon = player.getInventory().getWeaponById(weaponId);
                if (weapon != null) {
                    weapon.refillAmmo();
                    player.getInventory().syncBukkitInventoryWeapon(weapon);
                }
            }
            case PAP_AMMO -> {
                final ZmWeapon upgradedWeapon = player.getInventory().getWeaponById(papWeaponId);
                if (upgradedWeapon != null) {
                    upgradedWeapon.refillAmmo();
                    player.getInventory().syncBukkitInventoryWeapon(upgradedWeapon);
                }
            }
        }
    }

    @Override
    public boolean canBePurchased(ZmPlayer player)
    {
        return true;
    }

    @Override
    public String getPromptText(ZmPlayer player)
    {
        final WallbuyState state = getState(player);
        final ZmWeapon prototype = weaponRegistry.getWeapon(weaponId);
        final String displayName = prototype != null ? prototype.getItemTemplate().displayName() : "Weapon";
        final String textFormat = "Press [<key:key.swapOffhand>] to buy %s [Cost: " + getCost(player) + "]";

        return switch (state) {
            case WEAPON -> textFormat.formatted(displayName);
            case AMMO -> textFormat.formatted(displayName + " ammo");
            case PAP_AMMO -> textFormat.formatted(displayName + " Pack-A-Punch'ed ammo");
        };
    }

}
