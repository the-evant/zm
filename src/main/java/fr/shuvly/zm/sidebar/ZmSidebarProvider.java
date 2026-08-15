package fr.shuvly.zm.sidebar;

import fr.shuvly.core.common.sidebar.SidebarProvider;
import fr.shuvly.zm.Zm;
import fr.shuvly.zm.game.Game;
import fr.shuvly.zm.map.zone.Zone;
import fr.shuvly.zm.player.ZmPlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class ZmSidebarProvider
    implements SidebarProvider
{

    private static final Zm MAIN = Zm.getInstance();


    @Override
    public String getTitle(Player player)
    {
        return "<black><b>zombi casshhhhh</b></black>";
    }

    @Override
    public List<String> getLines(Player player)
    {
        final Game playerGame = MAIN.getGameManager().getPlayerGame(player);

        if (playerGame == null) {
            return List.of(
                "",
                "<gray>You are currently in",
                "<white><b>The Lobby</b></white>",
                ""
            );
        }

        final ZmPlayer zmPlayer = playerGame.getZmPlayer(player);

        if (zmPlayer == null) {
            return List.of("<red>Loading player data...</red>");
        }

        final Zone zone = playerGame.getMap().getZoneAt(player.getLocation());
        final String playerZone = zone != null ? zone.getId() : "None";

        return List.of(
            player.getName(),
            "",
            "Game: " + playerGame.getId(),
            "Zone: " + playerZone,
            "Points: " + zmPlayer.getPoints(),
            "State: " + zmPlayer.getState()
        );
    }

}
