package fr.shuvly.zm.command.dev.game.subcommand;

import fr.shuvly.core.common.command.subcommand.AbstractSubcommand;
import fr.shuvly.core.common.exception.InvalidCommandContextException;
import fr.shuvly.zm.Zm;
import fr.shuvly.zm.command.dev.game.GameCommand;
import fr.shuvly.zm.exception.MapParseException;
import fr.shuvly.zm.game.Game;
import fr.shuvly.zm.game.GameManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static fr.shuvly.core.common.constant.TextParser.parse;

public class GameJoinSubcommand
    extends AbstractSubcommand
{

    private static final Zm MAIN = Zm.getInstance();


    public GameJoinSubcommand(GameCommand parent)
        throws InvalidCommandContextException
    {
        super(parent, "join", "Joins a game.", "zm.dev.game.join");
    }


    @Override
    public void execute(
        @NotNull Player player,
        @NotNull String[] args
    )
    {
        if (args.length != 1) {
            player.sendMessage(parse("<red>Please provide game ID."));
            return;
        }

        final String gameId = args[0];
        final GameManager gameManager = MAIN.getGameManager();
        final Game game = gameManager.getGame(gameId);

        if (game == null) {
            player.sendMessage(parse("<red>Could not find game with ID '" + gameId + "'."));
            return;
        }

        if (game.hasPlayer(player)) {
            player.sendMessage(parse("<red>You are already in the game."));
            return;
        }

        gameManager.addPlayer(player, gameId);
        player.sendMessage(parse("<green>Successfully joined game '" + gameId + "'."));
    }

}
