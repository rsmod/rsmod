package org.rsmod.plugins.info.player.bitcode

import org.rsmod.plugins.info.buffer.BitBuffer
import org.rsmod.plugins.info.model.coord.HighResCoord
import org.rsmod.plugins.info.model.coord.LowResCoord

internal fun BitBuffer.putHighResUpdate(
    extended: Boolean,
    currCoords: HighResCoord,
    prevCoords: HighResCoord
): BitBuffer {
    putBoolean(true)
    putBoolean(extended)
    val diff = currCoords - prevCoords
    // If no movement, or if player just logged in, that means this
    // update is strictly for extended-info.
    if (extended && (diff.packed == 0 || prevCoords.packed == 0)) {
        putBits(len = 2, value = 0)
        return this
    }
    if (diff.level == 0) {
        if (diff.x in -1..1 && diff.z in -1..1) {
            putBits(len = 2, value = 1)
            putBits(len = 3, value = get3BitDirection(diff.x, diff.z))
            return this
        } else if (diff.x in -2..2 && diff.z in -2..2) {
            putBits(len = 2, value = 2)
            putBits(len = 4, value = get4BitDirection(diff.x, diff.z))
            return this
        }
    }
    if (diff.x in -15..15 && diff.z in -15..15) {
        putBits(len = 2, value = 3)
        putBoolean(false)
        putBits(len = 2, value = diff.level)
        putBits(len = 5, value = diff.x and 0x1F)
        putBits(len = 5, value = diff.z and 0x1F)
    } else {
        putBits(len = 2, value = 3)
        putBoolean(true)
        putBits(len = 2, value = diff.level)
        putBits(len = 14, value = diff.x)
        putBits(len = 14, value = diff.z)
    }
    return this
}

internal fun BitBuffer.putHighToLowResChange(currCoords: LowResCoord, prevCoords: LowResCoord): BitBuffer {
    val updateLowResCoords = currCoords != prevCoords
    putBoolean(true)
    putBoolean(false)
    putBits(len = 2, value = 0)
    putBoolean(updateLowResCoords)
    if (updateLowResCoords) {
        val diff = currCoords - prevCoords
        if (diff.packed == 0) { /* all coords are 0 */
            putBits(len = 2, value = 0)
        } else if (diff.x == 0 && diff.z == 0) {
            putBits(len = 2, value = 1)
            putBits(len = 2, value = diff.level)
        } else if (diff.x in -1..1 && diff.z in -1..1) {
            putBits(len = 2, value = 2)
            putBits(len = 2, value = diff.level)
            putBits(len = 3, value = get3BitDirection(diff.x, diff.z))
        } else {
            putBits(len = 2, value = 3)
            putBits(len = 2, value = diff.level)
            putBits(len = 8, value = diff.x)
            putBits(len = 8, value = diff.z)
        }
    }
    return this
}

internal fun BitBuffer.putLowResUpdate(currCoords: LowResCoord, prevCoords: LowResCoord): BitBuffer {
    putBoolean(true)
    putLowResCoordsChange(currCoords, prevCoords)
    return this
}

internal fun BitBuffer.putLowToHighResChange(currCoords: HighResCoord, prevCoords: HighResCoord): BitBuffer {
    val lowResCurrCoords = currCoords.toLowRes()
    val lowResPrevCoords = prevCoords.toLowRes()
    val updateLowResCoords = lowResCurrCoords != lowResPrevCoords
    putBoolean(true)
    putBits(len = 2, value = 0)
    putBoolean(updateLowResCoords)
    if (updateLowResCoords) {
        putLowResCoordsChange(lowResCurrCoords, lowResPrevCoords)
    }
    putBits(len = 13, value = currCoords.x)
    putBits(len = 13, value = currCoords.z)
    return this
}

internal fun BitBuffer.putLowResCoordsChange(currCoords: LowResCoord, prevCoords: LowResCoord) {
    val diff = currCoords - prevCoords
    if (diff.x == 0 && diff.z == 0 && diff.level == 0) {
        putBits(len = 2, value = 0)
    } else if (diff.x == 0 && diff.z == 0) {
        putBits(len = 2, value = 1)
        putBits(len = 2, value = diff.level)
    } else if (diff.x in -1..1 && diff.z in -1..1) {
        putBits(len = 2, value = 2)
        putBits(len = 2, value = diff.level)
        putBits(len = 3, value = get3BitDirection(diff.x, diff.z))
    } else {
        putBits(len = 2, value = 3)
        putBits(len = 2, value = diff.level)
        putBits(len = 8, value = diff.x and 0xFF)
        putBits(len = 8, value = diff.z and 0xFF)
    }
}

internal fun BitBuffer.putSkipCount(count: Int) {
    putBoolean(false)
    when {
        count == 0 -> putBits(len = 2, value = 0)
        count <= 0x1F -> {
            putBits(len = 2, value = 1)
            putBits(len = 5, value = count)
        }
        count <= 0xFF -> {
            putBits(len = 2, value = 2)
            putBits(len = 8, value = count)
        }
        else -> {
            putBits(len = 2, value = 3)
            putBits(len = 11, value = count)
        }
    }
}

private fun get3BitDirection(dx: Int, dy: Int): Int {
    require(dx != 0 || dy != 0)
    if (dx == -1 && dy == -1) return 0
    if (dx == 0 && dy == -1) return 1
    if (dx == 1 && dy == -1) return 2
    if (dx == -1 && dy == 0) return 3
    if (dx == 1 && dy == 0) return 4
    if (dx == -1 && dy == 1) return 5
    if (dx == 0 && dy == 1) return 6
    return if (dx == 1 && dy == 1) 7 else 0
}

private fun get4BitDirection(dx: Int, dy: Int): Int {
    require(dx != 0 || dy != 0)
    if (dx == -2 && dy == -2) return 0
    if (dx == -1 && dy == -2) return 1
    if (dx == 0 && dy == -2) return 2
    if (dx == 1 && dy == -2) return 3
    if (dx == 2 && dy == -2) return 4
    if (dx == -2 && dy == -1) return 5
    if (dx == 2 && dy == -1) return 6
    if (dx == -2 && dy == 0) return 7
    if (dx == 2 && dy == 0) return 8
    if (dx == -2 && dy == 1) return 9
    if (dx == 2 && dy == 1) return 10
    if (dx == -2 && dy == 2) return 11
    if (dx == -1 && dy == 2) return 12
    if (dx == 0 && dy == 2) return 13
    if (dx == 1 && dy == 2) return 14
    return if (dx == 2 && dy == 2) 15 else 0
}
