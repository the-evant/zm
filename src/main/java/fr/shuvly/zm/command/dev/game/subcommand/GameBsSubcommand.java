package fr.shuvly.zm.command.dev.game.subcommand;

import fr.shuvly.core.common.command.subcommand.AbstractSubcommand;
import fr.shuvly.core.common.exception.InvalidCommandContextException;
import fr.shuvly.zm.Zm;
import fr.shuvly.zm.command.dev.game.GameCommand;
import fr.shuvly.zm.game.Game;
import fr.shuvly.zm.game.GameManager;
import fr.shuvly.zm.game.GameState;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static fr.shuvly.core.common.constant.TextParser.parse;

public class GameBsSubcommand
    extends AbstractSubcommand
{

    private static final Zm MAIN = Zm.getInstance();


    public GameBsSubcommand(GameCommand parent)
        throws InvalidCommandContextException
    {
        super(parent, "bs", "Executes a bootstrap to go faster (create, join, start)", "zm.dev.game.bs");
    }


    @Override
    public void execute(
        @NotNull Player player,
        @NotNull String[] args
    )
    {
        player.performCommand("game create zm_dev");
        player.performCommand("game join 1-zm_dev");
        player.performCommand("game start");
    }

}
