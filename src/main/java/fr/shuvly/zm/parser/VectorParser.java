package fr.shuvly.zm.parser;

import fr.shuvly.zm.exception.MapParseException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

public class VectorParser
{

    private VectorParser() {}


    public static Vector parseVector(ConfigurationSection section)
        throws MapParseException
    {
        if (section == null) {
            throw new MapParseException("Section does not exist.");
        }

        return new Vector(
            section.getDouble("x", 0),
            section.getDouble("y", 0),
            section.getDouble("z", 0)
        );
    }

    public static Vector3f parseVector3f(ConfigurationSection section)
        throws MapParseException
    {
        if (section == null) {
            throw new MapParseException("Section does not exist.");
        }

        return new Vector3f(
            (float) section.getDouble("x", 0),
            (float) section.getDouble("y", 0),
            (float) section.getDouble("z", 0)
        );
    }

}
