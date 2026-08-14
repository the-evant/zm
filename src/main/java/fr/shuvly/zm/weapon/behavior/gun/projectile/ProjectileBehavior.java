package fr.shuvly.zm.weapon.behavior.gun.projectile;

import fr.shuvly.zm.weapon.ZmWeapon;
import fr.shuvly.zm.weapon.behavior.gun.AbstractGunBehavior;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.persistence.PersistentDataType;

public class ProjectileBehavior
    extends AbstractGunBehavior
{

    public static final NamespacedKey PROJECTILE_DATA_KEY = new NamespacedKey("zm", "projectile_data");

    private final ProjectileStats stats;


    public ProjectileBehavior(String id, boolean isInMysteryBox, ConfigurationSection config)
    {
        this(id, isInMysteryBox, config, ProjectileStats.fromConfig(config.getConfigurationSection("stats")));
    }

    private ProjectileBehavior(String id, boolean isInMysteryBox, ConfigurationSection config, ProjectileStats stats)
    {
        super(id, isInMysteryBox, config, stats.base());
        this.stats = stats;
    }

    private ProjectileBehavior(ProjectileBehavior prototype)
    {
        super(prototype);
        this.stats = prototype.stats;
    }


    @Override
    public ZmWeapon duplicate() { return new ProjectileBehavior(this); }

    @Override
    protected void executeShot(Player player)
    {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 1.0f);

        final Snowball projectile = player.launchProjectile(Snowball.class);
        projectile.setVelocity(player.getLocation().getDirection().multiply(stats.projectileSpeed()));

//        projectile.setItem(new ItemStack(org.bukkit.Material.AIR));

        final String payload = stats.base().maxDamage() + ";" + stats.base().minDamage() + ";" + stats.aoeRadius();

        projectile.getPersistentDataContainer().set(PROJECTILE_DATA_KEY, PersistentDataType.STRING, payload);
    }

    @Override
    protected String formatSpecificLore(String line)
    {
        return line
            .replace("{projectile_speed}", String.valueOf(stats.projectileSpeed()))
            .replace("{aoe_radius}", String.valueOf(stats.aoeRadius()));
    }

    @Override
    public ZmWeapon create(String id, boolean isInMysteryBox, ConfigurationSection config)
    {
        return new ProjectileBehavior(id, isInMysteryBox, config);
    }

}
