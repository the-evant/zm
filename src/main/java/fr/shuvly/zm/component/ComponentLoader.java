package fr.shuvly.zm.component;

import fr.shuvly.zm.component.door.DoorComponentParser;
import fr.shuvly.zm.component.mystery_box.MysteryBoxComponentParser;
import fr.shuvly.zm.component.pap.PapComponentParser;
import fr.shuvly.zm.component.perk_machine.PerkMachineComponentParser;
import fr.shuvly.zm.component.wallbuy.WallbuyComponentParser;
import fr.shuvly.zm.map.MapParsingContext;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;
import java.util.Map;

public class ComponentLoader
{

    private static final Map<String, ComponentFactory<?>> FACTORIES = Map.of(
        "doors.yml", new DoorComponentParser(),
        "wallbuys.yml", new WallbuyComponentParser(),
        "paps.yml", new PapComponentParser(),
        "perk_machines.yml", new PerkMachineComponentParser(),
        "mystery_boxes.yml", new MysteryBoxComponentParser()
    );

    private static final List<String> KEYS_2_IGNORE = List.of(
        "settings"
    );


    public static void loadComponents(File componentsDir, MapParsingContext context)
    {
        if (!componentsDir.exists() || !componentsDir.isDirectory()) {
            return;
        }

        final File[] files = componentsDir.listFiles((dir, name) -> name.endsWith(".yml"));

        if (files == null) {
            return;
        }

        for (File file : files) {
            final ComponentFactory<?> factory = FACTORIES.get(file.getName());

            if (factory == null) {
                continue; // todo: log warning
            }

            final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

            for (String key : config.getKeys(false)) {
                if (KEYS_2_IGNORE.contains(key)) {
                    continue;
                }

                final BaseComponent component = factory.parse(key, config.getConfigurationSection(key), context);
                context.componentRegistry().register(component);
            }
        }
    }

}
