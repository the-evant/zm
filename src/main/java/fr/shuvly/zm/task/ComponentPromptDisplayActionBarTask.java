package fr.shuvly.zm.task;

import fr.shuvly.zm.component.BaseComponent;
import fr.shuvly.zm.component.ComponentRegistry;
import fr.shuvly.zm.component.Interactable;
import fr.shuvly.zm.game.Game;
import fr.shuvly.zm.player.ZmPlayer;
import fr.shuvly.zm.util.RayTraceUtil;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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
        return 10L;
    }

    @Override
    public void accept(ScheduledTask scheduledTask)
    {
        final ComponentRegistry componentRegistry = game.getMap().getComponentRegistry();

        for (ZmPlayer zmPlayer : game.getAlivePlayers()) {
            final Player player = zmPlayer.getPlayer();

            if (player == null) {
                continue;
            }

            final BaseComponent target = RayTraceUtil.getTargetedComponent(player, componentRegistry);

            if (!(target instanceof Interactable interactable)) {
                player.sendActionBar(Component.empty());
                continue;
            }


            final String text = interactable.getPromptText(zmPlayer);

            if (text != null && !text.isEmpty()) {
                player.sendActionBar(Component.text(text));
            }
        }
    }

}
