package fr.shuvly.zm.perk;

import fr.shuvly.zm.player.ZmPlayer;

public abstract class ZmPerk
{

    private final ZmPerkType type;
    private final String displayName;
    private final ZmPerkItem item;


    protected ZmPerk(ZmPerkType type, String displayName, ZmPerkItem item)
    {
        this.type = type;
        this.displayName = displayName;
        this.item = item;
    }


    public abstract void apply(ZmPlayer player);
    public abstract void remove(ZmPlayer player);


    public ZmPerkType getType() { return type; }
    public String getDisplayName() { return displayName; }
    public ZmPerkItem getItem() { return item; }

}
