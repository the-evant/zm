package fr.shuvly.zm.player;

import fr.shuvly.zm.perk.ZmPerk;
import fr.shuvly.zm.perk.ZmPerkType;
import fr.shuvly.zm.player.inventory.ZmInventory;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class ZmPlayer
{

    private final Player player;
    private ZmPlayerState state;

    private ZmInventory inventory;
    private Set<ZmPerk> perks = new HashSet<>();
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

    public void addPerk(ZmPerk perk) { this.perks.add(perk); }
    public void removePerk(ZmPerk perk) { this.perks.remove(perk); }
    public Set<ZmPerk> getPerks() { return perks; }
    public boolean hasPerk(String id) { return perks.stream().anyMatch(perk -> perk.getType().getId().equals(id)); }
    public boolean hasPerk(ZmPerkType perkType) { return perks.stream().anyMatch(perk -> perk.getType() == perkType); }

}
