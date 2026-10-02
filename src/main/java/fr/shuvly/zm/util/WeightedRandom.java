package fr.shuvly.zm.util;

import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

public final class WeightedRandom
{

    private WeightedRandom() {}


    public static <T extends Weighted> T roll(Collection<T> items)
    {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Cannot roll from an empty or null weighted collection.");
        }

        double totalWeight = 0.0;
        for (T item : items) {
            if (item.getWeight() > 0) {
                totalWeight += item.getWeight();
            }
        }

        if (totalWeight <= 0.0) {
            throw new IllegalStateException("Total weight of the pool must be strictly > 0.");
        }

        double randomValue = ThreadLocalRandom.current().nextDouble(totalWeight);
        double cumulativeWeight = 0.0;

        for (T item : items) {
            if (item.getWeight() <= 0) {
                continue;
            }

            cumulativeWeight += item.getWeight();
            if (randomValue < cumulativeWeight) {
                return item;
            }
        }

        return items.stream().reduce((_, second) -> second).orElseThrow();
    }

}
