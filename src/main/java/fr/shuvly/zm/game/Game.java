package fr.shuvly.zm.game;

import fr.shuvly.zm.Zm;
import fr.shuvly.zm.component.mystery_box.MysteryBoxManager;
import fr.shuvly.zm.manager.TaskManager;
import fr.shuvly.zm.map.*;
import fr.shuvly.zm.map.spawnpoints.ZmMapSpawnPoints;
import fr.shuvly.zm.player.ZmPlayer;
import fr.shuvly.zm.player.ZmPlayerState;
import fr.shuvly.zm.player.state.PlayerStateManager;
import fr.shuvly.zm.player.state.PlayerStateSettings;
import fr.shuvly.zm.task.ComponentPromptDisplayActionBarTask;
import fr.shuvly.zm.task.PlayerStateTask;
import fr.shuvly.zm.world.WorldManager;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
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
    private final PlayerStateManager playerStateManager;
    private MysteryBoxManager mysteryBoxManager;


    protected Game(String id)
    {
        this.id = id;

        this.state = GameState.UNINITIALIZED;

        this.players = new HashMap<>();

        this.roundManager = new RoundManager();
        this.taskManager = new TaskManager();
        this.playerStateManager = new PlayerStateManager(this, PlayerStateSettings.base());
    }


    /**
     * Loads the map into memory before the game starts.
     */
    public CompletableFuture<Void> loadMap(ZmMapInfo info)
    {
        this.state = GameState.LOADING_MAP;

        final File mapPath = new File(info.path());
        final Path sourceWorldPath = mapPath.toPath().resolve("world");

        return WorldManager.createGameWorldAsync(sourceWorldPath, this.id)
            .thenAccept(loadedWorld -> {
                this.map = ZmMapLoader.load(mapPath, loadedWorld);
                MAIN.getLogger().info("Successfully loaded map: " + info.displayName() + " (" + info.id() + ")");
                this.state = GameState.WAITING_FOR_PLAYERS;
            })
            .exceptionally(e -> {
                MAIN.getLogger().warning("Failed to load map: " + info.displayName() + " (" + info.id() + "): " + e);
                e.printStackTrace();
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

        final ZmMapSpawnPoints mapSpawnPoints = map.getSpawnPoints();

        if (!mapSpawnPoints.getLobbySpawnPoints().isEmpty()) {
            for (ZmPlayer player : players.values()) {
                player.getPlayer().teleportAsync(mapSpawnPoints.getNextGameSpawnPoint());
                Objects.requireNonNull(player.getPlayer().getAttribute(Attribute.MAX_HEALTH)).setBaseValue(20);
            }
        }

        this.taskManager.addTask(new ComponentPromptDisplayActionBarTask(this));
        this.taskManager.addTask(new PlayerStateTask(this));

        this.mysteryBoxManager = new MysteryBoxManager(
            this.map.getComponentRegistry(),
            this.map.getMysteryBoxSettings()
        );

        this.state = GameState.PLAYING;
    }

    /**
     * Starts the next round, bringing dead players back.
     */
    public void nextRound()
    {
        if (this.state != GameState.PLAYING) {
            return;
        }

        this.roundManager.nextRound();
        this.playerStateManager.respawnDeadPlayers();
    }

    /**
     * Ends the game if no player is alive anymore.
     * Downed players count as not alive: nobody is left to revive them.
     */
    public void checkGameOver()
    {
        if (this.state != GameState.PLAYING) {
            return;
        }

        final boolean anyoneAlive = players.values().stream()
            .anyMatch(p -> p.getState() == ZmPlayerState.ALIVE);

        if (!anyoneAlive) {
            gameOver();
        }
    }

    private void gameOver()
    {
        this.state = GameState.GAME_OVER;

        this.gameOverAnimation.play(this, () -> {
            final GameManager gameManager = MAIN.getGameManager();

            // The game may have been destroyed manually during the animation
            if (gameManager.getGame(this.id) == this) {
                gameManager.destroyGame(this.id);
            }
        });
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

        final ZmMapSpawnPoints mapSpawnPoints = map.getSpawnPoints();
        final Location loc =
            mapSpawnPoints.getLobbySpawnPoints().isEmpty()
                ? mapSpawnPoints.getNextGameSpawnPoint()
                : mapSpawnPoints.getNextLobbySpawnPoint();

        player.teleportAsync(loc);
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
    public PlayerStateManager getPlayerStateManager() { return playerStateManager; }

}
