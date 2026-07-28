package fr.shuvly.zm.command.dev.zone;

import fr.shuvly.core.common.command.AbstractCommand;
import fr.shuvly.core.common.exception.InvalidCommandContextException;
import fr.shuvly.zm.command.dev.zone.subcommand.ZoneHereSubcommand;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ZoneCommand
    extends AbstractCommand
{

    public ZoneCommand()
    {
        super("zm.dev.zone", true);

        try {
            super.getSubcommandManager()
                .registerSubcommand(new ZoneHereSubcommand(this));
        } catch (InvalidCommandContextException _) {
            // don't care
        }
    }


    @Override
    public void execute(
        @NotNull Player player,
        @NotNull Command command,
        @NotNull String label,
        @NotNull String[] args
    )
    {
        // help?
    }

}
