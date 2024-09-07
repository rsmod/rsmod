package org.rsmod.map.util

import kotlin.math.max
import kotlin.math.min
import org.rsmod.map.CoordGrid

/**
 * [Bounds] represents a rectangular area within a 3D coordinate grid in the game world, defined by
 * its base coordinates (x, z, level), width, and length. This class allows for compact storage and
 * efficient calculations involving rectangular areas on a specific level of the game map.
 *
 * The coordinates and dimensions of the bounds are packed into a single [Long] value for
 * efficiency. The packed value encodes the base [x], [z], and [level] coordinates, as well as the
 * [width] and [length] of the rectangle.
 *
 * All operations in this class consider both the position and size of the [Bounds]. This means that
 * functions like [isWithinDistance] and [distanceTo] calculate distances based on the dimensions
 * and coordinates of both [Bounds] instances, accounting for their full area rather than just their
 * origin points.
 *
 * ### Example Usage:
 * ```
 * val bounds1 = Bounds(x = 3200, z = 3200, level = 0, width = 2, length = 3)
 * val bounds2 = Bounds(x = 3205, z = 3207, level = 0, width = 3, length = 1)
 *
 * // Check if `bounds1` is within a distance of 5 tiles from `bounds2`
 * val isWithin = bounds1.isWithinDistance(bounds2, 5)
 * println(isWithin)  // Output: true
 *
 * // Calculate the maximum distance, in tiles, between `bounds1` and `bounds2`
 * val distance = bounds1.distanceTo(bounds2)
 * println(distance)  // Output: 5
 * ```
 *
 * The [Bounds] class provides utility methods to calculate distances, check for containment, and
 * iterate over the area defined by the bounds.
 *
 * @property packed The packed long representation of the bounds' base coordinates and dimensions.
 * @property x The X coordinate of the lower-left corner of the bounds.
 * @property z The Z coordinate of the lower-left corner of the bounds.
 * @property level The level or height coordinate of the bounds.
 * @property width The inclusive width of the bounds, extending from the base [x] coordinate.
 * @property length The inclusive length of the bounds, extending from the base [z] coordinate.
 * @property coords The base [CoordGrid] of the bounds. (lower-left corner)
 * @constructor Creates a [Bounds] instance using base coordinates ([x], [z], [level]), [width], and
 *   [length].
 */
@JvmInline
public value class Bounds(public val packed: Long) {
    public val x: Int
        get() = ((packed shr X_BIT_OFFSET) and X_BIT_MASK).toInt()

    public val z: Int
        get() = ((packed shr Z_BIT_OFFSET) and Z_BIT_MASK).toInt()

    public val level: Int
        get() = ((packed shr LEVEL_BIT_OFFSET) and LEVEL_BIT_MASK).toInt()

    public val width: Int
        get() = ((packed shr WIDTH_BIT_OFFSET) and WIDTH_BIT_MASK).toInt()

    public val length: Int
        get() = ((packed shr LENGTH_BIT_OFFSET) and LENGTH_BIT_MASK).toInt()

    public val coords: CoordGrid
        get() = CoordGrid(x, z, level)

    /**
     * The X coordinate of the centre of the bounds, as a double for precision.
     *
     * This is useful for angle calculations between two [Bounds] objects, particularly when the
     * width of either bounds is an odd number, resulting in a non-integer centre that can't be
     * accurately represented by a whole integer.
     */
    public val fineCentreX: Double
        get() = x + (width / 2.0)

    /**
     * The Z coordinate of the centre of the bounds, as a double for precision.
     *
     * Like [fineCentreX], this is useful for accurate angle calculations when the length of either
     * bounds is an odd number, providing a precise centre coordinate that cannot be represented by
     * a whole integer.
     */
    public val fineCentreZ: Double
        get() = z + (length / 2.0)

    public constructor(
        x: Int,
        z: Int,
        level: Int,
        width: Int,
        length: Int,
    ) : this(pack(x = x, z = z, level = level, width = width, length = length))

    public constructor(
        x: Int,
        z: Int,
        level: Int,
        size: Int,
    ) : this(pack(x = x, z = z, level = level, width = size, length = size))

    public constructor(
        coords: CoordGrid,
        width: Int,
        length: Int,
    ) : this(x = coords.x, z = coords.z, level = coords.level, width = width, length = length)

    public constructor(
        coords: CoordGrid,
        size: Int = 1,
    ) : this(x = coords.x, z = coords.z, level = coords.level, width = size, length = size)

    public fun distanceTo(other: Bounds): Int {
        require(level == other.level) { "Bounds must be on same level. (a=$this, b=$other)" }
        return maxDistanceBetween(this, other)
    }

    public fun isWithinDistance(other: Bounds, distance: Int): Boolean {
        require(level == other.level) { "Bounds must be on same level. (a=$this, b=$other)" }
        return areWithinDistance(this, other, distance)
    }

    public operator fun iterator(): Iterator<CoordGrid> = BoundsIterator(this)

    override fun toString(): String = "Bounds(base=$coords, width=$width, length=$length)"

    public companion object {
        public fun areWithinDistance(a: Bounds, b: Bounds, distance: Int): Boolean {
            // As a free micro-optimization we can return early if distance is negative.
            // Due to this, we don't include a range check at the bottom to check that
            // the difference in coordinates is between 0 to distance.
            if (distance < 0) {
                return false
            }
            val xDistance = xDistanceBetween(a, b)
            if (xDistance > distance) {
                return false
            }
            val zDistance = zDistanceBetween(a, b)
            return zDistance <= distance
        }

        public fun xDistanceBetween(a: Bounds, b: Bounds): Int {
            val minX = minX(a, b)
            val maxX = maxX(a, b)
            val expandedMinX = minX.x + minX.width - 1
            return max(0, maxX.x - expandedMinX)
        }

        public fun zDistanceBetween(a: Bounds, b: Bounds): Int {
            val minZ = minZ(a, b)
            val maxZ = maxZ(a, b)
            val expandedMinZ = minZ.z + minZ.length - 1
            return max(0, maxZ.z - expandedMinZ)
        }

        public fun minDistanceBetween(a: Bounds, b: Bounds): Int =
            min(xDistanceBetween(a, b), zDistanceBetween(a, b))

        public fun maxDistanceBetween(a: Bounds, b: Bounds): Int =
            max(xDistanceBetween(a, b), zDistanceBetween(a, b))

        public fun minX(a: Bounds, b: Bounds): Bounds = if (a.x < b.x) a else b

        public fun minZ(a: Bounds, b: Bounds): Bounds = if (a.z < b.z) a else b

        public fun maxX(a: Bounds, b: Bounds): Bounds = if (a.x > b.x) a else b

        public fun maxZ(a: Bounds, b: Bounds): Bounds = if (a.z > b.z) a else b

        /* Bit-packing section */

        private const val Z_BIT_COUNT: Int = 14
        private const val X_BIT_COUNT: Int = 14
        private const val LEVEL_BIT_COUNT: Int = 2
        private const val WIDTH_BIT_COUNT: Int = 16
        private const val LENGTH_BIT_COUNT: Int = 16

        private const val Z_BIT_OFFSET: Int = 0
        private const val X_BIT_OFFSET: Int = Z_BIT_OFFSET + Z_BIT_COUNT
        private const val LEVEL_BIT_OFFSET: Int = X_BIT_OFFSET + X_BIT_COUNT
        private const val WIDTH_BIT_OFFSET: Int = LEVEL_BIT_OFFSET + LEVEL_BIT_COUNT
        private const val LENGTH_BIT_OFFSET: Int = WIDTH_BIT_OFFSET + WIDTH_BIT_COUNT

        private const val Z_BIT_MASK: Long = (1L shl Z_BIT_COUNT) - 1
        private const val X_BIT_MASK: Long = (1L shl X_BIT_COUNT) - 1
        private const val LEVEL_BIT_MASK: Long = (1L shl LEVEL_BIT_COUNT) - 1
        private const val WIDTH_BIT_MASK: Long = (1L shl WIDTH_BIT_COUNT) - 1
        private const val LENGTH_BIT_MASK: Long = (1L shl LENGTH_BIT_COUNT) - 1

        private fun pack(x: Int, z: Int, level: Int, width: Int, length: Int): Long {
            require(x in 0..X_BIT_MASK) { "`x` value must be within range [0..$X_BIT_MASK]." }
            require(z in 0..Z_BIT_MASK) { "`z` value must be within range [0..$Z_BIT_MASK]." }
            require(level in 0..LEVEL_BIT_MASK) {
                "`level` value must be within range [0..$LEVEL_BIT_MASK]."
            }
            require(width in 0..WIDTH_BIT_MASK) {
                "`width` value must be within range [0..$WIDTH_BIT_MASK]."
            }
            require(length in 0..LENGTH_BIT_MASK) {
                "`length` value must be within range [0..$LENGTH_BIT_MASK]."
            }
            val packed =
                ((x.toLong() and X_BIT_MASK) shl X_BIT_OFFSET) or
                    ((z.toLong() and Z_BIT_MASK) shl Z_BIT_OFFSET) or
                    ((level.toLong() and LEVEL_BIT_MASK) shl LEVEL_BIT_OFFSET) or
                    ((width.toLong() and WIDTH_BIT_MASK) shl WIDTH_BIT_OFFSET) or
                    ((length.toLong() and LENGTH_BIT_MASK) shl LENGTH_BIT_OFFSET)
            return packed
        }
    }
}

private class BoundsIterator(private val bounds: Bounds) : Iterator<CoordGrid> {
    private var currentX = bounds.x
    private var currentZ = bounds.z

    override fun hasNext(): Boolean = currentZ < bounds.z + bounds.length

    override fun next(): CoordGrid {
        if (!hasNext()) {
            throw NoSuchElementException()
        }
        val coords = CoordGrid(currentX, currentZ, bounds.level)
        if (currentX + 1 < bounds.x + bounds.width) {
            currentX++
        } else {
            currentX = bounds.x
            currentZ++
        }
        return coords
    }
}
