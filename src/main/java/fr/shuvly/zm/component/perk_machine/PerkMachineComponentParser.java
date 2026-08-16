package fr.shuvly.zm.component.perk_machine;

import fr.shuvly.zm.component.ComponentFactory;
import fr.shuvly.zm.component.interaction.InteractionTrigger;
import fr.shuvly.zm.component.interaction.TriggerParser;
import fr.shuvly.zm.exception.MapParseException;
import fr.shuvly.zm.map.MapParsingContext;
import fr.shuvly.zm.perk.ZmPerk;
import org.bukkit.configuration.ConfigurationSection;

public class PerkMachineComponentParser
    implements ComponentFactory<PerkMachineComponent>
{

    @Override
    public PerkMachineComponent parse(
        String id,
        ConfigurationSection config,
        MapParsingContext context
    )
    {
        final String perkId = config.getString("perk_id");
        final int cost = config.getInt("cost", 2500);
        final ConfigurationSection triggerSec = config.getConfigurationSection("trigger");

        if (triggerSec == null) {
            throw new MapParseException("Perk Missing trigger");
        }

        final InteractionTrigger trigger = TriggerParser.parse(triggerSec);

        final ZmPerk perk = context.perkRegistry().getPerk(perkId);

        if (perk == null) {
            throw new IllegalArgumentException("Unknown perk_id: " + perkId);
        }

        return new PerkMachineComponent(id, trigger, perk, cost);
    }

}
