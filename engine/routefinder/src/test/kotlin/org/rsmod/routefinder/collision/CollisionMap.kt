package org.rsmod.routefinder.collision

import kotlin.math.max
import kotlin.math.min

fun buildCollisionMap(x1: Int, z1: Int, x2: Int, z2: Int) =
    CollisionFlagMap().apply {
        for (level in 0 until 4) {
            for (z in min(z1, z2)..max(z1, z2)) {
                for (x in min(x1, x2)..max(x1, x2)) {
                    allocateIfAbsent(x, z, level)
                }
            }
        }
    }

fun CollisionFlagMap.flag(
    baseX: Int,
    baseZ: Int,
    width: Int,
    length: Int,
    mask: Int,
): CollisionFlagMap {
    for (level in 0 until 4) {
        for (z in 0 until length) {
            for (x in 0 until width) {
                this[baseX + x, baseZ + z, level] = mask
            }
        }
    }
    return this
}
