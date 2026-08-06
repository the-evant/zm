package fr.shuvly.zm.weapon;

import fr.shuvly.zm.component.interaction.InteractionType;
import fr.shuvly.zm.player.ZmPlayer;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public abstract class ZmWeapon
{

    public static final NamespacedKey INSTANCE_UUID_KEY = new NamespacedKey("zm", "wpn_uuid");

    private final String id;
    private final ZmWeaponCategory category;
    private final ZmWeaponItem itemTemplate;

    private UUID instanceUuid;


    protected ZmWeapon(String id, ZmWeaponCategory category, ConfigurationSection config)
    {
        this.id = id;
        this.category = category;
        this.itemTemplate = ZmWeaponItem.fromConfig(config.getConfigurationSection("item"));
        this.instanceUuid = UUID.randomUUID();
    }

    protected ZmWeapon(ZmWeapon prototype)
    {
        this.id = prototype.id;
        this.category = prototype.category;
        this.itemTemplate = prototype.itemTemplate;
        this.instanceUuid = UUID.randomUUID();
    }

    public abstract ZmWeapon duplicate();
    public abstract void onInteract(ZmPlayer player, InteractionType type);
    public abstract void onReload(ZmPlayer player);
    protected abstract ItemStack buildItemStack();

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
    protected ZmWeaponItem getItemTemplate() { return itemTemplate; }
    public UUID getInstanceUuid() { return instanceUuid; }

}
