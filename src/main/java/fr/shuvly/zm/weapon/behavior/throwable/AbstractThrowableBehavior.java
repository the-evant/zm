package fr.shuvly.zm.weapon.behavior.throwable;

import fr.shuvly.paper.maditem.MadItem;
import fr.shuvly.paper.maditem.MadSkull;
import fr.shuvly.zm.Zm;
import fr.shuvly.zm.component.interaction.InteractionType;
import fr.shuvly.zm.player.ZmPlayer;
import fr.shuvly.zm.weapon.ZmWeapon;
import fr.shuvly.zm.weapon.ZmWeaponCategory;
import fr.shuvly.zm.weapon.ZmWeaponItemTemplate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class AbstractThrowableBehavior
    extends ZmWeapon
{

    protected static final Zm MAIN = Zm.getInstance();

    private final ThrowableStats baseStats;

    private int currentAmount;
    private long lastThrowTimeMs;


    protected AbstractThrowableBehavior(
        String id,
        ZmWeaponCategory category,
        boolean isInMysteryBox,
        ConfigurationSection config,
        ThrowableStats baseStats
    )
    {
        super(id, category, isInMysteryBox, config);
        this.baseStats = baseStats;
        this.currentAmount = baseStats.maxAmount();
        this.lastThrowTimeMs = 0;
    }

    protected AbstractThrowableBehavior(AbstractThrowableBehavior prototype)
    {
        super(prototype);
        this.baseStats = prototype.baseStats;
        this.currentAmount = prototype.baseStats.maxAmount();
        this.lastThrowTimeMs = 0;
    }


    protected abstract void executeThrow(Player player);
    protected abstract String formatSpecificLore(String line);


    @Override
    public void onInteract(ZmPlayer zmPlayer, InteractionType type)
    {
        if (type != InteractionType.RIGHT_CLICK) {
            return;
        }

        long now = System.currentTimeMillis();
        if (now - lastThrowTimeMs < (baseStats.throwCooldownTicks() * 50L)) {
            return;
        }

        if (currentAmount <= 0) return;

        final Player player = zmPlayer.getPlayer();

        executeThrow(player);

        currentAmount--;
        lastThrowTimeMs = now;

        zmPlayer.getInventory().syncBukkitInventoryWeapon(this);
    }

    @Override
    public void onReload(ZmPlayer player) {} // cant reload lolxd

    public void refill()
    {
        this.currentAmount = baseStats.maxAmount();
    }

    @Override
    protected ItemStack buildItemStack()
    {
        final ZmWeaponItemTemplate template = super.getItemTemplate();

        final List<String> formattedLore = template.lore().stream()
            .map(line -> line.replace("{amount}", String.valueOf(currentAmount))
                .replace("{max_amount}", String.valueOf(baseStats.maxAmount()))
                .replace("{cooldown}", String.valueOf(baseStats.throwCooldownTicks()))
            )
            .map(this::formatSpecificLore)
            .toList();

        final MadItem item = (template.base64texture() == null || template.base64texture().isEmpty())
            ? new MadItem(template.material())
            : new MadSkull().setTexture(template.base64texture());

        item.setName(template.displayName())
            .setLore(formattedLore)
            .setUnbreakable(true)
            .setAmount(Math.max(1, currentAmount));

        return item.build().getItemStack();
    }

}
