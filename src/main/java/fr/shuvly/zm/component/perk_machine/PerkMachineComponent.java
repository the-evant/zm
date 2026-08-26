package fr.shuvly.zm.component.perk_machine;

import fr.shuvly.zm.component.BaseComponent;
import fr.shuvly.zm.component.Purchasable;
import fr.shuvly.zm.component.interaction.InteractionTrigger;
import fr.shuvly.zm.perk.ZmPerk;
import fr.shuvly.zm.player.ZmPlayer;

import static fr.shuvly.core.common.constant.TextParser.parse;

public class PerkMachineComponent
    extends BaseComponent
    implements Purchasable
{

    private final ZmPerk perk;
    private final int cost;

    private final PerkMachineAnimator animator;


    public PerkMachineComponent(
        String id,
        InteractionTrigger trigger,
        ZmPerk perk,
        int cost
    )
    {
        super(id, trigger);
        this.perk = perk;
        this.cost = cost;
        this.animator = new PerkMachineAnimator(this);
    }


    @Override
    public int getCost(ZmPlayer player)
    {
        return this.cost;
    }

    @Override
    public boolean canBePurchased(ZmPlayer player)
    {
        return true; //!player.hasPerk(perk.getType());
    }

    @Override
    public void onPurchase(ZmPlayer player)
    {
        animator.playDrinkAnimation(player, () -> {
            player.addPerk(perk);
            perk.apply(player);
            player.getPlayer().sendMessage(parse("perk added lol!!!"));
        });
    }

    @Override
    public String getPromptText(ZmPlayer player)
    {
        if (player.hasPerk(perk.getType())) {
            return null;
        }

        return "Press [<key:key.swapOffhand>] to buy " + perk.getDisplayName() + " [Cost: " + cost + "]";
    }

    public ZmPerk getPerk() { return perk; }

}
