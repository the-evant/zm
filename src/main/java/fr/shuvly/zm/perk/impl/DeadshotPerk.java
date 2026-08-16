package fr.shuvly.zm.perk.impl;

import fr.shuvly.zm.perk.ZmPerk;
import fr.shuvly.zm.perk.ZmPerkType;
import fr.shuvly.zm.player.ZmPlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

public class DeadshotPerk
    extends ZmPerk
{

    public DeadshotPerk()
    {
        super(ZmPerkType.DEADSHOT, "<black>deashot thekairi");
    }


    @Override
    public void apply(ZmPlayer zmPlayer)
    {}

    @Override
    public void remove(ZmPlayer zmPlayer)
    {}

}
