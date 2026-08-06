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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class WeaponInteractionListener
    implements Listener
{

    private static final Zm MAIN = Zm.getInstance();


    @EventHandler
    public void onFire(PlayerInteractEvent event)
    {
        final ItemStack item = event.getItem();

        if (item == null || !item.hasItemMeta()) {
            return;
        }

        final ItemMeta meta = item.getItemMeta();

        if (!meta.getPersistentDataContainer().has(ZmWeapon.INSTANCE_UUID_KEY, PersistentDataType.STRING)) {
            return;
        }

        event.setCancelled(true);

        final Player player = event.getPlayer();
        final Game game = MAIN.getGameManager().getPlayerGame(player);

        if (game == null) {
            return;
        }

        final ZmPlayer zmPlayer = game.getZmPlayer(player);

        final String uuidStr = meta.getPersistentDataContainer().get(ZmWeapon.INSTANCE_UUID_KEY, PersistentDataType.STRING);
        final UUID instanceUuid = UUID.fromString(uuidStr);

        final ZmWeapon usedWeapon = zmPlayer.getInventory().getWeaponByUuid(instanceUuid);

        if (usedWeapon == null) { // wtf
            return;
        }

        final InteractionType type =
            (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
                ? InteractionType.LEFT_CLICK
                : InteractionType.RIGHT_CLICK;

        usedWeapon.onInteract(zmPlayer, type);
    }

}
