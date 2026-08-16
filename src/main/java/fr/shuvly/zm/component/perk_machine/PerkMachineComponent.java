package fr.shuvly.zm.component.perk_machine;

import fr.shuvly.zm.component.BaseComponent;
import fr.shuvly.zm.component.Purchasable;
import fr.shuvly.zm.component.interaction.InteractionTrigger;
import fr.shuvly.zm.perk.ZmPerk;
import fr.shuvly.zm.player.ZmPlayer;

public class PerkMachineComponent
    extends BaseComponent
    implements Purchasable
{

    private final ZmPerk perk;
    private final int cost;


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
    }


    @Override
    public int getCost(ZmPlayer player)
    {
        return this.cost;
    }

    @Override
    public boolean canBePurchased(ZmPlayer player)
    {
        return !player.hasPerk(perk.getType());
    }

    @Override
    public void onPurchase(ZmPlayer player)
    {
        player.addPerk(perk);
        perk.apply(player);

    }

    @Override
    public String getPromptText(ZmPlayer player)
    {
        if (player.hasPerk(perk.getType())) {
            return null;
        }

        return "Press [<key:key.swapOffhand>] to buy " + perk.getDisplayName() + " [Cost: " + cost + "]";
    }

}
