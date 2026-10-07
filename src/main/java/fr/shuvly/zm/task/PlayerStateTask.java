package fr.shuvly.zm.task;

import fr.shuvly.zm.game.Game;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

/**
 * Ticks bleed out timers, revive progress and dead players' spectating.
 */
public class PlayerStateTask
    implements ZmTask
{

    private final Game game;


    public PlayerStateTask(Game game)
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
        return 1L;
    }

    @Override
    public void accept(ScheduledTask scheduledTask)
    {
        game.getPlayerStateManager().tick();
    }

}
