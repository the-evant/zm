package fr.shuvly.zm.weapon;

import fr.shuvly.zm.weapon.behavior.HitscanBehavior;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class ZmWeaponRegistry
{

    private final Map<String, ZmWeaponFactory> behaviorFactories = new HashMap<>();
    private final Map<String, ZmWeapon> registeredWeapons = new HashMap<>();


    public ZmWeaponRegistry()
    {
        registerDefaultBehaviors();
    }


    private void registerDefaultBehaviors()
    {
        behaviorFactories.put("HITSCAN", HitscanBehavior::new);
    }

    public void loadWeaponFromYaml(ConfigurationSection config)
    {
        final String id = config.getString("id");
        final String behaviorId = config.getString("behavior");
        final boolean isInMysteryBox = config.getBoolean("is_in_mystery_box", true);

        if (id == null || behaviorId == null) {
            throw new IllegalArgumentException("Weapon config missing 'id' or 'behavior'");
        }

        final ZmWeaponFactory factory = behaviorFactories.get(behaviorId);

        if (factory == null) {
            throw new IllegalArgumentException("Unknown behavior: " + behaviorId);
        }

        final ZmWeapon weapon = factory.create(id, isInMysteryBox, config);
        registeredWeapons.put(id, weapon);
    }

    public ZmWeapon getWeapon(String id) { return registeredWeapons.get(id); }

    public Map<String, ZmWeapon> getRegisteredWeapons() { return registeredWeapons; }

}
