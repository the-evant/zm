package fr.shuvly.zm.component;

import fr.shuvly.zm.player.ZmPlayer;

public interface Purchasable
    extends Interactable
{

    int getCost();
    void onPurchase(ZmPlayer player);

    @Override
    default boolean onInteract(
        ZmPlayer player,
        InteractionType interactionType
    )
    {
        if (true) { // todo: check player balance
            onPurchase(player);
            return true;
        } else {
            // todo: play sound or something?
            return false;
        }
    }

}
