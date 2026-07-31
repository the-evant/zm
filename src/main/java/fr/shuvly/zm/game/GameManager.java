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


    public CompletableFuture<Game> createGame(String mapName)
        throws MapParseException
    {
        final ZmMapInfo mapInfo = MAIN.getMapManager().getMapInfo(mapName);

        if (mapInfo == null) {
            throw new MapParseException("Map '" + mapName + "' is not loaded or does not exist.");
        }

        final String gameId = createdGamesAmount + "-" + mapName;
        final Game game = new Game(gameId);

        this.activeGames.put(gameId, game);
        this.createdGamesAmount++;

        return game.loadMap(mapInfo).thenApply(_ -> game);
    }

    public CompletableFuture<Void> destroyGame(String gameId)
    {
        final CompletableFuture<Void> future = new CompletableFuture<>();
        final Game game = this.activeGames.get(gameId);

        if (game == null) {
            future.completeExceptionally(new NullPointerException("Game '" + gameId + "' does not exist."));
            return future;
        }

        for (ZmPlayer player : game.getPlayers()) {
            this.playersGame.remove(player.getPlayer().getUniqueId().toString());
        }

        game.destroy();
        this.activeGames.remove(gameId);
        future.complete(null);
        return future;
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
