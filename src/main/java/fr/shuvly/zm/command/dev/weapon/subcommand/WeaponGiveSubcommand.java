package fr.shuvly.zm.command.dev.weapon.subcommand;

import fr.shuvly.core.common.command.AbstractCommand;
import fr.shuvly.core.common.command.subcommand.AbstractSubcommand;
import fr.shuvly.core.common.exception.InvalidCommandContextException;
import fr.shuvly.zm.Zm;
import fr.shuvly.zm.game.Game;
import fr.shuvly.zm.game.GameManager;
import fr.shuvly.zm.player.ZmPlayer;
import fr.shuvly.zm.weapon.ZmWeapon;
import fr.shuvly.zm.weapon.ZmWeaponRegistry;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static fr.shuvly.core.common.constant.TextParser.parse;

public class WeaponGiveSubcommand
    extends AbstractSubcommand
{

    private static final Zm MAIN = Zm.getInstance();


    public WeaponGiveSubcommand(AbstractCommand parent)
        throws InvalidCommandContextException
    {
        super(parent, "give", "Give a selected weapon.", "zm.dev.weapon.give");
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

        final String weaponId = args[0];

        if (!weaponRegistry.getRegisteredWeapons().containsKey(weaponId)) {
            player.sendMessage(parse("<red>This weapon does not exist. Weapon list can be found with /weapon list command."));
            return;
        }

        final ZmPlayer zmPlayer = game.getZmPlayer(player);
        final ZmWeapon weapon = weaponRegistry.getWeapon(weaponId);

        zmPlayer.getInventory().giveWeapon(weapon);

        player.sendMessage(parse("<green>You have been given</green> <white>" + weaponId + "</white><green>."));
    }

}
