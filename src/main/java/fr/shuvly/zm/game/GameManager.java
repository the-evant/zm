package fr.shuvly.zm.game;

import fr.shuvly.zm.Zm;
import fr.shuvly.zm.exception.MapParseException;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class GameManager
{

    private static final Zm MAIN = Zm.getInstance();

    private int createdGamesAmount = 1;
    private final Set<Game> activeGames = new HashSet<>();
    private final Map<String, Game> playersGame = new HashMap<>();
    //                ^^^^^^ player uuid


    public Game createGame(String mapName)
        throws MapParseException
    {
        final Game game = new Game(this.createdGamesAmount);
        final File mapFile = new File(MAIN.getDataFolder(), "maps/" + mapName + "/" + mapName + ".yml");

        if (mapFile.exists()) {
            game.loadMap(mapFile);
        } else {
            throw new MapParseException("Could not find map file: " + mapFile.getPath());
        }

        this.activeGames.add(game);
        this.createdGamesAmount++;
        return game;
    }

    public void addPlayer(Player player, String gameId)
    {
        final Game game = getGame(gameId);

        if (game != null) {
            game.addPlayer(player);
            this.playersGame.put(player.getUniqueId().toString(), game);
        }
    }

    public void removePlayer(Player player)
    {
        final Game game = this.playersGame.remove(player.getUniqueId().toString());

        if (game != null) {
            game.removePlayer(player);
        }
    }

    public Game getPlayerGame(Player player)
    {
        return this.playersGame.get(player.getUniqueId().toString());
    }


    public Set<Game> getActiveGames() { return Collections.unmodifiableSet(activeGames); }

    public Game getGame(String id)
    {
        return this.activeGames.stream()
            .filter(game -> game.getId().equals(id))
            .findFirst()
            .orElse(null);
    }

}
