package fr.shuvly.zm.task;

import fr.shuvly.zm.component.BaseComponent;
import fr.shuvly.zm.component.ComponentRegistry;
import fr.shuvly.zm.component.Interactable;
import fr.shuvly.zm.game.Game;
import fr.shuvly.zm.player.ZmPlayer;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import static fr.shuvly.core.common.constant.TextParser.parse;

public class ComponentPromptDisplayActionBarTask
    implements ZmTask
{

    private final Game game;


    public ComponentPromptDisplayActionBarTask(Game game)
    {
        this.game = game;
    }


    @Override
    public long getInitialDelay()
    {
        return 1L;
    }

    @Override
    public long getPeriod()
    {
        return 4L;
    }

    @Override
    public void accept(ScheduledTask scheduledTask)
    {
        final ComponentRegistry componentRegistry = game.getMap().getComponentRegistry();

        for (ZmPlayer zmPlayer : game.getAlivePlayers()) {
            final Player player = zmPlayer.getPlayer();

            if (!displayPrompt(componentRegistry, zmPlayer)) {
                player.sendActionBar(Component.empty());
            }
        }
    }

    /**
     * Checks if a player can interact with a component.
     * If found, displays its prompt message to them.
     *
     * @param   componentRegistry   Registry of components to check
     * @param   player              Player to check and display the prompt to
     * @return  true if a component has been found, false otherwise.
     */
    private boolean displayPrompt(ComponentRegistry componentRegistry, ZmPlayer player)
    {
        for (BaseComponent component : componentRegistry.getAll()) {
            if (!component.getInteractionTrigger().shouldTrigger(player)) {
                continue;
            }

            if (!(component instanceof Interactable interactable)) {
                continue;
            }

            final String text = interactable.getPromptText(player);

            if (text != null && !text.isEmpty()) {
                player.getPlayer().sendActionBar(parse(text));
                return true;
            }
        }

        return false;
    }

}
