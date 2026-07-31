package fr.shuvly.zm.map.spawnpoints;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public record ZmMapSpawnPoints(
    List<Location> lobbySpawnPoints,
    List<Location> gameSpawnPoints
)
{

    public ZmMapSpawnPoints()
    {
        this(new ArrayList<>(), new ArrayList<>());
    }


    public void addLobbySpawnPoint(Location location) { lobbySpawnPoints.add(location); }
    public void addGameSpawnPoint(Location location) { gameSpawnPoints.add(location); }

}
