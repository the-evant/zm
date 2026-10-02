package fr.shuvly.zm.component.mystery_box;

import org.bukkit.configuration.ConfigurationSection;

import java.util.concurrent.ThreadLocalRandom;

public record MysteryBoxUsesRange(
    int min, int max
)
{

    public MysteryBoxUsesRange
    {
        if (min < 1 || max < 1) {
            throw new IllegalArgumentException("Mystery Box uses must be strictly > 1.");
        }
        if (min > max) {
            throw new IllegalArgumentException("Mystery Box min uses cannot be greater than max uses.");
        }
    }

    public static MysteryBoxUsesRange base()
    {
        return new MysteryBoxUsesRange(4, 8);
    }


    public int roll()
    {
        return min == max ? min : ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public static MysteryBoxUsesRange parse(
        ConfigurationSection config,
        String key,
        MysteryBoxUsesRange fallback
    )
    {
        if (config == null || !config.contains(key)) {
            return fallback;
        }

        if (config.isInt(key)) {
            int val = config.getInt(key);
            return new MysteryBoxUsesRange(val, val);
        }

        final String str = config.getString(key);

        if (str != null && str.contains("-")) {
            final String[] parts = str.split("-");

            return new MysteryBoxUsesRange(
                Integer.parseInt(parts[0].trim()),
                Integer.parseInt(parts[1].trim())
            );
        }

        throw new IllegalArgumentException("Invalid format for max_uses: " + str);
    }

    public static MysteryBoxUsesRange parse(
        ConfigurationSection config,
        String key
    )
    {
        return parse(config, key, MysteryBoxUsesRange.base());
    }

}