package fr.shuvly.zm.component.door.animation;

import fr.shuvly.zm.map.region.Region;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.List;

public class InstantDoorAnimation
    implements DoorAnimation
{

    private final Region blocksRegion;


    public InstantDoorAnimation(Region blocksRegion)
    {
        this.blocksRegion = blocksRegion;
    }


    @Override
    public void animateOpen(World world, Runnable onComplete)
    {
        final List<Block> blocks = blocksRegion.getBlocks(world);

        for (Block block : blocks) {
//            world.spawnParticle(
//                Particle.BLOCK,
//                block.getLocation().add(0.5, 0.5, 0.5), 10,
//                block.getBlockData()
//            );
            block.setType(Material.AIR, false);
        }

        if (onComplete != null) {
            onComplete.run();
        }
    }

}
