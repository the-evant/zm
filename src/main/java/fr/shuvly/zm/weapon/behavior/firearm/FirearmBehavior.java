package fr.shuvly.zm.weapon.behavior.firearm;

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
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static fr.shuvly.core.common.constant.TextParser.parse;

public class FirearmBehavior
    extends ZmWeapon
    implements ZmWeaponFactory
{

    private static final Zm MAIN = Zm.getInstance();

    private final FirearmStats stats;

    private int currentClip;
    private int currentReserve;
    private long lastFireTimeMs;
    private boolean isReloading;


    public FirearmBehavior(String id, boolean isInMysteryBox, ConfigurationSection config)
    {
        super(id, ZmWeaponCategory.PRIMARY, isInMysteryBox, config);

        this.stats = FirearmStats.fromConfig(config.getConfigurationSection("stats"));

        this.currentClip = stats.clipSize();
        this.currentReserve = stats.maxReserve();
        this.lastFireTimeMs = 0;
        this.isReloading = false;
    }

    private FirearmBehavior(FirearmBehavior prototype)
    {
        super(prototype);

        this.stats = prototype.stats;

        this.currentClip = prototype.stats.clipSize();
        this.currentReserve = prototype.stats.maxReserve();
        this.lastFireTimeMs = 0;
        this.isReloading = false;
    }


    @Override
    public ZmWeapon duplicate()
    {
        return new FirearmBehavior(this);
    }

    @Override
    public void onInteract(ZmPlayer zmPlayer, InteractionType type)
    {
        if (isReloading || type != InteractionType.RIGHT_CLICK) {
            return;
        }

        final long now = System.currentTimeMillis();

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
            return stats.maxDamage();
        }

        if (distance >= stats.dropoffEnd()) {
            return stats.minDamage();
        }

        double progress = (distance - stats.dropoffStart()) / (stats.dropoffEnd() - stats.dropoffStart());

        return stats.maxDamage() - (progress * (stats.maxDamage() - stats.minDamage()));
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
    public ItemStack buildItemStack()
    {
        final ZmWeaponItem template = super.getItemTemplate();

        final List<String> formattedLore = template.lore().stream()
            .map(line ->
                line.replace("{clip}", String.valueOf(currentClip))
                    .replace("{reserve}", String.valueOf(currentReserve))
                    .replace("{clip_size}", String.valueOf(stats.clipSize()))
                    .replace("{max_reserve}", String.valueOf(stats.maxReserve()))
                    .replace("{pellet_count}", String.valueOf(stats.pelletCount()))
                    .replace("{spread}", String.valueOf(stats.spread()))
                    .replace("{min_damage}", String.valueOf(stats.minDamage()))
                    .replace("{max_damage}", String.valueOf(stats.maxDamage()))
                    .replace("{dropoff_start}", String.valueOf(stats.dropoffStart()))
                    .replace("{dropoff_end}", String.valueOf(stats.dropoffEnd()))
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
        return new FirearmBehavior(id, isInMysteryBox, config);
    }

    @Override
    public String getType() { return "FIREARM"; }

}
