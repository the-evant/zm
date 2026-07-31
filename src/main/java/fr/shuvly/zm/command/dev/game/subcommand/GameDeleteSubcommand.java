package fr.shuvly.zm.command.dev.game.subcommand;

import fr.shuvly.core.common.command.subcommand.AbstractSubcommand;
import fr.shuvly.core.common.exception.InvalidCommandContextException;
import fr.shuvly.zm.Zm;
import fr.shuvly.zm.command.dev.game.GameCommand;
import fr.shuvly.zm.game.Game;
import fr.shuvly.zm.game.GameManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static fr.shuvly.core.common.constant.TextParser.parse;

public class GameDeleteSubcommand
    extends AbstractSubcommand
{

    private static final Zm MAIN = Zm.getInstance();


    public GameDeleteSubcommand(GameCommand parent)
        throws InvalidCommandContextException
    {
        super(parent, "delete", "Deletes a game.", "zm.dev.game.delete");
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

        gameManager.destroyGame(game.getId())
            .thenAccept(_ -> {
                player.sendMessage(parse("<green>The game has been destroyed."));
            })
            .exceptionally(ex -> {
                if (ex instanceof NullPointerException) {
                    player.sendMessage(parse("<red>This game does not exist."));
                    return null;
                }

                player.sendMessage(parse("<red>An error occurred while destroying the game. Check the console."));
                MAIN.getLogger().severe("Failed to delete game: " + ex.getMessage());
                ex.printStackTrace();
                return null;
            });
    }

}
