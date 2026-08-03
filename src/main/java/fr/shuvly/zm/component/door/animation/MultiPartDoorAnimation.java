package fr.shuvly.zm.component.door.animation;

import fr.shuvly.zm.Zm;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

record DisplayState(
    BlockDisplay display,
    Transformation finalTransform,
    BlockData blockData,
    int finalX, int finalY, int finalZ
) {}

public class MultiPartDoorAnimation
    implements DoorAnimation
{

    private static final Zm MAIN = Zm.getInstance();
    private static final RegionScheduler REGION_SCHEDULER = MAIN.getServer().getRegionScheduler();

    private final List<DoorAnimationPart> parts;
    private final int duration;


    public MultiPartDoorAnimation(List<DoorAnimationPart> parts, int durationTicks)
    {
        this.parts = parts;
        this.duration = durationTicks;
    }


    @Override
    public void animateOpen(World world, Runnable onComplete)
    {
        final int durationTicks = this.duration * 20;
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
                        new AxisAngle4f(),
                        new Vector3f(1f, 1f, 1f),
                        new AxisAngle4f()
                    ));
                    d.setInterpolationDuration(durationTicks);
                    d.setInterpolationDelay(-1);
                });

                final BlockData originalData = block.getBlockData();

                block.setType(Material.AIR, false);

                final Vector3f rotatedCenterOffset = new Vector3f(centerOffset)
                    .rotate(finalRotation)
                    .add(part.translation());
                final Vector3f rotatedLocalCenter = new Vector3f(localCenter).rotate(finalRotation);
                final Vector3f finalTranslation = new Vector3f(rotatedCenterOffset).sub(rotatedLocalCenter);

                final Transformation finalTransform = new Transformation(
                    finalTranslation,
                    finalRotation,
                    new Vector3f(1f, 1f, 1f),
                    new Quaternionf()
                );

                final int finalX = (int) Math.floor(pivotLoc.getX() + rotatedCenterOffset.x());
                final int finalY = (int) Math.floor(pivotLoc.getY() + rotatedCenterOffset.y());
                final int finalZ = (int) Math.floor(pivotLoc.getZ() + rotatedCenterOffset.z());

                states.add(new DisplayState(
                    display,
                    finalTransform,
                    originalData,
                    finalX, finalY, finalZ
                ));
            }
        }

        for (DisplayState state : states) {
            state.display().getScheduler().runDelayed(
                MAIN,
                _ -> {
                    state.display().setTransformation(state.finalTransform());
                    state.display().setInterpolationDelay(0);
                },
                null,
                2L
            );
        }

        long cleanupDelay = durationTicks + 2L;

        for (DisplayState state : states) {
            state.display().getScheduler().runDelayed(
                MAIN,
                _ -> state.display().remove(),
                null,
                cleanupDelay
            );

            final Location finalLoc = new Location(world, state.finalX(), state.finalY(), state.finalZ());

            REGION_SCHEDULER.runDelayed(
                MAIN,
                finalLoc,
                _ -> {
                    Block b = world.getBlockAt(finalLoc);
                    b.setBlockData(state.blockData(), false);
                },
                cleanupDelay
            );
        }

        if (onComplete != null && firstPivot != null) {
            REGION_SCHEDULER.runDelayed(
                MAIN,
                firstPivot,
                _ -> onComplete.run(),
                cleanupDelay
            );
        }
    }
}