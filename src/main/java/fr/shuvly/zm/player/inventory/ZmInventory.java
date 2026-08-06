package fr.shuvly.zm.player.inventory;

import fr.shuvly.zm.player.ZmPlayer;
import fr.shuvly.zm.weapon.ZmWeapon;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

public class ZmInventory
{

    final ZmPlayer owner;

    private final Map<ZmInventorySlot, ZmWeapon> slots = new EnumMap<>(ZmInventorySlot.class);


    public ZmInventory(ZmPlayer owner)
    {
        this.owner = owner;
    }


    public void giveWeapon(ZmWeapon prototype)
    {
        final ZmWeapon weapon = prototype.duplicate();

        switch (weapon.getCategory()) {
            case PRIMARY -> {
                if (!slots.containsKey(ZmInventorySlot.PRIMARY_WEAPON)) {
                    giveWeaponAndSyncBukkitInventory(ZmInventorySlot.PRIMARY_WEAPON, weapon);
                } else if (!slots.containsKey(ZmInventorySlot.SECONDARY_WEAPON)) {
                    giveWeaponAndSyncBukkitInventory(ZmInventorySlot.SECONDARY_WEAPON, weapon);
                } else {
                    // todo: replace used slot by the weapon lol
                }
            }
            case LETHAL -> giveWeaponAndSyncBukkitInventory(ZmInventorySlot.LETHAL, weapon);
            case TACTICAL -> giveWeaponAndSyncBukkitInventory(ZmInventorySlot.TACTICAL, weapon);
            case MELEE -> giveWeaponAndSyncBukkitInventory(ZmInventorySlot.MELEE, weapon);
        }
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

    public void syncBukkitInventoryWeapon(ZmWeapon weapon)
    {
        for (Map.Entry<ZmInventorySlot, ZmWeapon> weaponEntry : slots.entrySet()) {
            if (weaponEntry.getValue().getInstanceUuid() == weapon.getInstanceUuid()) {
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
        owner.getPlayer().getInventory().setItem(slot.getHotbarIndex(), slots.get(slot).getItemStack());
    }

}
