package fr.shuvly.zm.weapon;

import fr.shuvly.zm.Zm;
import fr.shuvly.zm.weapon.behavior.gun.firearm.FirearmBehavior;
import fr.shuvly.zm.weapon.behavior.gun.projectile.ProjectileBehavior;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ZmWeaponRegistry
{

    private static final Zm MAIN = Zm.getInstance();

    private final Map<String, ZmWeaponFactory> behaviorFactories = new HashMap<>();
    private final Map<String, ZmWeapon> registeredWeapons = new HashMap<>();


    public ZmWeaponRegistry()
    {
        registerDefaultBehaviors();
    }


    private void registerDefaultBehaviors()
    {
        behaviorFactories.put("FIREARM", FirearmBehavior::new);
        behaviorFactories.put("PROJECTILE", ProjectileBehavior::new);
    }

    public void loadWeaponFromYaml(ConfigurationSection config)
    {
        final String id = config.getString("id");
        final String behavior = config.getString("behavior");
        final boolean isInMysteryBox = config.getBoolean("is_in_mystery_box", true);

        if (id == null || behavior == null) {
            throw new IllegalArgumentException("Weapon config missing 'id' or 'behavior'");
        }

        final ZmWeaponFactory factory = behaviorFactories.get(behavior);

        if (factory == null) {
            throw new IllegalArgumentException("Unknown behavior: " + behavior);
        }

        final ZmWeapon weapon = factory.create(id, isInMysteryBox, config);
        registeredWeapons.put(id, weapon);
    }

    public ZmWeapon getWeapon(String id) { return registeredWeapons.get(id); }

    public Map<String, ZmWeapon> getRegisteredWeapons() { return registeredWeapons; }

}
