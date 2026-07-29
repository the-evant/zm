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

public class GameCreateSubcommand
    extends AbstractSubcommand
{

    private static final Zm MAIN = Zm.getInstance();


    public GameCreateSubcommand(GameCommand parent)
        throws InvalidCommandContextException
    {
        super(parent, "create", "Creates a new game.", "zm.dev.game.create");
    }


    @Override
    public void execute(
        @NotNull Player player,
        @NotNull String[] args
    )
    {
        if (args.length != 1) {
            player.sendMessage(parse("<red>Please provide map name."));
            return;
        }

        final String mapName = args[0];

        final GameManager gameManager = MAIN.getGameManager();
        final Game createdGame;

        try {
            createdGame = gameManager.createGame(mapName);
        } catch (MapParseException exception) {
            player.sendMessage(parse("<red>There have been an error while creating game with map '" + mapName + "': " + exception.getMessage()));
            return;
        }

        player.sendMessage(parse("<green>The game (" + createdGame.getId() + ") has been successfully created."));
    }

}
