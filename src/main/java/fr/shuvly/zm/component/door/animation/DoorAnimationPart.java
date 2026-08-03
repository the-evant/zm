package fr.shuvly.zm.component.door.animation;

import fr.shuvly.zm.map.region.Region;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

public record DoorAnimationPart(
    Region blocksRegion,
    Vector pivot,             // hinge position (or center of mass)
    Vector3f translation,     // end position offset
    Vector3f rotationDegrees  // end rotation in degrees (pitch, yaw, roll)
) {}
