package fr.shuvly.zm.map.spawnpoints;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class ZmMapSpawnPoints
{

    private final List<Location> lobbySpawnPoints = new ArrayList<>();
    private final List<Location> gameSpawnPoints = new ArrayList<>();

    private int lobbySpawnsCounter = 0;
    private int gameSpawnsCounter = 0;


    public void addLobbySpawnPoint(Location location) { lobbySpawnPoints.add(location); }
    public void addGameSpawnPoint(Location location) { gameSpawnPoints.add(location); }

    public Location getNextLobbySpawnPoint()
    {
        return lobbySpawnPoints.get(lobbySpawnsCounter++ % lobbySpawnPoints.size());
    }

    public Location getNextGameSpawnPoint()
    {
        return gameSpawnPoints.get(gameSpawnsCounter++ % gameSpawnPoints.size());
    }

    public List<Location> getLobbySpawnPoints() { return lobbySpawnPoints; }
    public List<Location> getGameSpawnPoints() { return gameSpawnPoints; }

}
