package fr.shuvly.zm.component;

import fr.shuvly.zm.map.MapParsingContext;
import org.bukkit.configuration.ConfigurationSection;

@FunctionalInterface
public interface ComponentFactory<T extends BaseComponent>
{

    T parse(String id, ConfigurationSection config, MapParsingContext context);

}
