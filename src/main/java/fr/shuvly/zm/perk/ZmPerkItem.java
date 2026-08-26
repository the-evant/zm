package fr.shuvly.zm.perk;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DeathProtection;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

public class ZmPerkItem
{

    private final ItemStack item;


    private ZmPerkItem(ItemStack item)
    {
        this.item = item;

        // ignore "experimental state", it works lol
        item.setData(
            DataComponentTypes.DEATH_PROTECTION,
            DeathProtection.deathProtection().build()
        );
    }


    public static ZmPerkItem potion(Color potionColor)
    {
        final ItemStack item = new ItemStack(Material.POTION);
        final PotionMeta meta = (PotionMeta) item.getItemMeta();

        if (meta != null) {
            meta.setColor(potionColor);
            item.setItemMeta(meta);
        } else { // wtf
            throw new RuntimeException("what"); // todo: better error message
        }

        return new ZmPerkItem(item);
    }


    public ItemStack build() { return item; }

}
