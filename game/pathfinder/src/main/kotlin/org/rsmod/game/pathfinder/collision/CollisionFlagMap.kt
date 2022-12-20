package org.rsmod.game.pathfinder.collision

public class CollisionFlagMap(
    private val tiles: MutableMap<Int, Int> = mutableMapOf()
) {

    public fun add(x: Int, y: Int, level: Int, mask: Int) {
        val old = this[x, y, level] ?: 0
        this[x, y, level] = old or mask
    }

    public fun remove(x: Int, y: Int, level: Int, mask: Int) {
        val old = this[x, y, level] ?: 0
        this[x, y, level] = old and mask.inv()
    }

    public operator fun get(x: Int, y: Int, level: Int): Int {
        val packed = asPackedCoords(x, y, level)
        return tiles[packed] ?: 0
    }

    public operator fun set(x: Int, y: Int, level: Int, flags: Int) {
        val packed = asPackedCoords(x, y, level)
        tiles[packed] = flags
    }

    public companion object {

        @Suppress("NOTHING_TO_INLINE")
        public inline fun asPackedCoords(x: Int, y: Int, level: Int): Int {
            return (x and 0x7FFF) or ((y and 0x7FFF) shl 15) or ((level and 0x3) shl 30)
        }
    }
}
