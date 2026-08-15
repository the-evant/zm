package fr.shuvly.zm.component.window;

import fr.shuvly.zm.component.ComponentFactory;
import fr.shuvly.zm.map.MapParsingContext;
import org.bukkit.configuration.ConfigurationSection;

public class WindowComponentParser
    implements ComponentFactory<WindowComponent>
{

    @Override
    public WindowComponent parse(
        String id,
        ConfigurationSection config,
        MapParsingContext context
    )
    {
        return null;
    }

}
