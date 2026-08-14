package fr.shuvly.zm.weapon;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public record ZmWeaponItemTemplate(
    Material material,
    int customModelData,
    String base64texture,
    String displayName,
    List<String> lore,
    boolean glowing,
    boolean hideTooltips
)
{

    public static ZmWeaponItemTemplate fromConfig(ConfigurationSection config)
    {
        if (config == null) {
            throw new IllegalArgumentException("Weapon missing 'item' config");
        }

        return new ZmWeaponItemTemplate(
            Material.valueOf(config.getString("material", "STONE").toUpperCase()),
            config.getInt("custom_model_data", 0),
            config.getString("base64_texture", null), // null if not a head
            config.getString("display_name", "Unknown Weapon"),
            config.getStringList("lore"),
            config.getBoolean("glowing", false),
            config.getBoolean("hide_tooltips", true)
        );
    }

}
