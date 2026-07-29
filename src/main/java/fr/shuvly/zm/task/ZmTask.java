package fr.shuvly.zm.task;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

import java.util.function.Consumer;

public interface ZmTask
    extends Consumer<ScheduledTask>
{

    /**
     * How many ticks to wait before running the first time.
     */
    long getInitialDelay();

    /**
     * How many ticks between each execution.
     */
    long getPeriod();

}
