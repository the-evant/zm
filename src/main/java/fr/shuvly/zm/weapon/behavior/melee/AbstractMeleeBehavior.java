package fr.shuvly.zm.weapon.behavior.melee;

import fr.shuvly.paper.maditem.MadItem;
import fr.shuvly.paper.maditem.MadSkull;
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

public abstract class AbstractMeleeBehavior
    extends ZmWeapon
    implements ZmWeaponFactory
{

    private final MeleeStats baseStats;
    private long lastAttackTimeMs = 0;


    protected AbstractMeleeBehavior(
        String id,
        ConfigurationSection config,
        MeleeStats baseStats
    )
    {
        super(id, ZmWeaponCategory.MELEE, config);
        this.baseStats = baseStats;
    }

    protected AbstractMeleeBehavior(AbstractMeleeBehavior prototype)
    {
        super(prototype);
        this.baseStats = prototype.baseStats;
    }


    protected abstract void executeMelee(Player player);
    protected abstract String formatSpecificLore(String line);


    @Override
    public void onInteract(ZmPlayer zmPlayer, InteractionType type)
    {
        if (type != InteractionType.LEFT_CLICK) {
            return;
        }

        long now = System.currentTimeMillis();
        if (now - lastAttackTimeMs < (baseStats.cooldownTicks() * 50L)) {
            return;
        }

        lastAttackTimeMs = now;

        executeMelee(zmPlayer.getPlayer());
    }

    @Override
    public void onReload(ZmPlayer player) {} // no reload lol

    @Override
    protected ItemStack buildItemStack()
    {
        final ZmWeaponItemTemplate template = super.getItemTemplate();

        final List<String> formattedLore = template.lore().stream()
            .map(line -> line.replace("{damage}", String.valueOf(baseStats.damage()))
                .replace("{range}", String.valueOf(baseStats.range()))
                .replace("{cooldown}", String.valueOf(baseStats.cooldownTicks())))
            .map(this::formatSpecificLore)
            .toList();

        final MadItem item = (template.base64texture() == null || template.base64texture().isEmpty())
            ? new MadItem(template.material())
            : new MadSkull().setTexture(template.base64texture());

        item.setName(template.displayName())
            .setLore(formattedLore)
            .setUnbreakable(true);

        return item.build().getItemStack();
    }

    @Override
    public void refillAmmo() {}

}
