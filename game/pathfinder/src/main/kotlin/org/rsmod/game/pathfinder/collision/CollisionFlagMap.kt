package org.rsmod.game.pathfinder.collision

@Suppress("MemberVisibilityCanBePrivate")
public class CollisionFlagMap(private val flags: Array<IntArray?> = arrayOfNulls(TOTAL_ZONES)) {

    public fun getOrAlloc(x: Int, z: Int, level: Int): IntArray {
        val zoneIndex = coordsToZoneIndex(x, z, level)
        val curr = flags[zoneIndex]
        if (curr != null) return curr
        val alloc = IntArray(ZONE_SIZE)
        flags[zoneIndex] = alloc
        return alloc
    }

    public fun allocateIfAbsent(x: Int, z: Int, level: Int) {
        val zoneIndex = coordsToZoneIndex(x, z, level)
        if (flags[zoneIndex] != null) return
        flags[zoneIndex] = IntArray(ZONE_SIZE)
    }

    /**
     * Deallocates the whole 8x8 zone to which [x], [z] and [level] belong.
     */
    public fun deallocate(x: Int, z: Int, level: Int) {
        val zoneIndex = coordsToZoneIndex(x, z, level)
        flags[zoneIndex] = null
    }

    public fun add(x: Int, z: Int, level: Int, mask: Int) {
        val old = this[x, z, level]
        this[x, z, level] = old or mask
    }

    public fun remove(x: Int, z: Int, level: Int, mask: Int) {
        val old = this[x, z, level]
        this[x, z, level] = old and mask.inv()
    }

    public fun allocateAndGet(x: Int, z: Int, level: Int): Int {
        allocateIfAbsent(x, z, level)
        return this[x, z, level]
    }

    public operator fun get(x: Int, z: Int, level: Int): Int {
        val zoneIndex = coordsToZoneIndex(x, z, level)
        val localIndex = coordsToZoneLocalIndex(x, z)
        return flags[zoneIndex]?.get(localIndex) ?: DEFAULT_FLAG
    }

    public operator fun set(x: Int, z: Int, level: Int, flags: Int) {
        val zoneFlags = getOrAlloc(x, z, level)
        val localIndex = coordsToZoneLocalIndex(x, z)
        zoneFlags[localIndex] = flags
    }

    public fun defaultFlag(): Int {
        return DEFAULT_FLAG
    }

    public companion object {

        public const val TOTAL_ZONES: Int = 2048 * 2048 * 4

        public const val ZONE_SIZE: Int = 8 * 8

        public const val DEFAULT_FLAG: Int = -1

        private fun coordsToZoneLocalIndex(x: Int, z: Int): Int {
            return (x and 0x7) or ((z and 0x7) shl 3)
        }

        private fun coordsToZoneIndex(x: Int, z: Int, level: Int): Int {
            return (x shr 3 and 0x7FF) or ((z shr 3 and 0x7FF) shl 11) or ((level and 0x3) shl 22)
        }
    }
}
