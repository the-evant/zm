package fr.shuvly.zm.perk;

import fr.shuvly.zm.perk.impl.*;

import java.util.HashMap;
import java.util.Map;

public class ZmPerkRegistry
{

    private final Map<String, ZmPerk> registeredPerks = new HashMap<>();


    public ZmPerkRegistry() { registerAll(); }


    public void registerAll()
    {
        register(new JuggernogPerk());
        register(new DoubleTapPerk());
        register(new MuleKickPerk());
        register(new SpeedColaPerk());
    }

    public void register(ZmPerk perk) { registeredPerks.put(perk.getType().getId(), perk); }


    public ZmPerk getPerk(String id) { return registeredPerks.get(id); }
    public Map<String, ZmPerk> getRegisteredPerks() { return registeredPerks; }

}
