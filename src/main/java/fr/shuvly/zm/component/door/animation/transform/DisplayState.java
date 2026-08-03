package fr.shuvly.zm.component.door.animation.transform;

import fr.shuvly.zm.component.door.animation.DoorAnimationCompletionMode;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.util.Transformation;

record DisplayState(
    BlockDisplay display,
    Transformation finalTransform,
    BlockData blockData,
    int finalX, int finalY, int finalZ,
    int delay,
    DoorAnimationCompletionMode completionMode
) {}
