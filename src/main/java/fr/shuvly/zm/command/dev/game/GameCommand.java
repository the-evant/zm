package fr.shuvly.zm.command.dev.game;

import fr.shuvly.core.common.command.AbstractCommand;
import fr.shuvly.core.common.exception.InvalidCommandContextException;
import fr.shuvly.zm.command.dev.game.subcommand.*;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GameCommand
    extends AbstractCommand
{

    public GameCommand()
    {
        super("zm.dev.game", true);

        try {
            super.getSubcommandManager()
                .registerSubcommand(new GameCreateSubcommand(this))
                .registerSubcommand(new GameDeleteSubcommand(this))
                .registerSubcommand(new GameJoinSubcommand(this))
                .registerSubcommand(new GameStartSubcommand(this))
                .registerSubcommand(new GameMaplistSubcommand(this))
                .registerSubcommand(new GameBsSubcommand(this));
        } catch (InvalidCommandContextException _) {
            // don't care lol
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
