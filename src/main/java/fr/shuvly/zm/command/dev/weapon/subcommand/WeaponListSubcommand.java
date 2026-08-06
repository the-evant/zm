package fr.shuvly.zm.command.dev.weapon.subcommand;

import fr.shuvly.core.common.command.AbstractCommand;
import fr.shuvly.core.common.command.subcommand.AbstractSubcommand;
import fr.shuvly.core.common.exception.InvalidCommandContextException;
import fr.shuvly.zm.Zm;
import fr.shuvly.zm.game.Game;
import fr.shuvly.zm.game.GameManager;
import fr.shuvly.zm.weapon.ZmWeapon;
import fr.shuvly.zm.weapon.ZmWeaponRegistry;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static fr.shuvly.core.common.constant.TextParser.parse;

public class WeaponListSubcommand
    extends AbstractSubcommand
{

    private static final Zm MAIN = Zm.getInstance();


    public WeaponListSubcommand(AbstractCommand parent)
        throws InvalidCommandContextException
    {
        super(parent, "list", "Displays every registered weapon.", "zm.dev.weapon.list");
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

        final ZmWeaponRegistry weaponRegistry = game.getMap().getWeaponRegistry();

        for (Map.Entry<String, ZmWeapon> weaponEntry : weaponRegistry.getRegisteredWeapons().entrySet()) {
            player.sendMessage(String.format("- %s: %s", weaponEntry.getKey(), weaponEntry.getValue()));
        }
    }

}
