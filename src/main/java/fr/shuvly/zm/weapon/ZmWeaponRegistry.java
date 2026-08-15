package fr.shuvly.zm.weapon;

import java.util.HashMap;
import java.util.Map;

public class ZmWeaponRegistry
{

    private final Map<String, ZmWeapon> registeredWeapons = new HashMap<>();


    public void register(String id, ZmWeapon weapon)
    {
        registeredWeapons.put(id, weapon);
    }


    public ZmWeapon getWeapon(String id) { return registeredWeapons.get(id); }
    public Map<String, ZmWeapon> getRegisteredWeapons() { return registeredWeapons; }

}
