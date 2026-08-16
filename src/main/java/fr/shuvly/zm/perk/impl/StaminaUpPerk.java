package fr.shuvly.zm.perk.impl;

import fr.shuvly.zm.perk.ZmPerk;
import fr.shuvly.zm.perk.ZmPerkType;
import fr.shuvly.zm.player.ZmPlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

public class StaminaUpPerk
    extends ZmPerk
{

    public StaminaUpPerk()
    {
        super(ZmPerkType.STAMINA_UP, "staminup!!!!!,1");
    }


    @Override
    public void apply(ZmPlayer zmPlayer)
    {
        final Player player = zmPlayer.getPlayer();
        final AttributeInstance walkSpeedAttr = player.getAttribute(Attribute.MOVEMENT_SPEED);

        if (walkSpeedAttr != null) {
            walkSpeedAttr.setBaseValue(.14);
        }
    }

    @Override
    public void remove(ZmPlayer zmPlayer)
    {
        final Player player = zmPlayer.getPlayer();
        final AttributeInstance speedAttr = player.getAttribute(Attribute.MOVEMENT_SPEED);

        if (speedAttr != null) {
            speedAttr.setBaseValue(.1);
        }
    }

}
