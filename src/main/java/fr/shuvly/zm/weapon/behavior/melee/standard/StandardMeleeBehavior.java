package fr.shuvly.zm.weapon.behavior.melee.standard;

import fr.shuvly.zm.weapon.ZmWeapon;
import fr.shuvly.zm.weapon.behavior.melee.AbstractMeleeBehavior;
import fr.shuvly.zm.weapon.behavior.melee.MeleeStats;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

public class StandardMeleeBehavior
    extends AbstractMeleeBehavior
{

    private final MeleeStats stats;


    public StandardMeleeBehavior(String id, ConfigurationSection config)
    {
        this(
            id,
            config,
            MeleeStats.fromConfig(config.getConfigurationSection("stats"))
        );
    }

    private StandardMeleeBehavior(
        String id,
        ConfigurationSection config,
        MeleeStats stats
    )
    {
        super(id, config, stats);
        this.stats = stats;
    }

    private StandardMeleeBehavior(StandardMeleeBehavior prototype)
    {
        super(prototype);
        this.stats = prototype.stats;
    }


    @Override
    public ZmWeapon duplicate()
    {
        return new StandardMeleeBehavior(this);
    }

    @Override
    protected void executeMelee(Player player)
    {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, 1.0f);

        final RayTraceResult result = player.getWorld().rayTraceEntities(
            player.getEyeLocation(),
            player.getEyeLocation().getDirection(),
            stats.range(),
            0.5,
            entity -> true //entity instanceof LivingEntity && entity != player
        );

        if (result != null && result.getHitEntity() instanceof LivingEntity target) {
            target.damage(stats.damage(), player);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.0f, 1.0f);
        }
    }

    @Override
    protected String formatSpecificLore(String line)
    {
        return line; // no specific tags
    }

    @Override
    public ZmWeapon create(
        String id,
        ConfigurationSection config
    )
    {
        return new StandardMeleeBehavior(id, config);
    }

}
