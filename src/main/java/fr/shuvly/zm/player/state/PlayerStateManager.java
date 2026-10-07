package fr.shuvly.zm.player.state;

import fr.shuvly.zm.game.Game;
import fr.shuvly.zm.game.GameState;
import fr.shuvly.zm.perk.ZmPerkType;
import fr.shuvly.zm.player.ZmPlayer;
import fr.shuvly.zm.player.ZmPlayerState;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.*;

import static fr.shuvly.core.common.constant.TextParser.parse;

/**
 * Handles transitions between player states inside a game (alive -> down [-> dead] -> alive).
 */
public class PlayerStateManager
{

    private static final int PROGRESS_BAR_LENGTH = 30;

    private final Game game;
    private final PlayerStateSettings settings;

    private final Map<UUID, DownedPlayer> downedPlayers = new HashMap<>();
    private final Map<UUID, Integer> lastReviveInputTick = new HashMap<>();
    private final Map<UUID, GameMode> gameModesBeforeDeath = new HashMap<>();


    public PlayerStateManager(Game game, PlayerStateSettings settings)
    {
        this.game = game;
        this.settings = settings;
    }


    // ===== State transitions =====

    /**
     * Puts an alive player on the ground, waiting for a revive.
     */
    public void down(ZmPlayer zmPlayer)
    {
        if (zmPlayer.getState() != ZmPlayerState.ALIVE || game.getState() != GameState.PLAYING) {
            return;
        }

        final Player player = zmPlayer.getPlayer();

        zmPlayer.setState(ZmPlayerState.DOWN);

        final DownedPlayer downed = new DownedPlayer(zmPlayer, settings.bleedOutTicks());

        downed.spawn();
        downedPlayers.put(player.getUniqueId(), downed);

        player.showTitle(Title.title(
            parse("<red><b>DOWNED"),
            parse("<gray>Wait for a teammate to revive you"),
            Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(2), Duration.ofMillis(500))
        ));
        broadcast("<red>" + player.getName() + " is down!");

        game.checkGameOver();
    }

    private void revive(DownedPlayer downed)
    {
        final ZmPlayer zmPlayer = downed.getZmPlayer();
        final ZmPlayer reviver = downed.getReviver();
        final Player player = zmPlayer.getPlayer();

        downedPlayers.remove(player.getUniqueId());

        zmPlayer.setState(ZmPlayerState.ALIVE);
        downed.despawn();

        player.teleport(downed.getLocation());
        healFully(player);
        player.clearTitle();
        player.sendActionBar(Component.empty());

        if (reviver != null) {
            reviver.getPlayer().sendActionBar(Component.empty());
            broadcast("<green>" + player.getName() + " has been revived by " + reviver.getPlayer().getName() + ".");
        }
    }

    private void bleedOut(DownedPlayer downed)
    {
        final ZmPlayer zmPlayer = downed.getZmPlayer();
        final Player player = zmPlayer.getPlayer();

        downedPlayers.remove(player.getUniqueId());

        zmPlayer.setState(ZmPlayerState.DEAD);
        downed.despawn();

        zmPlayer.clearPerks();
        zmPlayer.getInventory().clear();
        player.getInventory().clear();

        gameModesBeforeDeath.put(player.getUniqueId(), player.getGameMode());
        player.setGameMode(GameMode.SPECTATOR);
        cycleSpectatorTarget(zmPlayer);

        player.showTitle(Title.title(
            parse("<dark_red><b>YOU DIED"),
            parse("<gray>You will respawn next round"),
            Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(3), Duration.ofMillis(500))
        ));
        broadcast("<dark_red>" + player.getName() + " has bled out.");

        game.checkGameOver();
    }

    /**
     * Brings every dead player back into the game, with a fresh state. Called when a round passes.
     */
    public void respawnDeadPlayers()
    {
        for (ZmPlayer zmPlayer : game.getPlayers()) {
            if (zmPlayer.getState() != ZmPlayerState.DEAD) {
                continue;
            }

            final Player player = zmPlayer.getPlayer();

            zmPlayer.setState(ZmPlayerState.ALIVE);
            player.setGameMode(gameModesBeforeDeath.getOrDefault(player.getUniqueId(), GameMode.ADVENTURE));
            gameModesBeforeDeath.remove(player.getUniqueId());

            player.teleportAsync(game.getMap().getSpawnPoints().getNextGameSpawnPoint());
            healFully(player);
            player.sendActionBar(Component.empty());

            player.sendMessage(parse("<green>You have respawned."));
        }
    }

    /**
     * Restores a player's normal state when they leave the game (quit, game destroyed...).
     */
    public void release(ZmPlayer zmPlayer)
    {
        final Player player = zmPlayer.getPlayer();
        final UUID uuid = player.getUniqueId();
        final ZmPlayerState previousState = zmPlayer.getState();

        for (DownedPlayer downed : downedPlayers.values()) {
            if (downed.getReviver() == zmPlayer) {
                downed.cancelRevive();
            }
        }

        zmPlayer.setState(ZmPlayerState.ALIVE);

        final DownedPlayer downed = downedPlayers.remove(uuid);

        if (downed != null) {
            cancelRevive(downed);
            downed.despawn();
        }

        if (previousState == ZmPlayerState.DEAD) {
            player.setGameMode(gameModesBeforeDeath.getOrDefault(uuid, GameMode.ADVENTURE));
        }

        gameModesBeforeDeath.remove(uuid);
        lastReviveInputTick.remove(uuid);

        player.sendActionBar(Component.empty());
    }


    // ===== Revive =====

    /**
     * Called when a player presses the interaction key.
     *
     * @return  true if the input was used to revive someone, meaning it should not trigger anything else.
     */
    public boolean handleReviveInput(ZmPlayer zmPlayer)
    {
        if (game.getState() != GameState.PLAYING || zmPlayer.getState() != ZmPlayerState.ALIVE) {
            return false;
        }

        if (!isReviving(zmPlayer) && findRevivable(zmPlayer) == null) {
            return false;
        }

        lastReviveInputTick.put(zmPlayer.getPlayer().getUniqueId(), Bukkit.getCurrentTick());
        return true;
    }

    public boolean isReviving(ZmPlayer zmPlayer)
    {
        return downedPlayers.values().stream().anyMatch(downed -> downed.getReviver() == zmPlayer);
    }

    /**
     * @return The prompt to display to a player standing next to a downed teammate, or null if there is none.
     */
    public String getRevivePrompt(ZmPlayer zmPlayer)
    {
        if (zmPlayer.getState() != ZmPlayerState.ALIVE || isReviving(zmPlayer)) {
            return null;
        }

        final DownedPlayer target = findRevivable(zmPlayer);

        if (target == null) {
            return null;
        }

        return "Hold [<key:key.swapOffhand>] to revive " + target.getZmPlayer().getPlayer().getName();
    }

    private void startRevive(ZmPlayer reviver, DownedPlayer downed)
    {
        final int ticks = reviver.hasPerk(ZmPerkType.QUICK_REVIVE)
            ? settings.quickReviveTicks()
            : settings.reviveTicks();

        downed.startRevive(reviver, ticks);
    }

    private void cancelRevive(DownedPlayer downed)
    {
        final ZmPlayer reviver = downed.getReviver();
        if (reviver != null) {
            reviver.getPlayer().sendActionBar(Component.empty());
        }
        downed.cancelRevive();
    }

    private boolean canKeepReviving(ZmPlayer reviver, DownedPlayer downed)
    {
        return reviver.getState() == ZmPlayerState.ALIVE
            && reviver.getPlayer().isOnline()
            && isInReviveRange(reviver, downed)
            && isHoldingReviveInput(reviver);
    }

    private boolean isHoldingReviveInput(ZmPlayer zmPlayer)
    {
        final Player player = zmPlayer.getPlayer();

        if (settings.allowSneakToRevive() && player.isSneaking()) {
            return true;
        }

        final Integer lastInput = lastReviveInputTick.get(player.getUniqueId());
        return lastInput != null && Bukkit.getCurrentTick() - lastInput <= settings.reviveInputGraceTicks();
    }

    /**
     * @return The closest downed player that is not already being revived, in range of the given player.
     */
    private DownedPlayer findRevivable(ZmPlayer zmPlayer)
    {
        DownedPlayer closest = null;
        double closestDistance = Double.MAX_VALUE;

        for (DownedPlayer downed : downedPlayers.values()) {
            if (downed.getReviver() != null || !isInReviveRange(zmPlayer, downed)) {
                continue;
            }

            final double distance = zmPlayer.getPlayer().getLocation().distanceSquared(downed.getLocation());
            if (distance < closestDistance) {
                closest = downed;
                closestDistance = distance;
            }
        }

        return closest;
    }

    private boolean isInReviveRange(ZmPlayer zmPlayer, DownedPlayer downed)
    {
        final Player player = zmPlayer.getPlayer();

        if (!player.getWorld().equals(downed.getLocation().getWorld())) {
            return false;
        }

        final double range = settings.reviveRange();
        return player.getLocation().distanceSquared(downed.getLocation()) <= range * range;
    }


    // ===== Spectating =====

    public boolean isValidSpectatorTarget(Entity entity)
    {
        if (!(entity instanceof Player target)) {
            return false;
        }

        final ZmPlayer zmTarget = game.getZmPlayer(target);
        return zmTarget != null
            && (zmTarget.getState() == ZmPlayerState.ALIVE || zmTarget.getState() == ZmPlayerState.DOWN);
    }

    /**
     * Makes a dead player spectate the next player still in the game.
     */
    public void cycleSpectatorTarget(ZmPlayer spectator)
    {
        final Player player = spectator.getPlayer();

        if (spectator.getState() != ZmPlayerState.DEAD || player.getGameMode() != GameMode.SPECTATOR) {
            return;
        }

        final List<Player> targets = game.getPlayers().stream()
            .map(ZmPlayer::getPlayer)
            .filter(this::isValidSpectatorTarget)
            .sorted(Comparator.comparing(Player::getName))
            .toList();

        if (targets.isEmpty()) {
            return;
        }

        final int currentIndex = targets.indexOf(player.getSpectatorTarget());
        final Player next = targets.get((currentIndex + 1) % targets.size());

        player.setSpectatorTarget(next);
    }


    // ===== Ticking =====

    public void tick()
    {
        if (game.getState() != GameState.PLAYING) {
            return;
        }

        tickDownedPlayers();
        tickNewRevives();

        if (Bukkit.getCurrentTick() % 20 == 0) {
            tickSpectators();
        }
    }

    private void tickDownedPlayers()
    {
        for (DownedPlayer downed : List.copyOf(downedPlayers.values())) {
            final ZmPlayer reviver = downed.getReviver();

            if (reviver != null) {
                if (!canKeepReviving(reviver, downed)) {
                    cancelRevive(downed);
                } else {
                    downed.tickRevive();

                    if (downed.isReviveComplete()) {
                        revive(downed);
                        continue;
                    }
                }
            }

            if (downed.getReviver() == null) {
                downed.tickBleedOut();

                if (downed.hasBledOut()) {
                    bleedOut(downed);
                    continue;
                }
            }

            displayDownedHud(downed);
        }
    }

    private void tickNewRevives()
    {
        for (ZmPlayer zmPlayer : game.getAlivePlayers()) {
            if (isReviving(zmPlayer) || !isHoldingReviveInput(zmPlayer)) {
                continue;
            }

            final DownedPlayer target = findRevivable(zmPlayer);

            if (target != null) {
                startRevive(zmPlayer, target);
                zmPlayer.getPlayer().playSound(zmPlayer.getPlayer(), Sound.ITEM_ARMOR_EQUIP_LEATHER, 1f, 1f);
            }
        }
    }

    private void tickSpectators()
    {
        for (ZmPlayer zmPlayer : game.getPlayers()) {
            if (zmPlayer.getState() != ZmPlayerState.DEAD) {
                continue;
            }

            final Player player = zmPlayer.getPlayer();

            if (!isValidSpectatorTarget(player.getSpectatorTarget())) {
                cycleSpectatorTarget(zmPlayer);
            }

            final Entity target = player.getSpectatorTarget();
            final String targetName = target != null ? target.getName() : "nobody";

            player.sendActionBar(parse(
                "<gray>Spectating <white>" + targetName + "</white> · Respawning next round · [<key:key.sneak>] to switch"
            ));
        }
    }

    private void displayDownedHud(DownedPlayer downed)
    {
        final Player player = downed.getZmPlayer().getPlayer();
        final ZmPlayer reviver = downed.getReviver();

        if (reviver != null) {
            final String bar = progressBar(downed.getReviveProgress());

            player.sendActionBar(parse("<green>Being revived by " + reviver.getPlayer().getName() + " " + bar));
            reviver.getPlayer().sendActionBar(parse("<green>Reviving " + player.getName() + " " + bar));
            downed.updateCorpseDescription("<green>Reviving...");
        } else {
            final int secondsLeft = downed.getBleedOutSecondsLeft();

            player.sendActionBar(parse("<red>Bleeding out... <white>" + secondsLeft + "s"));
            downed.updateCorpseDescription("<red>Downed · " + secondsLeft + "s");
        }
    }


    // ===== Utils =====

    private static String progressBar(double progress)
    {
        final int filled = (int) Math.round(Math.clamp(progress, 0, 1) * PROGRESS_BAR_LENGTH);

        return "<green>" + "|".repeat(filled) + "<dark_gray>" + "|".repeat(PROGRESS_BAR_LENGTH - filled);
    }

    private static void healFully(Player player)
    {
        final AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);

        player.setHealth(maxHealth != null ? maxHealth.getValue() : 20.0);
        player.setFoodLevel(20);
        player.setFireTicks(0);
    }

    private void broadcast(String message)
    {
        for (ZmPlayer zmPlayer : game.getPlayers()) {
            zmPlayer.getPlayer().sendMessage(parse(message));
        }
    }

    public PlayerStateSettings getSettings() { return settings; }

}
