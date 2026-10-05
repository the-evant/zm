package fr.shuvly.zm.perk.impl;

import fr.shuvly.zm.perk.ZmPerk;
import fr.shuvly.zm.perk.ZmPerkItem;
import fr.shuvly.zm.perk.ZmPerkType;
import fr.shuvly.zm.player.ZmPlayer;
import org.bukkit.Color;

/**
 * Placeholder: the faster revive time is handled by {@link fr.shuvly.zm.player.state.PlayerStateManager},
 * which checks if the reviver has this perk.
 */
public class QuickRevivePerk
    extends ZmPerk
{

    public QuickRevivePerk()
    {
        super(
            ZmPerkType.QUICK_REVIVE,
            "quik reviv....",
            ZmPerkItem.potion(Color.AQUA)
        );
    }


    @Override
    public void apply(ZmPlayer player)
    {}

    @Override
    public void remove(ZmPlayer player)
    {}

}
