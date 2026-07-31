package fr.shuvly.zm.world.io;

import fr.shuvly.zm.Zm;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorldFileManager
{

    private static final Zm MAIN = Zm.getInstance();
    private static final ExecutorService IO_EXECUTOR = Executors.newCachedThreadPool();
    private static final List<String> filesToIgnore = Arrays.asList(
        "session.lock",
        "uid.dat"
    );


    private WorldFileManager() {}


    public static void shutdown()
    {
        IO_EXECUTOR.shutdown();
    }

    public static CompletableFuture<Void> copyWorldAsync(Path source, Path target)
    {
        return CompletableFuture.runAsync(() -> {
            try {
                Files.walkFileTree(source, new SimpleFileVisitor<>() {
                    @Override
                    public @NonNull FileVisitResult preVisitDirectory(@NonNull Path dir, @NonNull BasicFileAttributes attrs)
                        throws IOException
                    {
                        final Path targetDir = target.resolve(source.relativize(dir));

                        if (!Files.exists(targetDir)) {
                            Files.createDirectory(targetDir);
                        }

                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public @NonNull FileVisitResult visitFile(@NonNull Path file, @NonNull BasicFileAttributes attrs)
                        throws IOException
                    {
                        if (filesToIgnore.contains(file.getFileName().toString())) {
                            return FileVisitResult.CONTINUE;
                        }

                        Files.copy(file, target.resolve(source.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                throw new CompletionException("Failed to copy world from " + source + " to " + target, e);
            }
        }, IO_EXECUTOR);
    }

    public static CompletableFuture<Void> deleteWorldAsync(Path target)
    {
        if (!Files.exists(target)) {
            return CompletableFuture.completedFuture(null);
        }

        return CompletableFuture.runAsync(() -> {
            try {
                Files.walkFileTree(target, new SimpleFileVisitor<>() {
                    @Override
                    public @NonNull FileVisitResult visitFile(@NonNull Path file, @NonNull BasicFileAttributes attrs)
                    {
                        try {
                            Files.delete(file);
                        } catch (IOException exception) {
                            MAIN.getLogger().warning("Could not delete file '" + file + "': " + exception.getMessage() + ". Falling back to deletion on JVM exit.");
                            file.toFile().deleteOnExit();
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public @NonNull FileVisitResult postVisitDirectory(@NonNull Path dir, IOException exc)
                    {
                        try {
                            Files.delete(dir);
                        } catch (IOException exception) {
                            MAIN.getLogger().warning("Could not delete dir '" + dir + "': " + exception.getMessage() + ". Falling back to deletion on JVM exit.");
                            dir.toFile().deleteOnExit();
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException exception) {
                throw new CompletionException("Failed to delete world at " + target, exception);
            }
        }, IO_EXECUTOR);
    }

}
