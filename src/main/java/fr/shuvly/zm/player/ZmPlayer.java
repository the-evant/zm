package fr.shuvly.zm.player;

import org.bukkit.entity.Player;

public class ZmPlayer
{

    private Player player;
    private ZmPlayerState state;


    public ZmPlayer(Player player)
    {
        this.player = player;
        this.state = ZmPlayerState.ALIVE;
    }


    public Player getPlayer() { return player; }
    public ZmPlayerState getState() { return state; }

}
