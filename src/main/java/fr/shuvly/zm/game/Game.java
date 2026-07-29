package fr.shuvly.zm.game;

import fr.shuvly.zm.Zm;
import fr.shuvly.zm.manager.TaskManager;
import fr.shuvly.zm.map.ZmMap;
import fr.shuvly.zm.map.ZmMapInfo;
import fr.shuvly.zm.map.ZmMapParser;
import fr.shuvly.zm.player.ZmPlayer;
import fr.shuvly.zm.player.ZmPlayerState;
import fr.shuvly.zm.task.ComponentPromptDisplayActionBarTask;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class Game
{

    private static final Zm MAIN = Zm.getInstance();

    private String id;

    private GameState state;
    private ZmMap map;

    private final Map<String, ZmPlayer> players;

    private final RoundManager roundManager;
    private final TaskManager taskManager;


    protected Game(int gameIndex)
    {
        this.players = new HashMap<>();
        this.state = GameState.UNINITIALIZED;
        this.id = String.valueOf(gameIndex);

        this.roundManager = new RoundManager();
        this.taskManager = new TaskManager();
    }


    /**
     * Loads the map into memory before the game starts.
     */
    public void loadMap(File mapFile)
    {
        this.state = GameState.LOADING_MAP;
        this.map = ZmMapParser.parse(mapFile);

        final ZmMapInfo mapInfo = map.getInfo();

        this.id += "-zm_" + mapInfo.name();
        MAIN.getLogger().info("Successfully loaded map: " + mapInfo.displayName() + " (" + mapInfo.name() + ")");

        this.state = GameState.WAITING_FOR_PLAYERS;
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


    public void addPlayer(Player player) { this.players.put(player.getUniqueId().toString(), new ZmPlayer(player)); }
    public void removePlayer(Player player) { this.players.remove(player.getUniqueId().toString()); }
    public boolean hasPlayer(Player player) { return this.players.containsKey(player.getUniqueId().toString()); }

    public String getId() { return id; }
    public GameState getState() { return state; }
    public ZmMap getMap() { return map; }
    public Set<ZmPlayer> getPlayers() { return Set.copyOf(players.values()); }
    public RoundManager getRoundManager() { return roundManager; }

}
