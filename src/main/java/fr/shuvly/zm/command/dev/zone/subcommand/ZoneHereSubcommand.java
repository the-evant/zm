package fr.shuvly.zm.command.dev.zone.subcommand;

import fr.shuvly.core.common.command.subcommand.AbstractSubcommand;
import fr.shuvly.core.common.exception.InvalidCommandContextException;
import fr.shuvly.zm.Zm;
import fr.shuvly.zm.command.dev.zone.ZoneCommand;
import fr.shuvly.zm.map.Zone;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static fr.shuvly.core.common.constant.TextParser.parse;

public class ZoneHereSubcommand
    extends AbstractSubcommand
{

    private static final Zm MAIN = Zm.getInstance();


    public ZoneHereSubcommand(ZoneCommand parent)
        throws InvalidCommandContextException
    {
        super(parent, "here", "Displays whether the player is in a given zone.", "zm.dev.zone.here");
    }


    @Override
    public void execute(
        @NotNull Player player,
        @NotNull String[] args
    )
    {
        final Zone zoneAtPlayerLocation = MAIN.getGameManager().getMap().getZoneAt(player.getLocation());

        if (zoneAtPlayerLocation == null) {
            player.sendMessage(parse("<red>You are not in a zone."));
            return;
        }

        player.sendMessage(parse("You are in zone " + zoneAtPlayerLocation.getId()));
    }

}
