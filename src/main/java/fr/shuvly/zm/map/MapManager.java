package fr.shuvly.zm.map;

import fr.shuvly.zm.Zm;
import fr.shuvly.zm.exception.MapParseException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapManager
{

    private final List<ZmMapInfo> availableMaps;

    private final World lobby = Bukkit.getWorld("world");

    public MapManager()
    {
        this.availableMaps = new ArrayList<>();
    }


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

            try {
                final ZmMap parsedMap = ZmMapParser.parse(mapFile);
                final ZmMapInfo info = parsedMap.getInfo();

                this.availableMaps.add(info);

                Zm.getInstance().getLogger().info("Successfully validated map: " + info.displayName() + " (zm_" + info.name() + ")");
            } catch (MapParseException exception) {
                Zm.getInstance().getLogger().warning("Skipping invalid map '" + mapFile.getName() + "': " + exception.getMessage());
            } catch (Exception exception) {
                Zm.getInstance().getLogger().severe("Unexpected error while parsing map '" + mapFile.getName() + "': " + exception.getMessage());
            }
        }
    }


    public World getLobby() { return lobby; }
    public List<ZmMapInfo> getAvailableMaps()
    {
        return Collections.unmodifiableList(availableMaps);
    }

}
