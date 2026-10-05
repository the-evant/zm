package fr.shuvly.zm.command.dev.game.subcommand;

import fr.shuvly.core.common.command.subcommand.AbstractSubcommand;
import fr.shuvly.core.common.exception.InvalidCommandContextException;
import fr.shuvly.zm.Zm;
import fr.shuvly.zm.command.dev.game.GameCommand;
import fr.shuvly.zm.game.Game;
import fr.shuvly.zm.game.GameManager;
import fr.shuvly.zm.map.ZmMapInfo;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

import static fr.shuvly.core.common.constant.TextParser.parse;

public class GameListSubcommand
    extends AbstractSubcommand
{

    private static final Zm MAIN = Zm.getInstance();


    public GameListSubcommand(GameCommand parent)
        throws InvalidCommandContextException
    {
        super(parent, "list", "Displays all current running games.", "zm.dev.game.list");
    }


    @Override
    public void execute(
        @NotNull Player player,
        @NotNull String[] args
    )
    {
        final GameManager gameManager = MAIN.getGameManager();
        final Map<String, Game> runningGames = gameManager.getActiveGames();
        final StringBuilder sb = new StringBuilder();

        sb.append("Amount of games created so far: " + (gameManager.getCreatedGamesAmount() - 1) + "\n");
        sb.append("Running games:");

        if (runningGames.isEmpty()) {
            sb.append(" None.");
        } else {
            sb.append("\n");

            for (Map.Entry<String, Game> entry : runningGames.entrySet()) {
                final Game game = entry.getValue();
                sb.append("- ").append(entry.getKey()).append(":\n");

                sb.append("  - State: " + game.getState() + "\n");
                sb.append("  - Players: " + game.getAlivePlayers().size() + "/" + game.getPlayers().size() + "\n");
                sb.append("  - Map: " + game.getMap().getInfo().id() + "\n");
                sb.append("  - Round: " + game.getRoundManager().getCurrentRound() + "\n");

                sb.append("\n");
            }
        }

        player.sendMessage(parse(sb.toString()));
    }

}
