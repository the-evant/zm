package fr.shuvly.zm.listener;

import fr.shuvly.zm.Zm;
import fr.shuvly.zm.game.Game;
import fr.shuvly.zm.player.ZmPlayer;
import fr.shuvly.zm.weapon.ZmWeapon;
import fr.shuvly.zm.weapon.behavior.gun.AbstractGunBehavior;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class WeaponReloadListener
    implements Listener
{

    private static final Zm MAIN = Zm.getInstance();


    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onReload(PlayerDropItemEvent event)
    {
        final UUID weaponUuid = ZmWeapon.getWeaponUuid(event.getItemDrop().getItemStack());
        if (weaponUuid == null) {
            return;
        }

        event.setCancelled(true);

        final ZmPlayer zmPlayer = getZmPlayer(event.getPlayer());
        if (zmPlayer == null) {
            return;
        }

        final ZmWeapon weapon = zmPlayer.getInventory().getWeaponByUuid(weaponUuid);
        if (weapon == null) {
            return;
        }

        weapon.onReload(zmPlayer);
    }

    @EventHandler
    public void onSlotSwitch(PlayerInventorySlotChangeEvent event)
    {
        final ItemStack oldItem = event.getOldItemStack();
        final UUID weaponUuid = ZmWeapon.getWeaponUuid(oldItem);

        if (weaponUuid == null) {
            return;
        }

        final ZmPlayer zmPlayer = getZmPlayer(event.getPlayer());
        if (zmPlayer == null) {
            return;
        }

        final ZmWeapon weapon = zmPlayer.getInventory().getWeaponByUuid(weaponUuid);
        if (weapon == null) {
            return;
        }

        if (weapon instanceof AbstractGunBehavior gun) {
            if (gun.isReloading()) {
                gun.setReloadStatus(false);
            }
        }
    }

    /**
     * Fetches the ZmPlayer safely, returning null if the player is not in an active game.
     */
    private ZmPlayer getZmPlayer(Player player)
    {
        final Game game = MAIN.getGameManager().getPlayerGame(player);
        return game != null ? game.getZmPlayer(player) : null;
    }

}
