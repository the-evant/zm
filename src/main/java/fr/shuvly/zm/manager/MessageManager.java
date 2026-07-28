package fr.shuvly.zm.manager;

import fr.shuvly.core.common.message.AbstractMessageManager;
import fr.shuvly.core.common.rank.Rank;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static fr.shuvly.core.common.constant.TextParser.parse;

public class MessageManager
    extends AbstractMessageManager
{

    @Override
    public @Nullable String getJoinMessage(
        @NotNull Player player,
        @NotNull Rank playerRank
    )
    {
        return "<green>+</green> " + playerRank.getShortName() + " " + playerRank.getUsernameColor() + player.getName();
    }

    @Override
    public @Nullable String getQuitMessage(
        @NotNull Player player,
        @NotNull Rank playerRank
    )
    {
        return "<red>-</red> " + playerRank.getShortName() + " " + playerRank.getUsernameColor() + player.getName();
    }

    @Override
    public @NotNull Component getChatFormat(
        @NotNull Player player,
        @NotNull Rank playerRank,
        @NotNull Component message
    )
    {
        return parse(playerRank.getFullName() + " " + playerRank.getUsernameColor() + player.getName() + "<white> <sprite:gui:world_list/join> ")
            .append(message);
    }

}
