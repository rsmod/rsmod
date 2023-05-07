package org.rsmod.game.pathfinder.util

internal object RotationUtils {

    fun rotate(objectRot: Int, dimensionA: Int, dimensionB: Int): Int = when {
        objectRot and 0x1 != 0 -> dimensionB
        else -> dimensionA
    }

    fun rotate(objectRot: Int, blockAccessFlags: Int): Int = when (objectRot) {
        0 -> blockAccessFlags
        else -> ((blockAccessFlags shl objectRot) and 0xF) or (blockAccessFlags shr (4 - objectRot))
    }
}
