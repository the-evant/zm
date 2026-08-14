package fr.shuvly.zm.player.inventory;

public enum ZmInventorySlot
{

    PRIMARY_WEAPON(0),
    SECONDARY_WEAPON(1),
    TERTIARY_WEAPON(2),
    LETHAL(3),
    TACTICAL(4),
    MELEE(5);


    private final int hotbarIndex;


    ZmInventorySlot(int hotbarIndex)
    {
        this.hotbarIndex = hotbarIndex;
    }


    public int getHotbarIndex() { return hotbarIndex; }

    public boolean isPrimary()
    {
        return this == PRIMARY_WEAPON || this == SECONDARY_WEAPON || this == TERTIARY_WEAPON;
    }

    /**
     * Maps a Bukkit hotbar index back to a ZmInventorySlot.
     */
    public static ZmInventorySlot fromHotbarIndex(int index)
    {
        for (ZmInventorySlot slot : values()) {
            if (slot.getHotbarIndex() == index) {
                return slot;
            }
        }
        return null;
    }

}
