package fr.shuvly.zm.manager;

import fr.shuvly.zm.Zm;
import fr.shuvly.zm.task.ZmTask;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class TaskManager
{

    private static final Zm MAIN = Zm.getInstance();

    private final List<ScheduledTask> activeTasks = new ArrayList<>();


    public void addTask(ZmTask task)
    {
        final ScheduledTask scheduledTask = Bukkit.getGlobalRegionScheduler()
            .runAtFixedRate(
                MAIN,
                task,
                task.getInitialDelay(),
                task.getPeriod()
            );

        activeTasks.add(scheduledTask);
    }

    public void stopTasks()
    {
        for (ScheduledTask task : activeTasks) {
            task.cancel();
        }
        activeTasks.clear();
    }

}
