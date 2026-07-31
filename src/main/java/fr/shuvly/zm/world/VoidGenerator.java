package fr.shuvly.zm.world;

import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class VoidGenerator
    extends ChunkGenerator
{

    @Override
    public void generateNoise(
        @NotNull WorldInfo worldInfo,
        @NotNull Random random,
        int chunkX, int chunkZ,
        @NotNull ChunkData chunkData
    ) {} // nothing lol

}
