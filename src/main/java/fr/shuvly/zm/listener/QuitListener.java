package fr.shuvly.zm.listener;

import fr.shuvly.zm.Zm;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener
    implements Listener
{

    private static final Zm MAIN = Zm.getInstance();


    @EventHandler
    public void onQuit(PlayerQuitEvent event)
    {
        MAIN.getGameManager().removePlayer(event.getPlayer());
    }

}
