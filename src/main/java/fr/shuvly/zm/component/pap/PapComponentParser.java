package fr.shuvly.zm.component.pap;

import fr.shuvly.zm.component.ComponentFactory;
import fr.shuvly.zm.component.interaction.InteractionTrigger;
import fr.shuvly.zm.component.interaction.TriggerParser;
import fr.shuvly.zm.exception.MapParseException;
import fr.shuvly.zm.map.MapParsingContext;
import fr.shuvly.zm.parser.VectorParser;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

public class PapComponentParser
    implements ComponentFactory<PapComponent>
{

    @Override
    public PapComponent parse(
        String id,
        ConfigurationSection config,
        MapParsingContext context
    )
    {
        final int cost = config.getInt("cost", 5000);
        final int repapCost = config.getInt("repap_cost", 2500);
        final int upgradeTimeTicks = config.getInt("upgrade_time", 60);
        final int pickupTimeoutTicks = config.getInt("pickup_timeout", 300);

        final ConfigurationSection triggerSec = config.getConfigurationSection("trigger");
        if (triggerSec == null) {
            throw new MapParseException("Pack-a-Punch '" + id + "' is missing the 'trigger' section.");
        }

        final InteractionTrigger trigger = TriggerParser.parse(triggerSec);

        final ConfigurationSection compartmentSec = config.getConfigurationSection("weapon_compartment");
        if (compartmentSec == null) {
            throw new MapParseException("Pack-a-Punch '" + id + "' is missing 'weapon_compartment' coordinates.");
        }

        final Vector compVec = VectorParser.parseVector(compartmentSec);
        final Location weaponCompartment = new Location(
            context.world(),
            compVec.getX(),
            compVec.getY(),
            compVec.getZ()
        );

        final ConfigurationSection dirSec = config.getConfigurationSection("direction");
        final Vector direction = dirSec != null
            ? VectorParser.parseVector(dirSec)
            : new Vector(0, 0, 0);

        return new PapComponent(
            id,
            trigger,
            cost,
            repapCost,
            upgradeTimeTicks,
            pickupTimeoutTicks,
            weaponCompartment,
            direction,
            context.weaponRegistry()
        );
    }

}
