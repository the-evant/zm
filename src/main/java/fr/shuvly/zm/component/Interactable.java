package fr.shuvly.zm.component;

import fr.shuvly.zm.component.interaction.InteractionType;
import fr.shuvly.zm.player.ZmPlayer;

public interface Interactable
{

    boolean onInteract(ZmPlayer player, InteractionType interactionType);
    String getPromptText(ZmPlayer player);

}
