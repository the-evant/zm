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
            player.sendMessage(parse("<red>Please provide map id."));
            return;
        }

        final GameManager gameManager = MAIN.getGameManager();
        final String mapName = args[0];

        try {
            player.sendMessage(parse("<yellow>Creating game with map '" + mapName + "'..."));

            gameManager.createGame(mapName)
                .thenAccept(createdGame -> {
                    player.sendMessage(parse("<green>The game (" + createdGame.getId() + ") has been successfully created."));
                })
                .exceptionally(ex -> {
                    MAIN.getLogger().severe("Failed to generate world for game: " + ex.getMessage());
                    ex.printStackTrace();
                    return null;
                });
        } catch (MapParseException exception) {
            player.sendMessage(parse("<red>There was an error while creating game with map '" + mapName + "': " + exception.getMessage()));
        }
    }

}
