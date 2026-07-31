package fr.shuvly.zm.map;

import fr.shuvly.zm.Zm;
import fr.shuvly.zm.exception.MapParseException;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

public class MapManager
{

    private static final Zm MAIN = Zm.getInstance();
    private static final Logger LOGGER = MAIN.getLogger();

    private final World lobby = Bukkit.getWorld("world");
    private final Map<String, ZmMapInfo> availableMaps =  new HashMap<>();


    /**
     * Scans the maps/ folder and parses every map found in its subfolder.
     * If the map is successfully parsed without errors, it is considered valid.
     */
    public void loadAvailableMaps()
    {
        this.availableMaps.clear();
        final File mapsFolder = new File(Zm.getInstance().getDataFolder(), "maps");

        if (!mapsFolder.exists()) {
            mapsFolder.mkdirs();
            return;
        }

        final File[] subFolders = mapsFolder.listFiles(File::isDirectory);

        if (subFolders == null) {
            return;
        }

        for (File folder : subFolders) {
            final String mapName = folder.getName();
            final File mapFile = new File(folder, mapName + ".yml");

            if (!mapFile.exists()) {
                continue;
            }

            final File worldFolder = new File(folder, "map");
            if (!worldFolder.exists() || !worldFolder.isDirectory()) {
                LOGGER.warning("Skipping map '" + mapName + "': Missing 'map' directory.");
                continue;
            }

            try {
                final ZmMapInfo info = ZmMapParser.parseInfo(mapFile);

                this.availableMaps.put(info.worldName(), info);

                LOGGER.info("Successfully validated map: " + info.displayName() + " (" + info.worldName() + ")");
            } catch (MapParseException exception) {
                LOGGER.warning("Skipping invalid map '" + mapFile.getName() + "': " + exception.getMessage());
            } catch (Exception exception) {
                LOGGER.severe("Unexpected error while parsing map '" + mapFile.getName() + "': " + exception.getMessage());
            }
        }
    }


    public World getLobby() { return lobby; }
    public ZmMapInfo getMapInfo(String mapName) { return this.availableMaps.get(mapName); }
    public List<ZmMapInfo> getAvailableMaps()
    {
        return availableMaps.values().stream().toList();
    }

}
