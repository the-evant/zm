package fr.shuvly.zm.weapon;

import fr.shuvly.zm.component.interaction.InteractionType;
import fr.shuvly.zm.player.ZmPlayer;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;
import java.util.UUID;

public abstract class ZmWeapon
{

    public static final NamespacedKey INSTANCE_UUID_KEY = new NamespacedKey("zm", "wpn_uuid");

    private final String id;
    private final ZmWeaponCategory category;
    private final ZmWeaponItemTemplate itemTemplate;
    private final boolean isInMysteryBox;
    private final PapUpgradeConfig papConfig;

    private final UUID instanceUuid;


    protected ZmWeapon(
        String id,
        ZmWeaponCategory category,
        ConfigurationSection config
    )
    {
        this.id = id;
        this.category = category;
        this.itemTemplate = ZmWeaponItemTemplate.fromConfig(config.getConfigurationSection("item"));
        this.isInMysteryBox = config.getBoolean("is_in_mystery_box", true);
        this.papConfig = PapUpgradeConfig.fromConfig(config.getConfigurationSection("pap"));

        this.instanceUuid = UUID.randomUUID();
    }

    protected ZmWeapon(ZmWeapon prototype)
    {
        this.id = prototype.id;
        this.category = prototype.category;
        this.itemTemplate = prototype.itemTemplate;
        this.isInMysteryBox = prototype.isInMysteryBox;
        this.papConfig = prototype.papConfig;

        this.instanceUuid = UUID.randomUUID();
    }


    public abstract ZmWeapon duplicate();
    public abstract void onInteract(ZmPlayer player, InteractionType type);
    public abstract void onReload(ZmPlayer player);
    protected abstract ItemStack buildItemStack();
    public abstract void refillAmmo();

    public ItemStack getItemStack()
    {
        final ItemStack item = buildItemStack();
        final ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.getPersistentDataContainer().set(INSTANCE_UUID_KEY, PersistentDataType.STRING, instanceUuid.toString());
            item.setItemMeta(meta);
        }

        return item;
    }


    public String getId() { return id; }
    public ZmWeaponCategory getCategory() { return category; }
    public ZmWeaponItemTemplate getItemTemplate() { return itemTemplate; }
    public boolean isInMysteryBox() { return isInMysteryBox; }
    public Optional<PapUpgradeConfig> getPapConfig() { return Optional.ofNullable(papConfig); }
    public boolean isPackAPunchable() { return getPapConfig().isPresent(); }
    public UUID getInstanceUuid() { return instanceUuid; }

    // ...

    /**
     * Extracts the weapon UUID from the ItemStack's PDC.
     * @param   item    Weapon itemstack
     * @return  null if invalid or not a ZmWeapon.
     */
    public static UUID getWeaponUuid(ItemStack item)
    {
        if (item == null || !item.hasItemMeta()) {
            return null;
        }

        final String uuidStr = item.getItemMeta().getPersistentDataContainer().get(ZmWeapon.INSTANCE_UUID_KEY, PersistentDataType.STRING);
        return uuidStr == null ? null : UUID.fromString(uuidStr);
    }

}
