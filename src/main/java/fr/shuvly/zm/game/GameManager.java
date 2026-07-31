package fr.shuvly.zm.game;

import fr.shuvly.zm.Zm;
import fr.shuvly.zm.exception.MapParseException;
import fr.shuvly.zm.map.ZmMapInfo;
import fr.shuvly.zm.player.ZmPlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class GameManager
{

    private static final Zm MAIN = Zm.getInstance();

    private int createdGamesAmount = 1;
    private final Map<String, Game> activeGames = new HashMap<>();
    private final Map<String, Game> playersGame = new HashMap<>();
    //                ^^^^^^ player uuid


    public void shutdown()
    {
        for (Game game : activeGames.values()) {
            destroyGame(game.getId());
        }
    }

    public CompletableFuture<Game> createGame(String mapName)
        throws MapParseException
    {
        final ZmMapInfo mapInfo = MAIN.getMapManager().getMapInfo(mapName);

        if (mapInfo == null) {
            throw new MapParseException("Map '" + mapName + "' is not loaded or does not exist.");
        }

        final String gameId = createdGamesAmount + "-" + mapName;
        final Game game = new Game(gameId);

        return game.loadMap(mapInfo)
            .thenApply(_ -> {
                this.activeGames.put(gameId, game);
                this.createdGamesAmount++;
                return game;
            })
            .exceptionally(exception -> {
                MAIN.getLogger().severe("Could not create game '" + gameId + "': " + exception.getMessage());
                return null;
            });
    }

    public CompletableFuture<Void> destroyGame(String gameId)
        throws NullPointerException
    {
        final Game game = this.activeGames.get(gameId);

        if (game == null) {
            throw new NullPointerException("Game '" + gameId + "' does not exist.");
        }

        for (ZmPlayer player : game.getPlayers()) {
            this.playersGame.remove(player.getPlayer().getUniqueId().toString());
        }

        this.activeGames.remove(gameId);

        return game.destroy();
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
    public Game getGame(String id) { return this.activeGames.get(id); }

}
