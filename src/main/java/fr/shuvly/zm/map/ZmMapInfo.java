package fr.shuvly.zm.map;

public record ZmMapInfo(
    String id,          // "nacht"
    String displayName, // "Nacht der Untoten"
    String worldName,   // "zm_nacht" ; automatically resolved
    String configPath   // "plugins/maps/zm_nacht/zm_nacht.yml"
)
{

    public ZmMapInfo
    {
        if (!worldName.equals("zm_" + id)) {
            throw new IllegalArgumentException("worldName must exactly match 'zm_' + id");
        }
    }

    public ZmMapInfo(String name, String displayName, String configPath)
    {
        this(name, displayName, "zm_" + name, configPath);
    }

}
