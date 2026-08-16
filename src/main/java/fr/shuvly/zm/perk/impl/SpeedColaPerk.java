package fr.shuvly.zm.perk.impl;

import fr.shuvly.zm.perk.ZmPerk;
import fr.shuvly.zm.perk.ZmPerkType;
import fr.shuvly.zm.player.ZmPlayer;

public class SpeedColaPerk
    extends ZmPerk
{

    public SpeedColaPerk()
    {
        super(ZmPerkType.SPEED_COLA, "ishowspeed coca cola");
    }


    @Override
    public void apply(ZmPlayer player) {}

    @Override
    public void remove(ZmPlayer player) {}

}
