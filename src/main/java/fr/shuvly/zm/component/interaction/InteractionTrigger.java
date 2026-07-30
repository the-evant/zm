package fr.shuvly.zm.component.interaction;

import fr.shuvly.zm.player.ZmPlayer;

public interface InteractionTrigger
{

    /**
     * @param   player  Player to check
     * @return  true if player meets the requirements to trigger the interaction trigger.
     */
    boolean shouldTrigger(ZmPlayer player);

}
