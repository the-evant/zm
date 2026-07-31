package fr.shuvly.zm.game;

import fr.shuvly.zm.Zm;
import fr.shuvly.zm.manager.TaskManager;
import fr.shuvly.zm.map.MapManager;
import fr.shuvly.zm.map.ZmMap;
import fr.shuvly.zm.map.ZmMapInfo;
import fr.shuvly.zm.map.ZmMapParser;
import fr.shuvly.zm.player.ZmPlayer;
import fr.shuvly.zm.player.ZmPlayerState;
import fr.shuvly.zm.task.ComponentPromptDisplayActionBarTask;
import fr.shuvly.zm.world.WorldManager;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Game
{

    private static final Zm MAIN = Zm.getInstance();
    private static final MapManager MAP_MANAGER = MAIN.getMapManager();

    private final String id;

    private GameState state;
    private ZmMap map;

    private final Map<String, ZmPlayer> players;

    private final RoundManager roundManager;
    private final TaskManager taskManager;


    protected Game(String id)
    {
        this.id = id;

        this.state = GameState.UNINITIALIZED;

        this.players = new HashMap<>();

        this.roundManager = new RoundManager();
        this.taskManager = new TaskManager();
    }


    /**
     * Loads the map into memory before the game starts.
     */
    public CompletableFuture<Void> loadMap(ZmMapInfo info)
    {
        this.state = GameState.LOADING_MAP;

        final File mapConfigFile = new File(info.configPath());
        final Path sourceWorldPath = mapConfigFile.getParentFile().toPath().resolve("map");

        return WorldManager.createGameWorldAsync(sourceWorldPath, this.id)
            .thenAccept(loadedWorld -> {
                this.map = ZmMapParser.parse(mapConfigFile, loadedWorld);
                MAIN.getLogger().info("Successfully loaded map: " + info.displayName() + " (" + info.id() + ")");
                this.state = GameState.WAITING_FOR_PLAYERS;
            })
            .exceptionally(e -> {
                MAIN.getLogger().warning("Failed to load map: " + info.displayName() + " (" + info.id() + "): " + e);
                return null;
            });
    }

    public Set<ZmPlayer> getAlivePlayers()
    {
        return players.values().stream()
            .filter((ZmPlayer p) -> p.getState() == ZmPlayerState.ALIVE)
            .collect(Collectors.toUnmodifiableSet());
    }

    public void start()
    {
        if (this.state != GameState.WAITING_FOR_PLAYERS) {
            // todo: throw exception?
            return;
        }

        this.state = GameState.STARTING;

        this.taskManager.addTask(new ComponentPromptDisplayActionBarTask(this));

        this.state = GameState.PLAYING;
    }

    public CompletableFuture<Void> destroy()
    {
        this.state = GameState.ENDING;

        this.taskManager.stopTasks();

        for (ZmPlayer zmPlayer : players.values()) {
            removePlayer(zmPlayer.getPlayer());
        }

        players.clear();

        return WorldManager.destroyGameWorldAsync(map.getWorld())
            .thenAccept(_ -> {
                MAIN.getLogger().info("Successfully destroyed game " + this.id);
            })
            .exceptionally(exception -> {
                MAIN.getLogger().warning("Failed to destroy game " + this.id + ": " + exception.getMessage());
                return null;
            });
    }


    public void addPlayer(Player player)
    {
        this.players.put(player.getUniqueId().toString(), new ZmPlayer(player));
        player.teleportAsync(map.getWorld().getSpawnLocation());
    }

    public void removePlayer(Player player)
    {
        player.teleportAsync(MAP_MANAGER.getLobby().getSpawnLocation());
        this.players.remove(player.getUniqueId().toString());
    }

    public boolean hasPlayer(Player player) { return this.players.containsKey(player.getUniqueId().toString()); }
    public ZmPlayer getZmPlayer(Player player) { return this.players.get(player.getUniqueId().toString()); }

    public String getId() { return id; }
    public GameState getState() { return state; }
    public ZmMap getMap() { return map; }
    public Set<ZmPlayer> getPlayers() { return Set.copyOf(players.values()); }
    public RoundManager getRoundManager() { return roundManager; }

}
