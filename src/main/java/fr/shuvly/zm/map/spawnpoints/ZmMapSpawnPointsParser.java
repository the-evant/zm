package fr.shuvly.zm.map.spawnpoints;

import fr.shuvly.zm.exception.MapParseException;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ZmMapSpawnPointsParser
{

    private ZmMapSpawnPointsParser() {}


    private static Location parseLocation(ConfigurationSection section, World world)
    {
        return new Location(
            world,
            section.getDouble("x"), section.getDouble("y"), section.getDouble("z"),
            (float) section.getDouble("pitch"), (float) section.getDouble("yaw")
        );
    }

    private static void parseLocations(
        ConfigurationSection section,
        List<Location> list,
        World world
    )
        throws MapParseException
    {
        final List<Map<?, ?>> partsList = section.getMapList("locations");

        for (Map<?, ?> locationMap : partsList) {
            final MemoryConfiguration tempConfig = new MemoryConfiguration();

            tempConfig.createSection("temp", locationMap);
            list.add(parseLocation(Objects.requireNonNull(tempConfig.getConfigurationSection("temp")), world));
        }
    }

    public static ZmMapSpawnPoints parse(ConfigurationSection section, World world)
        throws MapParseException
    {
        final ZmMapSpawnPoints spawnPoints = new ZmMapSpawnPoints();

        final ConfigurationSection lobbySection = section.getConfigurationSection("lobby");
        final ConfigurationSection gameSection = section.getConfigurationSection("game");

        if (gameSection == null) {
            throw new MapParseException("Missing 'game' section.");
        }

        if (lobbySection != null) {
            parseLocations(lobbySection, spawnPoints.lobbySpawnPoints(), world);
        }

        parseLocations(gameSection, spawnPoints.gameSpawnPoints(), world);

        return spawnPoints;
    }

}
