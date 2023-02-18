package org.rsmod.plugins.info.player

import org.rsmod.plugins.info.player.buffer.BitBuffer
import org.rsmod.plugins.info.player.model.bitcode.I26BitCode
import org.rsmod.plugins.info.player.model.coord.HighResCoord
import org.rsmod.plugins.info.player.model.coord.LowResCoord

internal fun BitBuffer.getI26BitCode(bits: Int): I26BitCode {
    return I26BitCode(getBits(bits), bits)
}

internal fun BitBuffer.putI26BitCode(bitCode: I26BitCode): BitBuffer {
    if (bitCode.bitCount == 0) return this
    putBits(bitCode.bitCount, bitCode.value)
    return this
}

internal fun BitBuffer.putHighResUpdate(
    extended: Boolean,
    currCoords: HighResCoord,
    prevCoords: HighResCoord
): BitBuffer {
    putBoolean(true)
    putBoolean(extended)
    /* assume this is from log-in */
    if (prevCoords.packed == 0) {
        putBits(len = 2, value = 0)
        return this
    }
    val diff = currCoords - prevCoords
    /* if no movement - update is for extended info only */
    if (extended && diff.packed == 0) {
        putBits(len = 2, value = 0)
        return this
    }
    if (diff.level == 0) {
        if (diff.x in -1..1 && diff.y in -1..1) {
            putBits(len = 2, value = 1)
            putBits(len = 3, value = get3BitDirection(diff.x, diff.y))
            return this
        } else if (diff.x in -2..2 && diff.y in -2..2) {
            putBits(len = 2, value = 2)
            putBits(len = 4, value = get4BitDirection(diff.x, diff.y))
            return this
        }
    }
    if (diff.x in -15..15 && diff.y in -15..15) {
        putBits(len = 2, value = 3)
        putBoolean(false)
        putBits(len = 2, value = diff.level)
        putBits(len = 5, value = diff.x and 0x1F)
        putBits(len = 5, value = diff.y and 0x1F)
    } else {
        putBits(len = 2, value = 3)
        putBoolean(true)
        putBits(len = 2, value = diff.level)
        putBits(len = 14, value = diff.x)
        putBits(len = 14, value = diff.y)
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
        } else if (diff.x == 0 && diff.y == 0) {
            putBits(len = 2, value = 1)
            putBits(len = 2, value = diff.level)
        } else if (diff.x in -1..1 && diff.y in -1..1) {
            putBits(len = 2, value = 2)
            putBits(len = 2, value = diff.level)
            putBits(len = 3, value = get3BitDirection(diff.x, diff.y))
        } else {
            putBits(len = 2, value = 3)
            putBits(len = 2, value = diff.level)
            putBits(len = 8, value = diff.x)
            putBits(len = 8, value = diff.y)
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
    putBits(len = 13, value = currCoords.y)
    return this
}

internal fun BitBuffer.putLowResCoordsChange(currCoords: LowResCoord, prevCoords: LowResCoord) {
    val diff = currCoords - prevCoords
    if (diff.x == 0 && diff.y == 0 && diff.level == 0) {
        putBits(len = 2, value = 0)
    } else if (diff.x == 0 && diff.y == 0) {
        putBits(len = 2, value = 1)
        putBits(len = 2, value = diff.level)
    } else if (diff.x in -1..1 && diff.y in -1..1) {
        putBits(len = 2, value = 2)
        putBits(len = 2, value = diff.level)
        putBits(len = 3, value = get3BitDirection(diff.x, diff.y))
    } else {
        putBits(len = 2, value = 3)
        putBits(len = 2, value = diff.level)
        putBits(len = 8, value = diff.x and 0xFF)
        putBits(len = 8, value = diff.y and 0xFF)
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
    check(dx != 0 || dy != 0)
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
    check(dx != 0 || dy != 0)
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
