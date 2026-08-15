package fr.shuvly.zm.weapon.behavior.gun.firearm;

import fr.shuvly.zm.weapon.ZmWeapon;
import fr.shuvly.zm.weapon.behavior.gun.AbstractGunBehavior;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

public class FirearmBehavior
    extends AbstractGunBehavior
{

    private final FirearmStats stats;


    public FirearmBehavior(
        String id,
        ConfigurationSection config
    )
    {
        this(
            id,
            config,
            FirearmStats.fromConfig(config.getConfigurationSection("stats"))
        );
    }

    private FirearmBehavior(
        String id,
        ConfigurationSection config,
        FirearmStats stats
    )
    {
        super(id, config, stats.base());
        this.stats = stats;
    }

    private FirearmBehavior(FirearmBehavior prototype)
    {
        super(prototype);
        this.stats = prototype.stats;
    }


    @Override
    public ZmWeapon duplicate()
    {
        return new FirearmBehavior(this);
    }

    @Override
    protected void executeShot(Player player)
    {
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);

        final Location eyeLoc = player.getEyeLocation();
        final Vector baseDirection = eyeLoc.getDirection();

        for (int i = 0; i < stats.pelletCount(); i++) {
            final Vector shotDirection = baseDirection.clone();

            if (stats.spread() > 0) {
                double rx = (ThreadLocalRandom.current().nextDouble() - 0.5) * stats.spread();
                double ry = (ThreadLocalRandom.current().nextDouble() - 0.5) * stats.spread();
                double rz = (ThreadLocalRandom.current().nextDouble() - 0.5) * stats.spread();
                shotDirection.add(new Vector(rx, ry, rz)).normalize();
            }

            RayTraceResult result = player.getWorld().rayTraceEntities(
                eyeLoc, shotDirection, 100.0, 0.5, entity -> true //entity instanceof Zombie todo: lol
            );

            player.getWorld().spawnParticle(org.bukkit.Particle.CRIT, eyeLoc.clone().add(shotDirection.multiply(1.5)), 1);

            if (result != null && result.getHitEntity() instanceof LivingEntity target) {
                double distance = eyeLoc.distance(result.getHitPosition().toLocation(player.getWorld()));
                target.damage(calculateDamage(distance), player);
            }
        }
    }

    private double calculateDamage(double distance)
    {
        if (distance <= stats.dropoffStart()) {
            return stats.base().maxDamage();
        }

        if (distance >= stats.dropoffEnd()) {
            return stats.base().minDamage();
        }

        double progress = (distance - stats.dropoffStart()) / (stats.dropoffEnd() - stats.dropoffStart());

        return stats.base().maxDamage() - (progress * (stats.base().maxDamage() - stats.base().minDamage()));
    }

    @Override
    protected String formatSpecificLore(String line)
    {
        return line
            .replace("{pellet_count}", String.valueOf(stats.pelletCount()))
            .replace("{spread}", String.valueOf(stats.spread()))
            .replace("{dropoff_start}", String.valueOf(stats.dropoffStart()))
            .replace("{dropoff_end}", String.valueOf(stats.dropoffEnd()));
    }

    @Override
    public ZmWeapon create(
        String id,
        ConfigurationSection config
    )
    {
        return new FirearmBehavior(id, config);
    }

}
