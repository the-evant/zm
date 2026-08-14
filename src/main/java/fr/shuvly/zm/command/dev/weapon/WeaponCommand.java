package fr.shuvly.zm.command.dev.weapon;

import fr.shuvly.core.common.command.AbstractCommand;
import fr.shuvly.core.common.exception.InvalidCommandContextException;
import fr.shuvly.zm.command.dev.weapon.subcommand.WeaponGiveSubcommand;
import fr.shuvly.zm.command.dev.weapon.subcommand.WeaponListSubcommand;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WeaponCommand
    extends AbstractCommand
{

    public WeaponCommand()
    {
        super("zm.dev.weapon", true);

        try {
            super.getSubcommandManager()
                .registerSubcommand(new WeaponListSubcommand(this))
                .registerSubcommand(new WeaponGiveSubcommand(this));
        } catch (InvalidCommandContextException _) {
            // dont care...
        }
    }

    @Override
    public void execute(
        @NotNull Player player,
        @NotNull Command command,
        @NotNull String label,
        @NotNull String[] args
    )
    {
        // hmmm
    }
}
