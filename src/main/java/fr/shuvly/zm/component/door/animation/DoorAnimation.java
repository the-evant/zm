package fr.shuvly.zm.component.door.animation;

import org.bukkit.World;

@FunctionalInterface
public interface DoorAnimation
{

    /**
     * Executes the visual transition when a door opens or debris is cleared.
     *
     * @param   world       The world where the animation takes place
     * @param   onComplete  Callback to execute when the animation finishes
     */
    void animateOpen(World world, Runnable onComplete);

}
