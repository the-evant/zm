package fr.shuvly.zm.component.mystery_box;

import fr.shuvly.zm.component.ComponentFactory;
import fr.shuvly.zm.component.interaction.InteractionTrigger;
import fr.shuvly.zm.component.interaction.TriggerParser;
import fr.shuvly.zm.exception.MapParseException;
import fr.shuvly.zm.map.MapParsingContext;
import fr.shuvly.zm.parser.VectorParser;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

public class MysteryBoxComponentParser
    implements ComponentFactory<MysteryBoxComponent>
{

    @Override
    public MysteryBoxComponent parse(
        String id,
        ConfigurationSection config,
        MapParsingContext context
    )
    {
        final int cost = config.getInt("cost", 950);

        final ConfigurationSection triggerSec = config.getConfigurationSection("trigger");
        if (triggerSec == null) {
            throw new MapParseException("Mystery Box '" + id + "' is missing the 'trigger' section.");
        }
        final InteractionTrigger trigger = TriggerParser.parse(triggerSec);

        final ConfigurationSection locSec = config.getConfigurationSection("weapon_location");
        if (locSec == null) {
            throw new MapParseException("Mystery Box '" + id + "' is missing 'weapon_location'.");
        }
        final Vector locVec = VectorParser.parseVector(locSec);
        final Location weaponLocation = new Location(context.world(), locVec.getX(), locVec.getY(), locVec.getZ());

        final ConfigurationSection dirSec = config.getConfigurationSection("direction");
        final Vector direction = dirSec != null ? VectorParser.parseVector(dirSec) : new Vector(0, 0, 1);

        return new MysteryBoxComponent(
            id,
            trigger,
            cost,
            weaponLocation,
            direction,
            context.weaponRegistry()
        );
    }

}
