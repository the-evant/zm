package fr.shuvly.zm.component;

import fr.shuvly.zm.component.interaction.InteractionTrigger;

public abstract class BaseComponent
{

    private final String id;
    private final InteractionTrigger trigger;


    protected BaseComponent(String id, InteractionTrigger trigger)
    {
        this.id = id;
        this.trigger = trigger;
    }


    public String getId() { return id; }
    public InteractionTrigger getInteractionTrigger() { return trigger; }

}
