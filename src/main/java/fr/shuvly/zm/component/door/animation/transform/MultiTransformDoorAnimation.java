package fr.shuvly.zm.component.door.animation.transform;

import fr.shuvly.zm.Zm;
import fr.shuvly.zm.component.door.animation.DoorAnimation;
import fr.shuvly.zm.component.door.animation.DoorAnimationCompletionMode;
import fr.shuvly.zm.component.door.animation.DoorAnimationPart;
import fr.shuvly.zm.component.door.animation.DoorAnimationScaleAnchor;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class MultiTransformDoorAnimation
    implements DoorAnimation
{

    private static final Zm MAIN = Zm.getInstance();

    private final List<DoorAnimationPart> parts;
    private final int duration;


    public MultiTransformDoorAnimation(List<DoorAnimationPart> parts, int duration)
    {
        this.parts = parts;
        this.duration = duration;
    }


    @Override
    public void animateOpen(World world, Runnable onComplete)
    {
        final RegionScheduler regionScheduler = MAIN.getServer().getRegionScheduler();

        final int durationTicks = this.duration / 50;
        final List<DisplayState> states = new ArrayList<>();
        Location firstPivot = null;

        for (DoorAnimationPart part : parts) {
            final Vector partPivot = part.pivot();
            final Vector3f partRotation = part.rotationDegrees();
            final List<Block> blocks = part.blocksRegion().getBlocks(world);

            final Location pivotLoc = new Location(world, partPivot.getX() + 0.5, partPivot.getY() + 0.5, partPivot.getZ() + 0.5);

            if (firstPivot == null) {
                firstPivot = pivotLoc;
            }

            final Quaternionf finalRotation = new Quaternionf().rotationXYZ(
                (float) Math.toRadians(partRotation.x()),
                (float) Math.toRadians(partRotation.y()),
                (float) Math.toRadians(partRotation.z())
            );

            for (Block block : blocks) {
                final Vector3f localCenter = new Vector3f(0.5f, 0.5f, 0.5f);
                final Vector3f centerOffset = new Vector3f(
                    (float) (block.getX() + 0.5 - pivotLoc.getX()),
                    (float) (block.getY() + 0.5 - pivotLoc.getY()),
                    (float) (block.getZ() + 0.5 - pivotLoc.getZ())
                );

                final Vector3f initialTranslation = new Vector3f(centerOffset).sub(localCenter);

                final BlockDisplay display = world.spawn(pivotLoc, BlockDisplay.class, d -> {
                    d.setBlock(block.getBlockData());
                    d.setTransformation(new Transformation(
                        initialTranslation,
                        new Quaternionf(),
                        new Vector3f(1f, 1f, 1f),
                        new Quaternionf()
                    ));
                    d.setInterpolationDuration(durationTicks);
                    d.setInterpolationDelay(-1);

                    d.setDisplayWidth(1.0f);
                    d.setDisplayHeight(1.0f);
                });

                final BlockData originalData = block.getBlockData();

                final Location blockLoc = block.getLocation();
                regionScheduler.runDelayed(
                    MAIN,
                    blockLoc,
                    _ -> block.setType(Material.AIR, false),
                    2L
                );

                Vector3f scaledCenterOffset = new Vector3f(centerOffset);
                if (part.scaleAnchor() == DoorAnimationScaleAnchor.PIVOT) {
                    scaledCenterOffset.mul(part.scale());
                }

                final Vector3f rotatedCenterOffset = scaledCenterOffset
                    .rotate(finalRotation)
                    .add(part.translation());

                final Vector3f scaledRotatedLocalCenter = new Vector3f(localCenter)
                    .mul(part.scale())
                    .rotate(finalRotation);

                final Vector3f finalTranslation = new Vector3f(rotatedCenterOffset).sub(scaledRotatedLocalCenter);

                final Transformation finalTransform = new Transformation(
                    finalTranslation,
                    finalRotation,
                    part.scale(),
                    new Quaternionf()
                );

                final int finalX = (int) Math.floor(pivotLoc.getX() + rotatedCenterOffset.x());
                final int finalY = (int) Math.floor(pivotLoc.getY() + rotatedCenterOffset.y());
                final int finalZ = (int) Math.floor(pivotLoc.getZ() + rotatedCenterOffset.z());

                states.add(new DisplayState(
                    display,
                    finalTransform,
                    originalData,
                    finalX, finalY, finalZ,
                    part.delay(),
                    part.completionMode()
                ));
            }
        }

        long maxCleanupDelay = 0;

        for (DisplayState state : states) {
            int delayTicks = state.delay() / 50;
            long triggerDelay = 2L + delayTicks;
            long stateCleanupDelay = triggerDelay + durationTicks;

            if (stateCleanupDelay > maxCleanupDelay) {
                maxCleanupDelay = stateCleanupDelay;
            }

            state.display().getScheduler().runDelayed(
                MAIN,
                _ -> {
                    state.display().setInterpolationDelay(delayTicks);
                    state.display().setTransformation(state.finalTransform());
                },
                null,
                2L
            );

            if (state.completionMode() == DoorAnimationCompletionMode.RM || state.completionMode() == DoorAnimationCompletionMode.BLOCKS) {
                state.display().getScheduler().runDelayed(
                    MAIN,
                    _ -> state.display().remove(),
                    null,
                    stateCleanupDelay
                );
            }

            if (state.completionMode() == DoorAnimationCompletionMode.BLOCKS) {
                final Location finalLoc = new Location(world, state.finalX(), state.finalY(), state.finalZ());

                regionScheduler.runDelayed(
                    MAIN,
                    finalLoc,
                    _ -> {
                        final Block b = world.getBlockAt(finalLoc);
                        b.setBlockData(state.blockData(), false);
                    },
                    stateCleanupDelay
                );
            }
        }

        if (onComplete != null && firstPivot != null) {
            regionScheduler.runDelayed(
                MAIN,
                firstPivot,
                _ -> onComplete.run(),
                maxCleanupDelay
            );
        }
    }

}
