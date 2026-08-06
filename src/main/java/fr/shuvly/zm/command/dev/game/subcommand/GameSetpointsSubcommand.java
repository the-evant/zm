package fr.shuvly.zm.command.dev.game.subcommand;

import fr.shuvly.core.common.command.subcommand.AbstractSubcommand;
import fr.shuvly.core.common.exception.InvalidCommandContextException;
import fr.shuvly.zm.Zm;
import fr.shuvly.zm.command.dev.game.GameCommand;
import fr.shuvly.zm.game.Game;
import fr.shuvly.zm.game.GameManager;
import fr.shuvly.zm.player.ZmPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static fr.shuvly.core.common.constant.TextParser.parse;

public class GameSetpointsSubcommand
    extends AbstractSubcommand
{

    private static final Zm MAIN = Zm.getInstance();


    public GameSetpointsSubcommand(GameCommand parent)
        throws InvalidCommandContextException
    {
        super(parent, "setpoints", "Sets a player's points.", "zm.dev.game.setpoints");
    }


    @Override
    public void execute(
        @NotNull Player player,
        @NotNull String[] args
    )
    {
        if (args.length < 1 || args.length > 2) {
            player.sendMessage(parse("<red>Usage: /game setpoints [player] <points>"));
            return;
        }

        Player target = player;
        String pointsStr = args[0];

        if (args.length == 2) {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(parse("<red>Player not found."));
                return;
            }
            pointsStr = args[1];
        }

        int points;
        try {
            points = Integer.parseInt(pointsStr);
        } catch (NumberFormatException e) {
            player.sendMessage(parse("<red>Invalid points amount."));
            return;
        }

        final GameManager gameManager = MAIN.getGameManager();
        final Game game = gameManager.getPlayerGame(target);

        if (game == null) {
            player.sendMessage(parse("<red>That player is not in a game."));
            return;
        }

        final ZmPlayer zmPlayer = game.getZmPlayer(target);
        if (zmPlayer == null) {
            player.sendMessage(parse("<red>That player is not in the game."));
            return;
        }

        zmPlayer.setPoints(points);
        player.sendMessage(parse("<green>Successfully set points of " + target.getName() + " to " + points + "."));
    }

}
