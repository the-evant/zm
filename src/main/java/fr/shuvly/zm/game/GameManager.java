package fr.shuvly.zm.game;

import fr.shuvly.zm.Zm;
import fr.shuvly.zm.exception.MapParseException;
import fr.shuvly.zm.map.ZmMap;
import fr.shuvly.zm.parser.ZmMapParser;
import fr.shuvly.zm.player.ZmPlayer;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GameManager
{

    private static final Zm MAIN = Zm.getInstance();

    private Set<ZmPlayer> players;
    private RoundManager roundManager;

    private GameState state;
    private ZmMap map;


    public GameManager()
    {
        this.players = new HashSet<>();
        this.roundManager = new RoundManager();
        this.state = GameState.LOADING;
    }


    /**
     * Loads the map into memory before the game starts.
     */
    public void loadMap(File mapFile)
    {
        final ZmMapParser parser = new ZmMapParser();

        try {
            this.map = parser.parse(mapFile);
        } catch (MapParseException exception) {
            MAIN.getLogger().severe("Could not parse map file " + mapFile.getName() + ": " + exception.getMessage());
        }
        MAIN.getLogger().info("Successfully loaded map: " + map.getDisplayName());
    }


    public GameState getState() { return state; }
    public ZmMap getMap() { return map; }
    public Set<ZmPlayer> getPlayers() { return Collections.unmodifiableSet(players); }
    public RoundManager getRoundManager() { return roundManager; }

}
