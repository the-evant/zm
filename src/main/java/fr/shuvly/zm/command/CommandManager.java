package fr.shuvly.zm.command;

import fr.shuvly.core.common.command.AbstractCommandManager;
import fr.shuvly.zm.command.dev.game.GameCommand;
import fr.shuvly.zm.command.dev.zone.ZoneCommand;
import org.bukkit.plugin.PluginManager;

public class CommandManager
    extends AbstractCommandManager
{

    public CommandManager(PluginManager pluginManager)
    {
        super(pluginManager);
    }


    @Override
    protected void registerCommands(PluginManager pluginManager)
    {
        super.registerCommand("zone", ZoneCommand.class);
        super.registerCommand("game", GameCommand.class);
    }

}
