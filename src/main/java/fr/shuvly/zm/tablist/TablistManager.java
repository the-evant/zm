package fr.shuvly.zm.tablist;

import fr.shuvly.core.common.rank.Rank;
import fr.shuvly.core.common.tablist.AbstractTablistManager;
import fr.shuvly.core.paper.PCore;
import org.bukkit.entity.Player;

public class TablistManager
    extends AbstractTablistManager
{

    private static final PCore CORE = PCore.getInstance();


    @Override
    protected int getSortPower(Player player, Rank playerRank)
    {
        return 0; // no order in zm
    }

    @Override
    protected String getTablistNameFormat(Player player, Rank playerRank)
    {
        String fullName = playerRank.getFullName();
        String color = playerRank.getUsernameColor();

        if (fullName == null) fullName = "";
        if (color == null) color = "";

        String prefix = fullName + (fullName.isEmpty() ? "" : " ") + color;
        return (prefix.isEmpty() ? "" : prefix) + player.getName();
    }

    @Override
    protected String getNametagPrefixFormat(Player player, Rank playerRank)
    {
        CORE.getLogger().info(playerRank.getShortName());

        String shortName = playerRank.getShortName();
        String color = playerRank.getUsernameColor();

        if (shortName == null) shortName = "";
        if (color == null) color = "";

        final String prefix = shortName + (shortName.isEmpty() ? "" : " ") + color;
        return prefix.trim();
    }

    @Override
    protected String getHeader(Player player, Rank playerRank)
    {
        return "<b><black>zm</black></b>\n";
    }

    @Override
    protected String getFooter(Player player, Rank playerRank)
    {
        return "\nthx 4 wachting";
    }

}
