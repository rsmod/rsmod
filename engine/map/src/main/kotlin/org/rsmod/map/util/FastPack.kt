package org.rsmod.map.util

import org.rsmod.map.CoordGrid
import org.rsmod.map.square.MapSquareGrid
import org.rsmod.map.square.MapSquareKey
import org.rsmod.map.zone.ZoneGrid
import org.rsmod.map.zone.ZoneKey

private typealias MsKey = MapSquareKey

public object FastPack {
    /**
     * Fast path for obtaining the packed zone key from a [CoordGrid], bypassing validation checks.
     *
     * This function assumes that the provided [CoordGrid] is already valid and trusted, and it
     * omits any runtime bounds checking for performance reasons. It is intended for use only in
     * places where coordinate integrity is guaranteed.
     *
     * Most callers should prefer [ZoneKey.from], which provides safer construction with validation.
     * Only use this method in performance-critical sections where the inputs are known to be
     * correct.
     *
     * @return the [ZoneKey.packed] integer representing the corresponding [ZoneKey].
     */
    public fun zoneKey(coords: CoordGrid): Int =
        (((coords.x / ZoneGrid.LENGTH) and ZoneKey.X_BIT_MASK) shl ZoneKey.X_BIT_OFFSET) or
            (((coords.z / ZoneGrid.LENGTH) and ZoneKey.Z_BIT_MASK) shl ZoneKey.Z_BIT_OFFSET) or
            ((coords.level and ZoneKey.LEVEL_BIT_MASK) shl ZoneKey.LEVEL_BIT_OFFSET)

    /**
     * Fast path for obtaining the packed map square key from a [CoordGrid], bypassing validation
     * checks.
     *
     * This function assumes that the provided [CoordGrid] is already valid and trusted, and it
     * omits any runtime bounds checking for performance reasons. It is intended for use only in
     * places where coordinate integrity is guaranteed.
     *
     * Most callers should prefer [MapSquareKey.from], which provides safer construction with
     * validation. Only use this method in performance-critical sections where the inputs are known
     * to be correct.
     *
     * @return the [MapSquareKey.id] integer representing the corresponding [MapSquareKey].
     */
    public fun mapSquareKey(coords: CoordGrid): Int =
        ((coords.x / MapSquareGrid.LENGTH) and MsKey.X_BIT_MASK shl MsKey.X_BIT_OFFSET) or
            ((coords.z / MapSquareGrid.LENGTH) and MsKey.Z_BIT_MASK shl MsKey.Z_BIT_OFFSET)
}
