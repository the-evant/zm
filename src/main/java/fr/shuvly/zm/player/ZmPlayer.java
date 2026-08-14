package fr.shuvly.zm.player;

import fr.shuvly.zm.player.inventory.ZmInventory;
import org.bukkit.entity.Player;

public class ZmPlayer
{

    private final Player player;
    private ZmPlayerState state;

    private ZmInventory inventory;
    private int points;


    public ZmPlayer(Player player)
    {
        this.player = player;
        this.state = ZmPlayerState.ALIVE;

        this.inventory = new ZmInventory(this);
        this.points = 1000000;
    }


    public Player getPlayer() { return player; }

    public void setState(ZmPlayerState state) { this.state = state; }
    public ZmPlayerState getState() { return state; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
    public void addPoints(int points) { this.points += points; }
    public boolean removePoints(int points)
    {
        if (points < this.points) {
            this.points -= points;
            return true;
        }
        return false;
    }

    public ZmInventory getInventory() { return inventory; }

}
