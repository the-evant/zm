package fr.shuvly.zm.component;

import fr.shuvly.zm.map.region.Region;

public abstract class BaseComponent
{

    protected final String id;
    protected final Region hitbox;


    protected BaseComponent(String id, Region region)
    {
        this.id = id;
        this.hitbox = region;
    }


    public String getId() { return id; }
    public Region getHitbox() { return hitbox; }

}
