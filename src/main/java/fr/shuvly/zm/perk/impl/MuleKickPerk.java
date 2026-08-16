package fr.shuvly.zm.perk.impl;

import fr.shuvly.zm.perk.ZmPerk;
import fr.shuvly.zm.perk.ZmPerkType;
import fr.shuvly.zm.player.ZmPlayer;
import fr.shuvly.zm.player.inventory.ZmInventorySlot;

public class MuleKickPerk
    extends ZmPerk
{

    public MuleKickPerk()
    {
        super(ZmPerkType.MULE_KICK, "mule kik....");
    }


    @Override
    public void apply(ZmPlayer player)
    {}

    @Override
    public void remove(ZmPlayer player)
    {
        player.getInventory().removeWeapon(ZmInventorySlot.TERTIARY_WEAPON);
    }

}
