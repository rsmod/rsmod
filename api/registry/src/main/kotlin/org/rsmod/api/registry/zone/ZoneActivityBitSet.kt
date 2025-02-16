package org.rsmod.api.registry.zone

import org.rsmod.map.zone.ZoneKey

public class ZonePlayerActivityBitSet {
    // Class was designed to be allocated once per level.
    private val level0: ZoneActivityBitSet = ZoneActivityBitSet()
    private val level1: ZoneActivityBitSet = ZoneActivityBitSet()
    private val level2: ZoneActivityBitSet = ZoneActivityBitSet()
    private val level3: ZoneActivityBitSet = ZoneActivityBitSet()

    private val all = listOf(level0, level1, level2, level3)

    public fun flag(zone: ZoneKey) {
        val bitSet = bitSet(zone)
        bitSet.flag(zone.x, zone.z)
    }

    public fun unflag(zone: ZoneKey) {
        val bitSet = bitSet(zone)
        bitSet.unflag(zone.x, zone.z)
    }

    public fun isFlagged(southWestZone: ZoneKey, northEastZone: ZoneKey, level: Int): Boolean {
        val bitSet = bitSet(level)
        return bitSet.isFlagged(southWestZone.x, southWestZone.z, northEastZone.x, northEastZone.z)
    }

    public fun isAnyLevelFlagged(southWestZone: ZoneKey, northEastZone: ZoneKey): Boolean {
        return all.any {
            it.isFlagged(southWestZone.x, southWestZone.z, northEastZone.x, northEastZone.z)
        }
    }

    private fun bitSet(zone: ZoneKey): ZoneActivityBitSet = bitSet(zone.level)

    private fun bitSet(level: Int): ZoneActivityBitSet =
        when (level) {
            0 -> level0
            1 -> level1
            2 -> level2
            3 -> level3
            else -> throw IllegalArgumentException("Invalid level for zone: $level")
        }
}

/** @author Kris | 02/03/2024 */
private class ZoneActivityBitSet {
    private val grid = IntArray(GRID_SIZE * (GRID_SIZE shr INT_BITS))

    fun flag(zoneX: Int, zoneZ: Int) {
        val index = index(zoneX, zoneZ)
        grid[index] = grid[index] or (1 shl (zoneZ and INT_BITS_FLAG))
    }

    fun unflag(zoneX: Int, zoneZ: Int) {
        val index = index(zoneX, zoneZ)
        grid[index] = grid[index] and (1 shl (zoneZ and INT_BITS_FLAG)).inv()
    }

    fun isFlagged(minZoneX: Int, minZoneZ: Int, maxZoneX: Int, maxZoneZ: Int): Boolean {
        val startY = minZoneZ and INT_BITS_FLAG.inv()
        val endY = maxZoneZ ushr INT_BITS shl INT_BITS
        var x = minZoneX
        var y: Int
        while (x <= maxZoneX) {
            y = startY
            while (y <= endY) {
                val index = index(x, y)
                val line = grid[index]
                val trailingTrimmed =
                    if (y + INT_BITS_FLAG > maxZoneZ) {
                        line and (1 shl (maxZoneZ - y + 1)).dec()
                    } else {
                        line
                    }
                val leadingTrimmed =
                    if (y < minZoneZ) {
                        trailingTrimmed ushr (minZoneZ - y)
                    } else {
                        trailingTrimmed
                    }
                if (leadingTrimmed != 0) {
                    return true
                }
                y += Int.SIZE_BITS
            }
            x++
        }
        return false
    }

    private companion object {
        private const val GRID_SIZE = 2048
        private const val INT_BITS = 5
        private const val INT_BITS_FLAG = (1 shl INT_BITS) - 1

        private fun index(zoneX: Int, zoneZ: Int): Int =
            (zoneX shl INT_BITS) or (zoneZ ushr INT_BITS)
    }
}
