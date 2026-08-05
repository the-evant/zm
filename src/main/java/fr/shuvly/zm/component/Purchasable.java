package fr.shuvly.zm.component;

import fr.shuvly.zm.component.interaction.InteractionType;
import fr.shuvly.zm.player.ZmPlayer;

public interface Purchasable
    extends Interactable
{

    int getCost();
    void onPurchase(ZmPlayer player);
    boolean canBePurchased(ZmPlayer player);

    @Override
    default boolean onInteract(
        ZmPlayer player,
        InteractionType interactionType
    )
    {
        if (canBePurchased(player)) {
            if (player.removePoints(getCost())) {
                onPurchase(player);
                return true;
            }
        }
        // todo: play sound or something?
        return false;
    }

}
