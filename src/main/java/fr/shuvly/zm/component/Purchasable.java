package fr.shuvly.zm.component;

import fr.shuvly.zm.component.interaction.InteractionType;
import fr.shuvly.zm.player.ZmPlayer;

public interface Purchasable
    extends Interactable
{

    int getCost(ZmPlayer player);
    void onPurchase(ZmPlayer player);
    boolean canBePurchased(ZmPlayer player);

    @Override
    default boolean onInteract(
        ZmPlayer zmPlayer,
        InteractionType interactionType
    )
    {
        if (canBePurchased(zmPlayer)) {
            if (zmPlayer.removePoints(getCost(zmPlayer))) {
                onPurchase(zmPlayer);
                return true;
            }
        }
        // todo: play sound or something?
        return false;
    }

}
