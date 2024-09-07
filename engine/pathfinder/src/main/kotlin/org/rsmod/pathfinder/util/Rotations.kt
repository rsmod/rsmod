package org.rsmod.pathfinder.util

public object Rotations {
    public fun rotate(angle: Int, dimensionA: Int, dimensionB: Int): Int =
        if (angle and 0x1 != 0) {
            dimensionB
        } else {
            dimensionA
        }

    public fun rotate(angle: Int, blockAccessFlags: Int): Int =
        if (angle == 0) {
            blockAccessFlags
        } else {
            ((blockAccessFlags shl angle) and 0xF) or (blockAccessFlags shr (4 - angle))
        }
}
