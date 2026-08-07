package fr.shuvly.zm.weapon;

import fr.shuvly.zm.Zm;
import fr.shuvly.zm.weapon.behavior.firearm.FirearmBehavior;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ZmWeaponRegistry
{

    private static final Zm MAIN = Zm.getInstance();
    private static final Logger LOGGER = MAIN.getLogger();

    private final Map<String, ZmWeaponFactory> behaviorFactories = new HashMap<>();
    private final Map<String, ZmWeapon> registeredWeapons = new HashMap<>();


    public ZmWeaponRegistry()
    {
        registerDefaultBehaviors();
    }


    private void registerDefaultBehaviors()
    {
        registerBehavior(FirearmBehavior.class);
    }

    private void registerBehavior(Class<? extends ZmWeaponFactory> clazz)
    {
        try {
            final ZmWeaponFactory factory = clazz.getDeclaredConstructor().newInstance();

            behaviorFactories.put(factory.getType(), factory);

            LOGGER.info("Weapon factory '" + factory.getType() + "' registered!");
        }
        catch (
            InstantiationException |
            IllegalAccessException |
            InvocationTargetException |
            NoSuchMethodException exception
        ) {
            LOGGER.severe("Error while registering weapon factory \"" + clazz.getName() + "\"!");
            LOGGER.severe(exception.toString());
            exception.printStackTrace();
        }
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
