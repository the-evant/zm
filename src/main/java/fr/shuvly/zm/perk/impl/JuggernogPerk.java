package fr.shuvly.zm.perk.impl;

import fr.shuvly.zm.perk.ZmPerk;
import fr.shuvly.zm.perk.ZmPerkType;
import fr.shuvly.zm.player.ZmPlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

public class JuggernogPerk
    extends ZmPerk
{

    public JuggernogPerk()
    {
        super(ZmPerkType.JUGGERNOG, "jugernnoogggg;......");
    }


    @Override
    public void apply(ZmPlayer zmPlayer)
    {
        final Player player = zmPlayer.getPlayer();
        final AttributeInstance healthAttr = player.getAttribute(Attribute.MAX_HEALTH);

        if (healthAttr != null) {
            healthAttr.setBaseValue(40.0);
            player.setHealth(40.0);
        }
    }

    @Override
    public void remove(ZmPlayer zmPlayer)
    {
        final Player player = zmPlayer.getPlayer();
        final AttributeInstance healthAttr = player.getAttribute(Attribute.MAX_HEALTH);

        if (healthAttr != null) {
            healthAttr.setBaseValue(20.0);
        }
    }

}
