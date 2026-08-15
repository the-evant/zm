package fr.shuvly.zm.component.pap;

import fr.shuvly.zm.Zm;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.*;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class PapAnimator
{

    private static final Zm MAIN = Zm.getInstance();

    private final Location location;
    private final World world;
    private ItemDisplay displayEntity;

    private ScheduledTask processTask;
    private ScheduledTask timeoutTask;
    private ScheduledTask warningTask;


    public PapAnimator(Location location, Vector direction)
    {
        this.location = location.clone();

        if (direction.lengthSquared() > 0) {
            this.location.setDirection(direction);
        }

        this.world = this.location.getWorld();
    }


    public void startProcessing(
        ItemStack original,
        ItemStack upgraded,
        int processTicks,
        Runnable onReady
    )
    {
        if (this.world == null) {
            return;
        }

        int halfProcess = Math.max(1, processTicks / 2);

        MAIN.getServer().getRegionScheduler().execute(MAIN, this.location, () -> {
            this.displayEntity = this.world.spawn(this.location, ItemDisplay.class, display -> {
                display.setItemStack(original);
                display.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.FIXED);
                display.setTransformation(new Transformation(
                    new Vector3f(0f, 0f, 0f), new Quaternionf(),
                    new Vector3f(1f, 1f, 1f), new Quaternionf()
                ));
                display.setInterpolationDuration(halfProcess);
                display.setInterpolationDelay(-1);
            });

            this.processTask = this.displayEntity.getScheduler().runDelayed(MAIN, _ -> {
                if (!this.displayEntity.isValid()) { // should never happen lol
                    return;
                }

                this.world.playSound(this.location, Sound.BLOCK_PISTON_EXTEND, 1.0f, 0.8f);
                this.world.playSound(this.location, Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1.0f, 1.2f);

                this.displayEntity.setInterpolationDelay(0);
                this.displayEntity.setTransformation(new Transformation(
                    new Vector3f(0f, 0f, -0.6f), new Quaternionf(),
                    new Vector3f(1f, 1f, 1f), new Quaternionf()
                ));

                this.displayEntity.getScheduler().runDelayed(MAIN, _ -> {
                    if (!this.displayEntity.isValid()) {
                        return;
                    }

                    this.displayEntity.setItemStack(upgraded);
                    this.displayEntity.setGlowColorOverride(Color.AQUA);
                    this.displayEntity.setGlowing(true);

                    this.displayEntity.setInterpolationDuration(halfProcess);
                    this.displayEntity.setInterpolationDelay(0);
                    this.displayEntity.setTransformation(new Transformation(
                        new Vector3f(0f, 0f, 0.1f), new Quaternionf(),
                        new Vector3f(1f, 1f, 1f), new Quaternionf()
                    ));

                    this.world.spawnParticle(Particle.ENCHANT, this.location.clone().add(0, 0.5, 0), 40, 0.3, 0.3, 0.3, 0.5);
                    this.world.playSound(this.location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    this.world.playSound(this.location, Sound.BLOCK_BEACON_POWER_SELECT, 1.0f, 1.4f);

                    onReady.run();
                }, null, halfProcess);

            }, null, 2L);
        });
    }

    public void startTimeoutSequence(int timeoutTicks, Runnable onTimeoutExpired)
    {
        int warningStartTicks = Math.max(1, timeoutTicks - 100);

        this.warningTask = MAIN.getServer().getRegionScheduler().runDelayed(MAIN, this.location, _ -> {
            if (this.displayEntity == null || !this.displayEntity.isValid()) {
                return;
            }

            this.displayEntity.setGlowColorOverride(Color.RED);

            this.warningTask = this.displayEntity.getScheduler().runAtFixedRate(MAIN, _ -> {
                this.world.playSound(this.location, Sound.BLOCK_NOTE_BLOCK_HAT, 1.0f, 2.0f);

                this.displayEntity.setInterpolationDuration(5);
                this.displayEntity.setInterpolationDelay(0);
                this.displayEntity.setTransformation(new Transformation(
                    new Vector3f(0f, 0f, 0.1f), new Quaternionf(),
                    new Vector3f(0.75f, 0.75f, 0.75f), new Quaternionf()
                ));

                this.displayEntity.getScheduler().runDelayed(MAIN, expandTask -> {
                    this.displayEntity.setInterpolationDuration(5);
                    this.displayEntity.setInterpolationDelay(0);
                    this.displayEntity.setTransformation(new Transformation(
                        new Vector3f(0f, 0f, 0.1f), new Quaternionf(),
                        new Vector3f(1f, 1f, 1f), new Quaternionf()
                    ));
                }, null, 5L);
            }, null, 1L, 10L);
        }, warningStartTicks);

        this.timeoutTask = MAIN.getServer().getRegionScheduler().runDelayed(MAIN, this.location, _ -> {
            this.world.playSound(this.location, Sound.BLOCK_FIRE_EXTINGUISH, 1.0f, 0.8f);
            this.world.spawnParticle(Particle.SMOKE, this.location.clone().add(0, 0.5, 0), 15, 0.2, 0.2, 0.2, 0.05);
            onTimeoutExpired.run();
        }, timeoutTicks);
    }

    public void cleanup()
    {
        if (this.processTask != null) {
            this.processTask.cancel();
        }
        if (this.warningTask != null) {
            this.warningTask.cancel();
        }
        if (this.timeoutTask != null) {
            this.timeoutTask.cancel();
        }
        if (this.displayEntity != null && this.displayEntity.isValid()) {
            this.displayEntity.remove();
        }
        this.displayEntity = null;
    }

}