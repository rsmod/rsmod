@file:Suppress("UnusedImport")

package org.rsmod.game.region.zone

import org.rsmod.game.region.util.NormalRegionZoneMap.NormalZone

/**
 * Represents a unique flag that can be assigned to a [RegionZoneCopy].
 *
 * Each `RegionZoneCopy` can store a **single** unique flag, allowing differentiation between
 * identical copied zones that serve different purposes.
 *
 * These flags are particularly useful in scenarios where multiple instances of the same copied zone
 * exist but need to be treated differently by content.
 *
 * For example, a minigame home base may copy the same zone for both a red team and a blue team, but
 * in separate map sections. Flags allow distinguishing between them.
 *
 * The total number of available flags is limited by [RegionZoneCopy.UNIQUE_FLAG_BIT_COUNT] and
 * [NormalZone.UNIQUE_FLAG_BIT_COUNT]. Currently, up to `2^3` (8) unique flags are supported.
 */
public enum class RegionZoneFlag(private val bit: Int) {
    Flag1(bit = 0),
    Flag2(bit = 1),
    Flag3(bit = 2),
    Flag4(bit = 3),
    Flag5(bit = 4),
    Flag6(bit = 5),
    Flag7(bit = 6),
    Flag8(bit = 7);

    public val bitmask: Int
        get() = 1 shl bit
}
