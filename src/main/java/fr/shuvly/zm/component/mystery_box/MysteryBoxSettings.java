package fr.shuvly.zm.component.mystery_box;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public record MysteryBoxSettings(
    String defaultBoxId,
    MysteryBoxCycleSetting cycleSetting,
    List<String> cycleOrder,
    MysteryBoxUsesRange maxUsesRange
)
{

    public static MysteryBoxSettings base()
    {
        return new MysteryBoxSettings(
            "RANDOM",
            MysteryBoxCycleSetting.RANDOM,
            List.of(),
            MysteryBoxUsesRange.base()
        );
    }

    public static MysteryBoxSettings parse(ConfigurationSection config)
    {
        if (config == null) {
            return base();
        }

        final String defaultBox = config.getString("default_box", "RANDOM");

        final String cycleSettingStr = config.getString("cycle_setting", "RANDOM").toUpperCase();
        final MysteryBoxCycleSetting cycleSetting = MysteryBoxCycleSetting.valueOf(cycleSettingStr);

        List<String> cycleOrder = config.getStringList("cycle_order");
        if (cycleOrder.isEmpty()) {
            cycleOrder = List.of("RANDOM");
        }

        final MysteryBoxUsesRange maxUsesRange = MysteryBoxUsesRange.parse(config, "max_uses");

        return new MysteryBoxSettings(defaultBox, cycleSetting, cycleOrder, maxUsesRange);
    }

}
