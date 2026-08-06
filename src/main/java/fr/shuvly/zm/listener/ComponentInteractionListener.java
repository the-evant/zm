package fr.shuvly.zm.listener;

import fr.shuvly.zm.Zm;
import fr.shuvly.zm.component.BaseComponent;
import fr.shuvly.zm.component.ComponentRegistry;
import fr.shuvly.zm.component.Interactable;
import fr.shuvly.zm.component.interaction.InteractionType;
import fr.shuvly.zm.game.Game;
import fr.shuvly.zm.player.ZmPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import static fr.shuvly.core.common.constant.TextParser.parse;

public class ComponentInteractionListener
    implements Listener
{

    private static final Zm MAIN = Zm.getInstance();


    @EventHandler
    public void onSwapHand(PlayerSwapHandItemsEvent event)
    {
        event.setCancelled(true);

        final Player player = event.getPlayer();
        final Game game = MAIN.getGameManager().getPlayerGame(player);

        if (game == null) {
            return;
        }

        final ZmPlayer zmPlayer = game.getZmPlayer(player);
        final ComponentRegistry componentRegistry = game.getMap().getComponentRegistry();

        for (BaseComponent component : componentRegistry.getAll()) {
            if (component.getInteractionTrigger().shouldTrigger(zmPlayer)) {
                player.sendMessage(parse("Triggered component: " + component.getId()));

                if (component instanceof Interactable c) {
                    c.onInteract(zmPlayer, InteractionType.SWAP_HAND);
                    return;
                }
            }
        }
    }

}
