package fr.shuvly.zm.weapon.behavior.gun;

import fr.shuvly.paper.maditem.MadItem;
import fr.shuvly.paper.maditem.MadSkull;
import fr.shuvly.zm.Zm;
import fr.shuvly.zm.component.interaction.InteractionType;
import fr.shuvly.zm.perk.ZmPerkType;
import fr.shuvly.zm.player.ZmPlayer;
import fr.shuvly.zm.weapon.ZmWeapon;
import fr.shuvly.zm.weapon.ZmWeaponCategory;
import fr.shuvly.zm.weapon.ZmWeaponFactory;
import fr.shuvly.zm.weapon.ZmWeaponItemTemplate;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class AbstractGunBehavior
    extends ZmWeapon
    implements ZmWeaponFactory
{

    private static final Zm MAIN = Zm.getInstance();

    private GunStats baseStats;

    private int currentClip;
    private int currentReserve;
    private long lastFireTimeMs = 0;
    private boolean isReloading = false;
    private boolean isBursting = false;


    protected AbstractGunBehavior(
        String id,
        ConfigurationSection config,
        GunStats baseStats
    )
    {
        super(id, ZmWeaponCategory.PRIMARY, config);
        this.baseStats = baseStats;
        this.currentClip = baseStats.clipSize();
        this.currentReserve = baseStats.maxReserve();
    }

    protected AbstractGunBehavior(AbstractGunBehavior prototype)
    {
        super(prototype);

        this.baseStats = prototype.baseStats;
        this.currentClip = prototype.baseStats.clipSize();
        this.currentReserve = prototype.baseStats.maxReserve();
    }


    protected abstract void executeShot(ZmPlayer player);
    protected abstract String formatSpecificLore(String line);


    @Override
    public void onInteract(ZmPlayer zmPlayer, InteractionType type)
    {
        if (isReloading || isBursting || type != InteractionType.RIGHT_CLICK) {
            return;
        }

        final long now = System.currentTimeMillis();

        if (now - lastFireTimeMs < (baseStats.fireRateTicks() * 50L)) {
            return;
        }

        final Player player = zmPlayer.getPlayer();

        if (currentClip <= 0) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 2f);
            return;
        }

        if (baseStats.fireMode() == GunFireMode.BURST) {
            triggerBurstFire(zmPlayer, now);
        } else {
            triggerSingleFire(zmPlayer, now);
        }

        if (currentClip <= 0) {
            onReload(zmPlayer);
        }
    }

    private void triggerSingleFire(ZmPlayer zmPlayer, long time)
    {
        executeShot(zmPlayer);
        currentClip--;
        lastFireTimeMs = time;
        zmPlayer.getInventory().syncBukkitInventoryWeapon(this);
    }

    private void triggerBurstFire(ZmPlayer zmPlayer, long time)
    {
        Player player = zmPlayer.getPlayer();
        isBursting = true;
        lastFireTimeMs = time;

        final long effectiveBurstDelay = getEffectiveBurstDelayTicks(zmPlayer);
        final int[] shotsFired = {0}; // array is bc lambda requires final variables, lil hack

        player.getScheduler().runAtFixedRate(MAIN, task -> {
            if (!player.isOnline() || currentClip <= 0 || shotsFired[0] >= baseStats.burstShots()) {
                isBursting = false;
                task.cancel();
                return;
            }

            executeShot(zmPlayer);
            currentClip--;
            zmPlayer.getInventory().syncBukkitInventoryWeapon(this);
            shotsFired[0]++;
        }, null, 1L, effectiveBurstDelay);
    }

    @Override
    public void onReload(ZmPlayer player)
    {
        if (isReloading) {
            return;
        }

        final int needed = baseStats.clipSize() - currentClip;
        if (needed <= 0 || currentReserve <= 0) {
            return;
        }

        isReloading = true;

        final int amountToReload = Math.min(needed, currentReserve);
        final long effectiveReloadTicks = getEffectiveReloadTicks(player);

        player.getPlayer().getScheduler().runDelayed(MAIN, task -> {
            if (!player.getPlayer().isOnline() || !isReloading) {
                return;
            }

            isReloading = false;
            currentClip += amountToReload;
            currentReserve -= amountToReload;
            player.getInventory().syncBukkitInventoryWeapon(this);

        }, null, effectiveReloadTicks);
    }

    @Override
    protected ItemStack buildItemStack(ZmPlayer owner)
    {
        final ZmWeaponItemTemplate template = super.getItemTemplate();

        final List<String> formattedLore = template.lore().stream()
            .map(line ->
                line.replace("{clip}", String.valueOf(currentClip))
                    .replace("{reserve}", String.valueOf(currentReserve))
                    .replace("{clip_size}", String.valueOf(baseStats.clipSize()))
                    .replace("{max_reserve}", String.valueOf(baseStats.maxReserve()))
                    .replace("{min_damage}", String.valueOf(baseStats.minDamage()))
                    .replace("{max_damage}", String.valueOf(baseStats.maxDamage()))
                    .replace("{fire_rate}", String.valueOf(getEffectiveFireRateTicks(owner)))
                    .replace("{reload_ticks}", String.valueOf(getEffectiveReloadTicks(owner)))
            )
            .map(this::formatSpecificLore)
            .toList();

        final MadItem item;

        if (template.base64texture() == null || template.base64texture().isEmpty()) {
            item = new MadItem(template.material());
        } else {
            item = new MadSkull().setTexture(template.base64texture());
        }

        item.setName(template.displayName() + " (" + currentClip + "/" + currentReserve + ")")
            .setLore(formattedLore);

        item.setUnbreakable(true);

        return item.build().getItemStack();
    }

    @Override
    public void refillAmmo()
    {
        this.isReloading = false;
        this.currentClip = baseStats.clipSize();
        this.currentReserve = baseStats.maxReserve();
    }


    protected long getEffectiveFireRateTicks(ZmPlayer player)
    {
        long ticks = baseStats.fireRateTicks();
        if (player.hasPerk(ZmPerkType.DOUBLE_TAP)) {
            ticks = Math.max(1, ticks / 2);
        }
        return ticks;
    }

    protected long getEffectiveBurstDelayTicks(ZmPlayer player)
    {
        long ticks = baseStats.burstDelayTicks();
        if (player.hasPerk(ZmPerkType.DOUBLE_TAP)) {
            ticks = Math.max(1, ticks / 2);
        }
        return ticks;
    }

    protected long getEffectiveReloadTicks(ZmPlayer player)
    {
        long ticks = baseStats.reloadTicks();
        if (player.hasPerk(ZmPerkType.SPEED_COLA)) {
            ticks = Math.max(1, ticks / 2);
        }
        return ticks;
    }


    public GunStats getBaseStats() { return baseStats; }
    public boolean isReloading() { return isReloading; }
    public void setReloadStatus(boolean isReloading) { this.isReloading = isReloading; }

}
