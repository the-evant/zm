package fr.shuvly.zm.player.state;

/**
 * Timings and rules of the down / revive / bleed out system.
 * All durations are in milliseconds, converted to ticks at runtime.
 *
 * @param   bleedOutDuration    Time a downed player has before dying.
 * @param   reviveDuration      Time a reviver must hold the revive input.
 * @param   quickReviveDuration Same as {@link #reviveDuration}, when the reviver has Quick Revive.
 * @param   reviveInputGrace    Max time between two F presses for the input to still count as "held".
 *                              Must be longer than the OS key-repeat delay.
 * @param   reviveRange         Max distance between a reviver and a corpse.
 * @param   allowSneakToRevive  Whether holding sneak also counts as holding the revive input.
 */
public record PlayerStateSettings(
    int bleedOutDuration,
    int reviveDuration,
    int quickReviveDuration,
    int reviveInputGrace,
    double reviveRange,
    boolean allowSneakToRevive
)
{

    public static PlayerStateSettings base()
    {
        return new PlayerStateSettings(
            20000,
            10000,
            5000,
            600,
            2.0,
            true
        );
    }

    public int bleedOutTicks() { return bleedOutDuration / 50; }
    public int reviveTicks() { return reviveDuration / 50; }
    public int quickReviveTicks() { return quickReviveDuration / 50; }
    public int reviveInputGraceTicks() { return reviveInputGrace / 50; }

}
