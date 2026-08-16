package fr.shuvly.zm.component.perk_machine;

import fr.shuvly.zm.perk.ZmPerk;
import fr.shuvly.zm.player.ZmPlayer;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DeathProtection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Color;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import java.time.Duration;

import static fr.shuvly.core.common.constant.TextParser.parse;

public class PerkMachineAnimator
{

    public static void playDrinkAnimation(ZmPlayer zmPlayer, ZmPerk perk)
    {
        final Player player = zmPlayer.getPlayer();

        final ItemStack perkBottle = new ItemStack(Material.POTION);

        // ignore experimental state
        perkBottle.setData(DataComponentTypes.DEATH_PROTECTION, DeathProtection.deathProtection().build());

        final PotionMeta meta = (PotionMeta) perkBottle.getItemMeta();
        if (meta != null) {
            meta.displayName(parse(perk.getDisplayName()));
            meta.setColor(Color.RED); // todo: make this dynamic lol
            perkBottle.setItemMeta(meta);
        }

        final int heldSlot = player.getInventory().getHeldItemSlot();
        final ItemStack oldItem = player.getInventory().getItem(heldSlot);

        player.getInventory().setItem(heldSlot, perkBottle);

        player.playEffect(EntityEffect.PROTECTED_FROM_DEATH);

        final Component titleText = parse(perk.getDisplayName());
        final Component subtitleText = parse("<b>congrats bro!!!!!!!!1</b>");
        final Title.Times times = Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(2), Duration.ofMillis(500));
        player.showTitle(Title.title(titleText, subtitleText, times));

        // todo: maybe put that in a callback the parent will provide? bc thats not animation
        zmPlayer.addPerk(perk);
        perk.apply(zmPlayer);

        player.getInventory().setItem(heldSlot, oldItem);
    }

}
