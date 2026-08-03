package fr.shuvly.zm.component.door.animation;

public enum DoorAnimationCompletionMode
{

    RM,     // Removes the BlockDisplays, leaves AIR.
    BLOCKS, // Removes the BlockDisplays, places real blocks at the new position.
    DISPLAY // Keeps the BlockDisplays indefinitely, places no blocks.

}
