package fr.shuvly.zm.weapon.behavior.gun;

import fr.shuvly.paper.maditem.MadItem;
import fr.shuvly.paper.maditem.MadSkull;
import fr.shuvly.zm.Zm;
import fr.shuvly.zm.component.interaction.InteractionType;
import fr.shuvly.zm.player.ZmPlayer;
import fr.shuvly.zm.weapon.ZmWeapon;
import fr.shuvly.zm.weapon.ZmWeaponCategory;
import fr.shuvly.zm.weapon.ZmWeaponFactory;
import fr.shuvly.zm.weapon.ZmWeaponItemTemplate;
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


    protected AbstractGunBehavior(String id, boolean isInMysteryBox, ConfigurationSection config, GunStats baseStats)
    {
        super(id, ZmWeaponCategory.PRIMARY, isInMysteryBox, config);
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


    protected abstract void executeShot(Player player);
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
//            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 2f);
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
        executeShot(zmPlayer.getPlayer());
        currentClip--;
        lastFireTimeMs = time;
        zmPlayer.getInventory().syncBukkitInventoryWeapon(this);
    }

    private void triggerBurstFire(ZmPlayer zmPlayer, long time)
    {
        Player player = zmPlayer.getPlayer();
        isBursting = true;
        lastFireTimeMs = time;

        final int[] shotsFired = {0}; // array is bc lambda requires final variables, lil hack

        player.getScheduler().runAtFixedRate(MAIN, task -> {
            if (!player.isOnline() || currentClip <= 0 || shotsFired[0] >= baseStats.burstShots()) {
                isBursting = false;
                task.cancel();
                return;
            }

            executeShot(player);
            currentClip--;
            zmPlayer.getInventory().syncBukkitInventoryWeapon(this);
            shotsFired[0]++;
        }, null, 1L, baseStats.burstDelayTicks());
    }

    @Override
    public void onReload(ZmPlayer player)
    {
        if (isReloading) {
            return;
        }

        int needed = baseStats.clipSize() - currentClip;
        if (needed <= 0 || currentReserve <= 0) {
            return;
        }

        isReloading = true;
        int amountToReload = Math.min(needed, currentReserve);

        player.getPlayer().getScheduler().runDelayed(MAIN, task -> {
            if (!player.getPlayer().isOnline() || !isReloading) {
                return;
            }

            isReloading = false;
            currentClip += amountToReload;
            currentReserve -= amountToReload;
            player.getInventory().syncBukkitInventoryWeapon(this);

        }, null, baseStats.reloadTicks());
    }

    @Override
    protected ItemStack buildItemStack()
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
                    .replace("{fire_rate}", String.valueOf(baseStats.fireRateTicks()))
                    .replace("{reload_ticks}", String.valueOf(baseStats.reloadTicks()))
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

    public GunStats getBaseStats() { return baseStats; }
    public boolean isReloading() { return isReloading; }
    public void setReloadStatus(boolean isReloading) { this.isReloading = isReloading; }

}
