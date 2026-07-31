package fr.shuvly.zm.world;

import fr.shuvly.zm.Zm;
import fr.shuvly.zm.world.io.WorldFileManager;
import org.bukkit.Bukkit;
import org.bukkit.GameRules;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

public class WorldManager
{

    private static final Zm MAIN = Zm.getInstance();


    private WorldManager() {}


    /**
     * Copies the world folder asynchronously, then loads the Bukkit World on the main thread.
     */
    public static CompletableFuture<World> createGameWorldAsync(Path source, String gameId) {
        Path target = Paths.get(Bukkit.getWorldContainer().getAbsolutePath(), "world", "dimensions", "minecraft", gameId);

        return WorldFileManager.copyWorldAsync(source, target).thenApplyAsync(_ -> {
            CompletableFuture<World> worldFuture = new CompletableFuture<>();

            Bukkit.getGlobalRegionScheduler().execute(Zm.getInstance(), () -> {
                final WorldCreator creator = new WorldCreator(gameId);
                creator.generator(new VoidGenerator());
//                creator.keepSpawnInMemory(false);

                final World gameWorld = Bukkit.createWorld(creator);

                if (gameWorld != null) {
                    gameWorld.setAutoSave(false);
                    gameWorld.setGameRule(GameRules.SPAWN_MOBS, false);
                    gameWorld.setGameRule(GameRules.ADVANCE_TIME, false);
                    gameWorld.setGameRule(GameRules.ADVANCE_WEATHER, false);
                    gameWorld.setGameRule(GameRules.RANDOM_TICK_SPEED, 0);
                    worldFuture.complete(gameWorld);
                } else {
                    worldFuture.completeExceptionally(new RuntimeException("Bukkit failed to create world: " + gameId));
                }
            });

            return worldFuture.join();
        });
    }

    /**
     * Unloads the world without saving, then deletes the folder asynchronously.
     */
    public static CompletableFuture<Void> destroyGameWorldAsync(World world)
    {
        final String worldName = world.getName();
        final CompletableFuture<Void> future = new CompletableFuture<>();

        Bukkit.getGlobalRegionScheduler().execute(MAIN, () -> {
            if (Bukkit.unloadWorld(world, false)) {
                final Path target = Paths.get(Bukkit.getWorldContainer().getAbsolutePath(), worldName);
                WorldFileManager.deleteWorldAsync(target).thenRun(() -> future.complete(null));
            } else {
                future.completeExceptionally(new RuntimeException("Failed to unload Bukkit world: " + worldName));
            }
        });

        return future;
    }

}
