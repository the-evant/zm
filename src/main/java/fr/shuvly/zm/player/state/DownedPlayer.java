package fr.shuvly.zm.player.state;

import fr.shuvly.zm.player.ZmPlayer;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import static fr.shuvly.core.common.constant.TextParser.parse;

/**
 * A player lying on the ground, waiting to be revived.
 * <p>
 * The player is made invisible and seated on an invisible armor stand sunk into the ground,
 * which prevents them from moving and lowers their camera. A mannequin wearing their skin
 * is spawned in a prone pose to act as the visible corpse.
 */
public class DownedPlayer
{

    private static final double CAMERA_DROP = .5;
    private static final int EMPTY_HOTBAR_SLOT = 8;
    private static final double GROUND_SEARCH_DISTANCE = 16.0;

    private final ZmPlayer zmPlayer;
    private final Location location;
    private final int previousHeldSlot;

    private Mannequin corpse;
    private ArmorStand seat;

    private int bleedOutTicksLeft;

    private ZmPlayer reviver;
    private int reviveTicksDone;
    private int reviveTicksRequired;


    public DownedPlayer(ZmPlayer zmPlayer, int bleedOutTicks)
    {
        this.zmPlayer = zmPlayer;
        this.location = findGround(zmPlayer.getPlayer().getLocation());
        this.previousHeldSlot = zmPlayer.getPlayer().getInventory().getHeldItemSlot();
        this.bleedOutTicksLeft = bleedOutTicks;
    }


    public void spawn()
    {
        final Player player = zmPlayer.getPlayer();
        final World world = location.getWorld();

        this.corpse = world.spawn(location, Mannequin.class, mannequin -> {
            mannequin.setProfile(ResolvableProfile.resolvableProfile(player.getPlayerProfile()));
            mannequin.setPose(Pose.SWIMMING, true);
            mannequin.setImmovable(true);
            mannequin.setInvulnerable(true);
            mannequin.setSilent(true);
            mannequin.setPersistent(false);
            mannequin.customName(player.name());
            mannequin.setCustomNameVisible(true);
        });

        this.seat = world.spawn(location.clone().subtract(0, CAMERA_DROP, 0), ArmorStand.class, stand -> {
            stand.setMarker(true);
            stand.setInvisible(true);
            stand.setGravity(false);
            stand.setInvulnerable(true);
            stand.setSilent(true);
            stand.setPersistent(false);
        });

        player.setFireTicks(0);
        player.setInvisible(true);
        player.getInventory().setHeldItemSlot(EMPTY_HOTBAR_SLOT);

        this.seat.addPassenger(player);
    }

    /**
     * Removes the corpse and frees the player.
     * The player's state must no longer be DOWN, otherwise the dismount gets canceled.
     */
    public void despawn()
    {
        final Player player = zmPlayer.getPlayer();

        player.leaveVehicle();
        player.setInvisible(false);
        player.getInventory().setHeldItemSlot(previousHeldSlot);

        if (seat != null) {
            seat.remove();
        }
        if (corpse != null) {
            corpse.remove();
        }
    }

    public void updateCorpseDescription(String text)
    {
        if (corpse != null) {
            corpse.setDescription(parse(text));
        }
    }


    // ===== Bleed out =====

    public void tickBleedOut() { bleedOutTicksLeft--; }
    public boolean hasBledOut() { return bleedOutTicksLeft <= 0; }
    public int getBleedOutSecondsLeft() { return (int) Math.ceil(bleedOutTicksLeft / 20.0); }


    // ===== Revive =====

    public void startRevive(ZmPlayer reviver, int ticksRequired)
    {
        this.reviver = reviver;
        this.reviveTicksDone = 0;
        this.reviveTicksRequired = Math.max(1, ticksRequired);
    }

    public void cancelRevive()
    {
        this.reviver = null;
        this.reviveTicksDone = 0;
    }

    public void tickRevive() { reviveTicksDone++; }
    public boolean isReviveComplete() { return reviver != null && reviveTicksDone >= reviveTicksRequired; }
    public double getReviveProgress() { return reviver == null ? 0 : (double) reviveTicksDone / reviveTicksRequired; }
    public ZmPlayer getReviver() { return reviver; }


    public ZmPlayer getZmPlayer() { return zmPlayer; }
    public Location getLocation() { return location.clone(); }


    private static Location findGround(Location from)
    {
        final RayTraceResult hit = from.getWorld().rayTraceBlocks(
            from.clone().add(0, 0.1, 0),
            new Vector(0, -1, 0),
            GROUND_SEARCH_DISTANCE,
            FluidCollisionMode.NEVER,
            true
        );

        final Location ground = hit == null
            ? from.clone()
            : hit.getHitPosition().toLocation(from.getWorld());

        ground.setYaw(from.getYaw());
        ground.setPitch(0);
        return ground;
    }

}
