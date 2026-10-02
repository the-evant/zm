package fr.shuvly.zm.component.mystery_box;

import fr.shuvly.zm.component.BaseComponent;
import fr.shuvly.zm.component.ComponentRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MysteryBoxManager
{

    private final List<MysteryBoxComponent> allBoxes = new ArrayList<>();
    private final MysteryBoxCycleSetting cycleSetting;
    private final List<String> cycleOrder;

    private MysteryBoxComponent currentBox;
    private int cycleIndex = 0;


    public MysteryBoxManager(
        ComponentRegistry registry,
        MysteryBoxSettings settings
    )
    {
        final String defaultBoxId = settings.defaultBoxId();

        this.cycleSetting = settings.cycleSetting();
        this.cycleOrder = settings.cycleOrder();

        for (BaseComponent component : registry.getAll()) {
            if (component instanceof MysteryBoxComponent box) {
                box.setActive(false);
                box.setRelocateCallback(this::moveToNextBox);
                allBoxes.add(box);
            }
        }

        if (allBoxes.isEmpty()) {
            return;
        }

        if (defaultBoxId.equalsIgnoreCase("RANDOM")) {
            this.currentBox = allBoxes.get(ThreadLocalRandom.current().nextInt(allBoxes.size()));
        } else {
            this.currentBox = allBoxes.stream()
                .filter(b -> b.getId().equals(defaultBoxId))
                .findFirst()
                .orElse(allBoxes.getFirst()); // todo: error?
        }

        this.currentBox.setActive(true);
    }


    public void moveToNextBox()
    {
        if (allBoxes.size() <= 1) {
            this.currentBox.setActive(true);
            return;
        }

        if (cycleSetting == MysteryBoxCycleSetting.RANDOM) {
            MysteryBoxComponent nextBox;
            do {
                nextBox = allBoxes.get(ThreadLocalRandom.current().nextInt(allBoxes.size()));
            } while (nextBox == currentBox);

            this.currentBox = nextBox;
        } else {
            final String nextId = cycleOrder.get(cycleIndex);

            this.currentBox = allBoxes.stream()
                .filter(b -> b.getId().equals(nextId))
                .findFirst()
                .orElse(allBoxes.getFirst()); // todo: error???

            cycleIndex++;

            if (cycleIndex >= cycleOrder.size()) {
                cycleIndex = 0;
            }
        }

        // todo: delay for animation purposes??
        //  In a highly polished map, you might delay this by 5 seconds so players
        //  see the beam of light move across the sky before the box is fully active.
        this.currentBox.setActive(true);
    }

}
