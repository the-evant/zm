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

public class GameStartSubcommand
    extends AbstractSubcommand
{

    private static final Zm MAIN = Zm.getInstance();


    public GameStartSubcommand(GameCommand parent)
        throws InvalidCommandContextException
    {
        super(parent, "start", "Starts the game.", "zm.dev.game.start");
    }


    @Override
    public void execute(
        @NotNull Player player,
        @NotNull String[] args
    )
    {
        final GameManager gameManager = MAIN.getGameManager();
        final Game game = gameManager.getPlayerGame(player);

        if (game == null) {
            player.sendMessage(parse("<red>You are not in a game."));
            return;
        }

        if (game.getState() != GameState.WAITING_FOR_PLAYERS) {
            player.sendMessage(parse("<red>Your game cannot be started."));
            return;
        }

        game.start();

        player.sendMessage(parse("<green>The game has been started."));
    }

}
