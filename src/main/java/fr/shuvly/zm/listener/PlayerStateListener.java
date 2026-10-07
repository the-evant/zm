package fr.shuvly.zm.listener;

import fr.shuvly.zm.Zm;
import fr.shuvly.zm.game.Game;
import fr.shuvly.zm.game.GameState;
import fr.shuvly.zm.player.ZmPlayer;
import fr.shuvly.zm.player.ZmPlayerState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

public class PlayerStateListener
    implements Listener
{

    private static final Zm MAIN = Zm.getInstance();


    /**
     * Lethal damage puts the player down instead of killing them.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event)
    {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        final Game game = MAIN.getGameManager().getPlayerGame(player);
        if (game == null) {
            return;
        }

        if (game.getState() == GameState.GAME_OVER) {
            event.setCancelled(true);
            return;
        }

        if (game.getState() != GameState.PLAYING) {
            return;
        }

        final ZmPlayer zmPlayer = game.getZmPlayer(player);
        if (zmPlayer == null) {
            return;
        }

        if (zmPlayer.getState() != ZmPlayerState.ALIVE) {
            event.setCancelled(true);
            return;
        }

        if (event.getFinalDamage() < player.getHealth()) {
            return;
        }

        event.setCancelled(true);
        game.getPlayerStateManager().down(zmPlayer);
    }

    /**
     * Downed players are seated on their corpse, sneaking must not get them up.
     */
    @EventHandler
    public void onDismount(EntityDismountEvent event)
    {
        if (event.getEntity() instanceof Player player && hasState(player, ZmPlayerState.DOWN)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onHeldItemChange(PlayerItemHeldEvent event)
    {
        if (hasState(event.getPlayer(), ZmPlayerState.DOWN)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event)
    {
        if (hasState(event.getPlayer(), ZmPlayerState.DOWN)) {
            event.setCancelled(true);
        }
    }


    private boolean hasState(Player player, ZmPlayerState state)
    {
        final Game game = MAIN.getGameManager().getPlayerGame(player);

        if (game == null) {
            return false;
        }

        final ZmPlayer zmPlayer = game.getZmPlayer(player);

        return zmPlayer != null && zmPlayer.getState() == state;
    }

}
