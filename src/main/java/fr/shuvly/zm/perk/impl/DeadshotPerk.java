package fr.shuvly.zm.perk.impl;

import fr.shuvly.zm.perk.ZmPerk;
import fr.shuvly.zm.perk.ZmPerkItem;
import fr.shuvly.zm.perk.ZmPerkType;
import fr.shuvly.zm.player.ZmPlayer;
import org.bukkit.Color;

public class DeadshotPerk
    extends ZmPerk
{

    public DeadshotPerk()
    {
        super(
            ZmPerkType.DEADSHOT,
            "<black>deashot thekairi",
            ZmPerkItem.potion(Color.BLACK)
        );
    }


    @Override
    public void apply(ZmPlayer zmPlayer)
    {}

    @Override
    public void remove(ZmPlayer zmPlayer)
    {}

}
