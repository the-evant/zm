package fr.shuvly.zm.listener;

import fr.shuvly.zm.Zm;
import fr.shuvly.zm.component.interaction.InteractionType;
import fr.shuvly.zm.game.Game;
import fr.shuvly.zm.player.ZmPlayer;
import fr.shuvly.zm.weapon.ZmWeapon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static fr.shuvly.core.common.constant.TextParser.parse;

public class WeaponInteractionListener
    implements Listener
{

    private static final Zm MAIN = Zm.getInstance();

    private final Map<UUID, Long> lastInteraction = new HashMap<>();


    @EventHandler
    public void onEntityHit(EntityDamageByEntityEvent event)
    {
        if (!(event.getDamager() instanceof Player player)) {
            return;
        }

        final UUID weaponUuid = ZmWeapon.getWeaponUuid(player.getInventory().getItemInMainHand());
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

    @EventHandler
    public void onFire(PlayerInteractEvent event)
    {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        // dumbass fix for 1/100000 special cases bro (like right clicking on grass with a HOE)
        final Player player = event.getPlayer();
        final long currentTime = System.currentTimeMillis();

        if (lastInteraction.containsKey(player.getUniqueId()) && currentTime - lastInteraction.get(player.getUniqueId()) < 50) {
            return;
        }
        lastInteraction.put(player.getUniqueId(), currentTime);
        // ====================================================================================

        final UUID weaponUuid = ZmWeapon.getWeaponUuid(event.getItem());
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
     * Fetches the ZmPlayer safely, returning null if the player is not in an active game.
     */
    private ZmPlayer getZmPlayer(Player player)
    {
        final Game game = MAIN.getGameManager().getPlayerGame(player);
        return game != null ? game.getZmPlayer(player) : null;
    }

}
