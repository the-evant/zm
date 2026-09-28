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
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MysteryBoxAnimator
{

    private static final int RISE_SINK_DURATION_TICKS = 40;
    private static final long ANIM_DURATION_TICKS = 60L;
    private static final long WAIT_BEFORE_SINK_TICKS = 80L;
    private static final int SINK_DURATION_TICKS = 120;

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
                display.setItemStack(mysteryBoxWeapons.getFirst().buildItemStack(zmPlayer));
                display.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.FIXED);
                display.setTransformation(new Transformation(
                    new Vector3f(0f, 0f, 0f), new Quaternionf(),
                    new Vector3f(.6f, .6f, .6f), new Quaternionf()
                ));
                display.setInterpolationDuration(40);
                display.setTeleportDuration(0);
            });

            this.displayEntity.getScheduler().runDelayed(MAIN, t -> {
                displayEntity.setInterpolationDelay(0);
                displayEntity.setInterpolationDuration(RISE_SINK_DURATION_TICKS);

                final Transformation riseTransform = displayEntity.getTransformation();
                riseTransform.getTranslation().add(0, 1f, 0);
                displayEntity.setTransformation(riseTransform);
            }, null, 2L);

            final int[] lastIndex = { -1 };

            this.processTask = displayEntity.getScheduler().runAtFixedRate(MAIN, task -> {
                int nextIndex;
                do {
                    nextIndex = ThreadLocalRandom.current().nextInt(mysteryBoxWeapons.size());
                } while (mysteryBoxWeapons.size() > 1 && nextIndex == lastIndex[0]);

                lastIndex[0] = nextIndex;

                final ZmWeapon randomWeapon = mysteryBoxWeapons.get(nextIndex);
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

                this.sinkingTask = displayEntity.getScheduler().runDelayed(MAIN, sTask -> {
                    displayEntity.setInterpolationDelay(0);
                    displayEntity.setInterpolationDuration(SINK_DURATION_TICKS);

                    final Transformation downTransform = displayEntity.getTransformation();
                    downTransform.getTranslation().sub(0, 1f, 0);
                    displayEntity.setTransformation(downTransform);
                }, null, WAIT_BEFORE_SINK_TICKS);

                this.timeoutTask = displayEntity.getScheduler().runDelayed(MAIN, tTask -> {
                    onTimeout.run();
                }, null, WAIT_BEFORE_SINK_TICKS + SINK_DURATION_TICKS);

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
