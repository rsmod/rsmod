package org.rsmod.game.pathfinder.collision

@Suppress("NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")
public class CollisionFlagMap(private val flags: Array<IntArray?> = arrayOfNulls(TOTAL_ZONES)) {

    public fun getOrAlloc(x: Int, y: Int, level: Int): IntArray {
        val zoneIndex = coordsToZoneIndex(x, y, level)
        val curr = flags[zoneIndex]
        if (curr != null) return curr
        val alloc = IntArray(ZONE_SIZE)
        flags[zoneIndex] = alloc
        return alloc
    }

    /**
     * Deallocates the whole 8x8 zone to which [x], [y] and [level] belong.
     */
    public fun deallocate(x: Int, y: Int, level: Int) {
        val zoneIndex = coordsToZoneIndex(x, y, level)
        flags[zoneIndex] = null
    }

    public fun add(x: Int, y: Int, level: Int, mask: Int) {
        val old = this[x, y, level]
        this[x, y, level] = old or mask
    }

    public fun remove(x: Int, y: Int, level: Int, mask: Int) {
        val old = this[x, y, level]
        this[x, y, level] = old and mask.inv()
    }

    public operator fun get(x: Int, y: Int, level: Int): Int {
        val zoneIndex = coordsToZoneIndex(x, y, level)
        val localIndex = coordsToZoneLocalIndex(x, y)
        return flags[zoneIndex]?.get(localIndex) ?: 0
    }

    public operator fun set(x: Int, y: Int, level: Int, flags: Int) {
        val zoneFlags = getOrAlloc(x, y, level)
        val localIndex = coordsToZoneLocalIndex(x, y)
        zoneFlags[localIndex] = flags
    }

    public companion object {

        public const val TOTAL_ZONES: Int = 2048 * 2048 * 4

        public const val ZONE_SIZE: Int = 8 * 8

        private inline fun coordsToZoneLocalIndex(x: Int, y: Int): Int = (x and 0x7) or ((y and 0x7) shl 3)

        private inline fun coordsToZoneIndex(x: Int, y: Int, level: Int): Int {
            return (x shr 3 and 0x7FF) or ((y shr 3 and 0x7FF) shl 11) or ((level and 0x3) shl 22)
        }
    }
}
