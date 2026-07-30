package fr.shuvly.zm.component;

import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ComponentRegistry
{

    private final Map<String, BaseComponent> components = new HashMap<>();


    /**
     * Registers a single component into the active memory map.
     */
    public void register(BaseComponent component)
    {
        this.components.put(component.getId(), component);
    }

    /**
     * Registers a collection of components (used during the parsing phase).
     */
    public void registerAll(
        @NonNull Collection<? extends BaseComponent> newComponents
    )
    {
        for (BaseComponent component : newComponents) {
            this.register(component);
        }
    }

    /**
     * Clears all components. Call this when the game ends to prevent memory leaks.
     */
    public void clear() { this.components.clear(); }

    /**
     * Retrieves a specific component by its config ID.
     */
    public BaseComponent getComponent(String id) { return this.components.get(id); }

    /**
     * Returns a read-only view of all registered components.
     */
    public Collection<BaseComponent> getAll() { return Collections.unmodifiableCollection(this.components.values()); }

}
