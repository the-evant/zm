package fr.shuvly.zm.world.io;

import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class WorldFileManager
{

    private static List<String> filesToIgnore = Arrays.asList(
        "session.lock",
        "uid.dat"
    );


    private WorldFileManager() {}


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
        });
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
                        throws IOException
                    {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public @NonNull FileVisitResult postVisitDirectory(@NonNull Path dir, IOException exc)
                        throws IOException
                    {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                throw new CompletionException("Failed to delete world at " + target, e);
            }
        });
    }

}
