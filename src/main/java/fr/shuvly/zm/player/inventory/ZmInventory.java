package fr.shuvly.zm.player.inventory;

import fr.shuvly.zm.perk.ZmPerkType;
import fr.shuvly.zm.player.ZmPlayer;
import fr.shuvly.zm.weapon.ZmWeapon;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ZmInventory
{

    private final ZmPlayer owner;
    private final Map<ZmInventorySlot, ZmWeapon> slots = new EnumMap<>(ZmInventorySlot.class);


    public ZmInventory(ZmPlayer owner)
    {
        this.owner = owner;
    }


    public void giveWeapon(ZmWeapon prototype)
    {
        final ZmWeapon weapon = prototype.duplicate();

        switch (weapon.getCategory()) {
            case PRIMARY -> handlePrimaryWeaponGive(weapon);
            case LETHAL -> giveWeaponAndSyncBukkitInventory(ZmInventorySlot.LETHAL, weapon);
            case TACTICAL -> giveWeaponAndSyncBukkitInventory(ZmInventorySlot.TACTICAL, weapon);
            case MELEE -> giveWeaponAndSyncBukkitInventory(ZmInventorySlot.MELEE, weapon);
        }
    }

    private void handlePrimaryWeaponGive(ZmWeapon weapon)
    {
        final int maxSlots = getMaxPrimarySlots();
        final ZmInventorySlot[] primarySlots = {
            ZmInventorySlot.PRIMARY_WEAPON,
            ZmInventorySlot.SECONDARY_WEAPON,
            ZmInventorySlot.TERTIARY_WEAPON
        };

        for (int i = 0; i < maxSlots; i++) {
            ZmInventorySlot slot = primarySlots[i];
            if (!slots.containsKey(slot)) {
                giveWeaponAndSyncBukkitInventory(slot, weapon);
                forceHoldSlot(slot);
                return;
            }
        }

        final ZmInventorySlot targetSlot = getActivePrimarySlot();

        giveWeaponAndSyncBukkitInventory(targetSlot, weapon);
        forceHoldSlot(targetSlot);
    }

    /**
     * Returns how many primary weapons the player is allowed to have.
     * Default: 2, 3 if player have Mule Kick perk.
     */
    private int getMaxPrimarySlots()
    {
        if (owner.hasPerk(ZmPerkType.MULE_KICK)) {
            return 3;
        }
        return 2;
    }

    /**
     * Identifies which primary slot the player is currently holding.
     * If they are holding a grenade/knife while buying a gun, it safely defaults to PRIMARY_WEAPON.
     */
    private ZmInventorySlot getActivePrimarySlot()
    {
        final int heldIndex = owner.getPlayer().getInventory().getHeldItemSlot();
        final ZmInventorySlot activeSlot = ZmInventorySlot.fromHotbarIndex(heldIndex);

        if (activeSlot != null && activeSlot.isPrimary()) {
            return activeSlot; // todo: change lol
        }

        return ZmInventorySlot.PRIMARY_WEAPON;
    }

    /**
     * Forces the Bukkit player to visually hold the specified slot.
     */
    private void forceHoldSlot(ZmInventorySlot slot)
    {
        owner.getPlayer().getInventory().setHeldItemSlot(slot.getHotbarIndex());
    }

    public ZmWeapon getWeaponById(String weaponId)
    {
        for (ZmWeapon weapon : slots.values()) {
            if (weapon.getId().equals(weaponId)) {
                return weapon;
            }
        }
        return null;
    }

    public ZmWeapon getWeaponByUuid(UUID uuid)
    {
        for (ZmWeapon weapon : slots.values()) {
            if (weapon.getInstanceUuid().equals(uuid)) {
                return weapon;
            }
        }
        return null;
    }

    public Optional<ZmInventorySlot> getSlotHolding(UUID weaponUuid)
    {
        return this.slots.entrySet().stream()
            .filter(entry -> entry.getValue().getInstanceUuid().equals(weaponUuid))
            .map(Map.Entry::getKey)
            .findFirst();
    }

    public void removeWeapon(ZmInventorySlot slot)
    {
        if (this.slots.remove(slot) != null) {
            this.syncBukkitInventorySlot(slot);
        }
    }

    public void syncBukkitInventoryWeapon(ZmWeapon weapon)
    {
        for (Map.Entry<ZmInventorySlot, ZmWeapon> weaponEntry : slots.entrySet()) {
            if (weaponEntry.getValue().getInstanceUuid().equals(weapon.getInstanceUuid())) {
                syncBukkitInventorySlot(weaponEntry.getKey());
            }
        }
    }

    private void giveWeaponAndSyncBukkitInventory(ZmInventorySlot slot, ZmWeapon weapon)
    {
        slots.put(slot, weapon);
        syncBukkitInventorySlot(slot);
    }

    private void syncBukkitInventorySlot(ZmInventorySlot slot)
    {
        final ZmWeapon weapon = slots.get(slot);

        owner.getPlayer().getInventory().setItem(
            slot.getHotbarIndex(),
            weapon != null ? weapon.getItemStack(owner) : null
        );
    }

}
