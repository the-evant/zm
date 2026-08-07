package fr.shuvly.zm.weapon.behavior.projectile;

import fr.shuvly.paper.maditem.MadItem;
import fr.shuvly.paper.maditem.MadSkull;
import fr.shuvly.zm.Zm;
import fr.shuvly.zm.component.interaction.InteractionType;
import fr.shuvly.zm.player.ZmPlayer;
import fr.shuvly.zm.weapon.ZmWeapon;
import fr.shuvly.zm.weapon.ZmWeaponCategory;
import fr.shuvly.zm.weapon.ZmWeaponFactory;
import fr.shuvly.zm.weapon.ZmWeaponItem;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class ProjectileBehavior
    extends ZmWeapon
    implements ZmWeaponFactory
{

    private static final Zm MAIN = Zm.getInstance();
    public static final NamespacedKey PROJECTILE_DATA_KEY = new NamespacedKey("zm", "projectile_data");

    private final ProjectileStats stats;

    private int currentClip;
    private int currentReserve;
    private long lastFireTimeMs;
    private boolean isReloading;


    protected ProjectileBehavior(String id, boolean isInMysteryBox, ConfigurationSection config)
    {
        super(id, ZmWeaponCategory.PRIMARY, isInMysteryBox, config);

        this.stats = ProjectileStats.fromConfig(config.getConfigurationSection("stats"));

        this.currentClip = stats.clipSize();
        this.currentReserve = stats.maxReserve();
        this.lastFireTimeMs = 0;
        this.isReloading = false;
    }

    protected ProjectileBehavior(ProjectileBehavior prototype)
    {
        super(prototype);

        this.stats = prototype.stats;

        this.currentClip = prototype.stats.clipSize();
        this.currentReserve = prototype.stats.maxReserve();
        this.lastFireTimeMs = 0;
        this.isReloading = false;
    }


    @Override
    public ZmWeapon duplicate() { return new ProjectileBehavior(this); }

    @Override
    public void onInteract(ZmPlayer zmPlayer, InteractionType type)
    {
        if (isReloading || type != InteractionType.RIGHT_CLICK) {
            return;
        }

        long now = System.currentTimeMillis();

        if (now - lastFireTimeMs < (stats.fireRateTicks() * 50L)) {
            return;
        }

        final Player player = zmPlayer.getPlayer();

        if (currentClip <= 0) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 2f);
            return;
        }

        executeShot(player);

        currentClip--;
        lastFireTimeMs = now;

        zmPlayer.getInventory().syncBukkitInventoryWeapon(this);
    }

    private void executeShot(Player player)
    {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 1.0f);

        final Snowball projectile = player.launchProjectile(Snowball.class);
        projectile.setVelocity(player.getLocation().getDirection().multiply(stats.projectileSpeed()));

        projectile.setItem(new ItemStack(org.bukkit.Material.AIR));

        final String payload = stats.maxDamage() + ";" + stats.minDamage() + ";" + stats.aoeRadius();

        projectile.getPersistentDataContainer().set(PROJECTILE_DATA_KEY, PersistentDataType.STRING, payload);
    }

    @Override
    public void onReload(ZmPlayer player)
    {
        if (isReloading) {
            return;
        }

        int needed = stats.clipSize() - currentClip;
        if (needed <= 0 || currentReserve <= 0) {
            return;
        }

        isReloading = true;
        int amountToReload = Math.min(needed, currentReserve);

        Bukkit.getScheduler().runTaskLater(MAIN, () -> {
            isReloading = false;
            currentClip += amountToReload;
            currentReserve -= amountToReload;
            player.getInventory().syncBukkitInventoryWeapon(this);
        }, stats.reloadTicks());
    }

    @Override
    protected ItemStack buildItemStack()
    {
        final ZmWeaponItem template = super.getItemTemplate();

        final List<String> formattedLore = template.lore().stream()
            .map(line ->
                line.replace("{clip}", String.valueOf(currentClip))
                .replace("{reserve}", String.valueOf(currentReserve))
                .replace("{clip_size}", String.valueOf(stats.clipSize()))
                .replace("{max_reserve}", String.valueOf(stats.maxReserve()))
                .replace("{projectile_speed}", String.valueOf(stats.projectileSpeed()))
                .replace("{aoe_radius}", String.valueOf(stats.aoeRadius()))
                .replace("{min_damage}", String.valueOf(stats.minDamage()))
                .replace("{max_damage}", String.valueOf(stats.maxDamage()))
                .replace("{fire_rate}", String.valueOf(stats.fireRateTicks()))
                .replace("{reload_ticks}", String.valueOf(stats.reloadTicks()))
            )
            .toList();

        final MadItem item;

        if (template.base64texture() == null || template.base64texture().isEmpty()) {
            item = new MadItem(template.material());
        } else {
            item = new MadSkull().setTexture(template.base64texture());
        }

        item.setName(template.displayName())
            .setLore(formattedLore);

        item.setUnbreakable(true);

        return item.build().getItemStack();
    }

    @Override
    public ZmWeapon create(String id, boolean isInMysteryBox, ConfigurationSection config)
    {
        return new ProjectileBehavior(id, isInMysteryBox, config);
    }

    @Override
    public String getType() { return "PROJECTILE"; }

}
