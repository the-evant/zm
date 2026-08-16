package fr.shuvly.zm.perk;

public enum ZmPerkType
{

    JUGGERNOG,
    MULE_KICK,
    SPEED_COLA,
    STAMINA_UP,
    DOUBLE_TAP;


    private final String id;


    ZmPerkType()
    {
        this.id = "perk_" + name().toLowerCase();
    }


    public String getId() { return id; }

}
