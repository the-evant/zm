package fr.shuvly.zm.perk.impl;

import fr.shuvly.zm.perk.ZmPerk;
import fr.shuvly.zm.perk.ZmPerkType;
import fr.shuvly.zm.player.ZmPlayer;

public class DoubleTapPerk
    extends ZmPerk
{

    public DoubleTapPerk()
    {
        super(ZmPerkType.DOUBLE_TAP, "double tap!!!!");
    }


    @Override
    public void apply(ZmPlayer player) {}

    @Override
    public void remove(ZmPlayer player) {}

}
