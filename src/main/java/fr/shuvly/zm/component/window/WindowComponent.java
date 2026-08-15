package fr.shuvly.zm.component.window;

import fr.shuvly.zm.component.BaseComponent;
import fr.shuvly.zm.component.Interactable;
import fr.shuvly.zm.component.interaction.InteractionTrigger;

public class WindowComponent
    extends BaseComponent
    implements Interactable
{

    private WindowState currentState = WindowState.INTACT;
    private boolean initialized = false;


    public WindowComponent(String id, InteractionTrigger trigger)
    {
        super(id, trigger);
//        this.windowRegion = windowRegion;
    }

}
