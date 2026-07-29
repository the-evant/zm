package fr.shuvly.zm.command.dev.game.subcommand;

import fr.shuvly.core.common.command.subcommand.AbstractSubcommand;
import fr.shuvly.core.common.exception.InvalidCommandContextException;
import fr.shuvly.zm.Zm;
import fr.shuvly.zm.command.dev.game.GameCommand;
import fr.shuvly.zm.map.ZmMapInfo;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static fr.shuvly.core.common.constant.TextParser.parse;

public class GameMaplistSubcommand
    extends AbstractSubcommand
{

    private static final Zm MAIN = Zm.getInstance();


    public GameMaplistSubcommand(GameCommand parent)
        throws InvalidCommandContextException
    {
        super(parent, "maplist", "Displays all available maps.", "zm.dev.game.maplist");
    }


    @Override
    public void execute(
        @NotNull Player player,
        @NotNull String[] args
    )
    {
        final List<ZmMapInfo> mapsInfo = MAIN.getMapManager().getAvailableMaps();
        final StringBuilder sb = new StringBuilder();

        sb.append("Available Maps:");

        if (mapsInfo.isEmpty()) {
            sb.append(" None.");
        } else {
            sb.append("\n");

            for (final ZmMapInfo mapInfo : mapsInfo) {
                sb.append(String.format("- %s (zm_%s): '%s'\n", mapInfo.displayName(), mapInfo.name(), mapInfo.configPath()));
            }
        }

        player.sendMessage(parse(sb.toString()));
    }

}
