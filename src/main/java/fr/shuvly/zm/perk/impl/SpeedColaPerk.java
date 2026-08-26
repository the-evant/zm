package fr.shuvly.zm.perk.impl;

import fr.shuvly.zm.perk.ZmPerk;
import fr.shuvly.zm.perk.ZmPerkItem;
import fr.shuvly.zm.perk.ZmPerkType;
import fr.shuvly.zm.player.ZmPlayer;
import org.bukkit.Color;

public class SpeedColaPerk
    extends ZmPerk
{

    public SpeedColaPerk()
    {
        super(
            ZmPerkType.SPEED_COLA,
            "ishowspeed coca cola",
            ZmPerkItem.potion(Color.GREEN)
        );
    }


    @Override
    public void apply(ZmPlayer player) {}

    @Override
    public void remove(ZmPlayer player) {}

}
