package fr.shuvly.zm.component;

import fr.shuvly.zm.component.interaction.InteractionType;
import fr.shuvly.zm.player.ZmPlayer;

public interface Purchasable
    extends Interactable
{

    int getCost();
    void onPurchase(ZmPlayer player);

    @Override
    default boolean onInteract(ZmPlayer player, InteractionType interactionType)
    {
        return true;
//        if (player.hasPoints(getCost())) {
//            player.removePoints(getCost());
//            onPurchase(player);
//        } else {
//            // TODO: Play "not enough points" sound
//        }
    }

}
