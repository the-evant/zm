package fr.shuvly.zm.listener;

import fr.shuvly.zm.Zm;
import fr.shuvly.zm.component.interaction.InteractionType;
import fr.shuvly.zm.game.Game;
import fr.shuvly.zm.player.ZmPlayer;
import fr.shuvly.zm.weapon.ZmWeapon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

import static fr.shuvly.core.common.constant.TextParser.parse;

public class WeaponInteractionListener
    implements Listener
{

    private static final Zm MAIN = Zm.getInstance();


    @EventHandler
    public void onEntityHit(EntityDamageByEntityEvent event)
    {
        if (!(event.getDamager() instanceof Player player)) {
            return;
        }

        final UUID weaponUuid = getWeaponUuid(player.getInventory().getItemInMainHand());
        if (weaponUuid == null) {
            return;
        }

        event.setCancelled(true);

        final ZmPlayer zmPlayer = getZmPlayer(player);
        if (zmPlayer == null) {
            return;
        }

        final ZmWeapon weapon = zmPlayer.getInventory().getWeaponByUuid(weaponUuid);
        if (weapon == null) {
            return;
        }

        weapon.onInteract(zmPlayer, InteractionType.LEFT_CLICK);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onReload(PlayerDropItemEvent event)
    {
        final UUID weaponUuid = getWeaponUuid(event.getItemDrop().getItemStack());
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
    public void onFire(PlayerInteractEvent event)
    {
        final UUID weaponUuid = getWeaponUuid(event.getItem());
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

        final InteractionType type =
            (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
                ? InteractionType.LEFT_CLICK
                : InteractionType.RIGHT_CLICK;

        weapon.onInteract(zmPlayer, type);
    }

    /**
     * Extracts the weapon UUID from the ItemStack's PDC. Returns null if invalid or not a ZM weapon.
     */
    private UUID getWeaponUuid(ItemStack item)
    {
        if (item == null || !item.hasItemMeta()) {
            return null;
        }

        final String uuidStr = item.getItemMeta().getPersistentDataContainer().get(ZmWeapon.INSTANCE_UUID_KEY, PersistentDataType.STRING);
        return uuidStr == null ? null : UUID.fromString(uuidStr);
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
