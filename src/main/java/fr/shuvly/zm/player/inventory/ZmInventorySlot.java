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

}
