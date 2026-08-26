package fr.shuvly.zm.component.perk_machine;

import fr.shuvly.zm.Zm;
import fr.shuvly.zm.perk.ZmPerk;
import fr.shuvly.zm.player.ZmPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.EntityEffect;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;

import static fr.shuvly.core.common.constant.TextParser.parse;

public class PerkMachineAnimator
{

    private static final Zm MAIN = Zm.getInstance();

    private final PerkMachineComponent parent;


    public PerkMachineAnimator(PerkMachineComponent parent)
    {
        this.parent = parent;
    }


    public void playDrinkAnimation(ZmPlayer zmPlayer, Runnable onComplete)
    {
        final Player player = zmPlayer.getPlayer();
        final ZmPerk perk = parent.getPerk();

        final ItemStack animationItem = perk.getItem().build();

        final int heldSlot = player.getInventory().getHeldItemSlot();
        final ItemStack oldItem = player.getInventory().getItem(heldSlot);

        player.getInventory().setItem(heldSlot, animationItem);

        player.playEffect(EntityEffect.PROTECTED_FROM_DEATH);

        final Component titleText = parse(perk.getDisplayName());
        final Component subtitleText = parse("<b>congrats bro!!!!!!!!1</b>");
        final Title.Times times = Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(2), Duration.ofMillis(500));
        player.showTitle(Title.title(titleText, subtitleText, times));

        player.getInventory().setItem(heldSlot, oldItem);

        player.getScheduler().runDelayed(MAIN, task -> {
            onComplete.run();
        }, null, 40L);
    }

}
