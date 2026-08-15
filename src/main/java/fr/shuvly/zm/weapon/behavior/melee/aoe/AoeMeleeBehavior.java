package fr.shuvly.zm.weapon.behavior.melee.aoe;

import fr.shuvly.zm.weapon.ZmWeapon;
import fr.shuvly.zm.weapon.behavior.melee.AbstractMeleeBehavior;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class AoeMeleeBehavior
    extends AbstractMeleeBehavior
{

    private final AoeMeleeStats stats;


    public AoeMeleeBehavior(
        String id,
        ConfigurationSection config
    )
    {
        this(
            id,
            config,
            AoeMeleeStats.fromConfig(config.getConfigurationSection("stats"))
        );
    }

    private AoeMeleeBehavior(
        String id,
        ConfigurationSection config,
        AoeMeleeStats stats
    )
    {
        super(id, config, stats.base());
        this.stats = stats;
    }

    private AoeMeleeBehavior(AoeMeleeBehavior prototype)
    {
        super(prototype);
        this.stats = prototype.stats;
    }


    @Override
    public ZmWeapon duplicate()
    {
        return new AoeMeleeBehavior(this);
    }

    @Override
    protected void executeMelee(Player player)
    {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 0.5f);
        player.getWorld().spawnParticle(Particle.EXPLOSION, player.getLocation(), 5);

        for (Entity nearby : player.getWorld().getNearbyEntities(player.getLocation(), stats.aoeRadius(), stats.aoeRadius(), stats.aoeRadius())) {
            if (nearby instanceof LivingEntity target && target != player) {
                target.damage(stats.base().damage(), player);
            }
        }
    }

    @Override
    protected String formatSpecificLore(String line)
    {
        return line.replace("{aoe_radius}", String.valueOf(stats.aoeRadius()));
    }

    @Override
    public ZmWeapon create(
        String id,
        ConfigurationSection config
    )
    {
        return new AoeMeleeBehavior(id, config);
    }

}
