package fr.shuvly.zm.weapon.behavior.throwable.timed_explosive;

import fr.shuvly.zm.weapon.ZmWeapon;
import fr.shuvly.zm.weapon.ZmWeaponCategory;
import fr.shuvly.zm.weapon.behavior.throwable.AbstractThrowableBehavior;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

public class TimedExplosiveBehavior
    extends AbstractThrowableBehavior
{

    private final TimedExplosiveStats stats;


    public TimedExplosiveBehavior(
        String id,
        ConfigurationSection config
    )
    {
        this(
            id,
            config,
            TimedExplosiveStats.fromConfig(config.getConfigurationSection("stats"))
        );
    }

    private TimedExplosiveBehavior(
        String id,
        ConfigurationSection config,
        TimedExplosiveStats stats
    )
    {
        super(id, ZmWeaponCategory.LETHAL, config, stats.base());
        this.stats = stats;
    }

    private TimedExplosiveBehavior(TimedExplosiveBehavior prototype)
    {
        super(prototype);
        this.stats = prototype.stats;
    }


    @Override
    public ZmWeapon duplicate() { return new TimedExplosiveBehavior(this); }

    @Override
    protected void executeThrow(Player player)
    {
        ItemStack thrownItem = super.buildItemStack();
        thrownItem.setAmount(1);

        Item grenade = player.getWorld().dropItem(player.getEyeLocation(), thrownItem);

        grenade.setVelocity(player.getLocation().getDirection().multiply(1.2));

        grenade.setPickupDelay(Integer.MAX_VALUE);
        grenade.setCanMobPickup(false);

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_SNOWBALL_THROW, 1.0f, 0.5f);

        grenade.getScheduler().runDelayed(MAIN, task -> {
            if (!grenade.isValid()) {
                return;
            }

            grenade.getWorld().spawnParticle(Particle.EXPLOSION, grenade.getLocation(), 3);
            grenade.getWorld().playSound(grenade.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 1.0f);

            for (Entity nearby : grenade.getWorld().getNearbyEntities(grenade.getLocation(), stats.explosionRadius(), stats.explosionRadius(), stats.explosionRadius())) {
                if (nearby instanceof LivingEntity target) {
                    target.damage(stats.damage(), player);
                }
            }

            grenade.remove();

        }, null, stats.fuseTicks());
    }

    @Override
    protected String formatSpecificLore(String line)
    {
        return line
            .replace("{fuse_ticks}", String.valueOf(stats.fuseTicks()))
            .replace("{radius}", String.valueOf(stats.explosionRadius()))
            .replace("{damage}", String.valueOf(stats.damage()));
    }

    @Override
    public ZmWeapon create(
        String id,
        ConfigurationSection config
    )
    {
        return new TimedExplosiveBehavior(id, config);
    }

}
