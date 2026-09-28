package fr.shuvly.zm.component.mystery_box;

import fr.shuvly.zm.Zm;
import fr.shuvly.zm.player.ZmPlayer;
import fr.shuvly.zm.weapon.ZmWeapon;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MysteryBoxAnimator
{

    private static final int RISE_SINK_DURATION_TICKS = 40;
    private static final long ANIM_DURATION_TICKS = 60L;
    private static final long RETRIEVE_TIMEOUT_TICKS = 200L;

    private static final Zm MAIN = Zm.getInstance();

    private final Location weaponLocation;
    private final World world;
    private ItemDisplay displayEntity;

    private ScheduledTask processTask;
    private ScheduledTask timeoutTask;
    private ScheduledTask sinkingTask;


    public MysteryBoxAnimator(
        Location weaponLocation,
        Vector direction
    )
    {
        this.weaponLocation = weaponLocation.clone();

        if (direction.lengthSquared() > 0) {
            this.weaponLocation.setDirection(direction);
        }

        this.world = this.weaponLocation.getWorld();
    }


    public void startProcessing(
        ZmPlayer zmPlayer,
        ZmWeapon finalWeapon,
        List<ZmWeapon> mysteryBoxWeapons,
        Runnable onReady,
        Runnable onTimeout
    )
    {
        MAIN.getServer().getRegionScheduler().execute(MAIN, weaponLocation, () -> {
            this.displayEntity = world.spawn(weaponLocation, ItemDisplay.class, display -> {
                display.setTeleportDuration(0);
                display.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.FIXED);
                display.setItemStack(mysteryBoxWeapons.getFirst().buildItemStack(zmPlayer));
                display.setInterpolationDelay(0);
                display.setInterpolationDuration(RISE_SINK_DURATION_TICKS);

                final Transformation transform = display.getTransformation();
                transform.getTranslation().add(0, 1.5f, 0);

                display.setTransformation(transform);
            });

            this.processTask = displayEntity.getScheduler().runAtFixedRate(MAIN, task -> {
                final ZmWeapon randomWeapon = mysteryBoxWeapons.get(ThreadLocalRandom.current().nextInt(mysteryBoxWeapons.size()));
                displayEntity.setItemStack(randomWeapon.buildItemStack(zmPlayer));
                world.playSound(weaponLocation, Sound.BLOCK_NOTE_BLOCK_HAT, 1.0f, 1.5f);
            }, null, 1L, 5L);

            displayEntity.getScheduler().runDelayed(MAIN, task -> {
                if (processTask != null) {
                    processTask.cancel();
                }

                displayEntity.setItemStack(finalWeapon.buildItemStack(zmPlayer));
                world.playSound(weaponLocation, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

                onReady.run();

                this.timeoutTask = displayEntity.getScheduler().runDelayed(MAIN, tTask -> {
                    displayEntity.setInterpolationDelay(0);
                    displayEntity.setInterpolationDuration(40);

                    final Transformation downTransform = displayEntity.getTransformation();
                    downTransform.getTranslation().sub(0, 1.5f, 0); // Go back down
                    displayEntity.setTransformation(downTransform);

                    this.sinkingTask = displayEntity.getScheduler().runDelayed(MAIN, cleanTask -> {
                        onTimeout.run();
                    }, null, RISE_SINK_DURATION_TICKS);
                }, null, RETRIEVE_TIMEOUT_TICKS);
            }, null, ANIM_DURATION_TICKS);
        });
    }

    public void cleanup()
    {
        if (this.processTask != null) {
            this.processTask.cancel();
        }
        if (this.timeoutTask != null) {
            this.timeoutTask.cancel();
        }
        if (this.sinkingTask != null) {
            this.sinkingTask.cancel();
        }
        if (this.displayEntity != null && this.displayEntity.isValid()) {
            this.displayEntity.remove();
        }
        this.displayEntity = null;
    }

}
