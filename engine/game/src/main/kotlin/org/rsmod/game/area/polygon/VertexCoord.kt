package org.rsmod.game.area.polygon

import org.rsmod.map.CoordGrid
import org.rsmod.map.square.MapSquareGrid
import org.rsmod.map.square.MapSquareKey

/**
 * Represents the absolute coordinates of a polygon vertex. This is similar to [CoordGrid], but
 * omits the `level` value; levels are dependent on the parent polygon.
 *
 * Example:
 * ```
 * // Equivalent to CoordGrid(0, 50, 50, 2, 5), or in absolute values X: 3202 and Z: 3205.
 * val vertex = VertexCoord(mx = 50, mz = 50, lx = 2, lz = 5)
 * ```
 *
 * @param mx The map square x coordinate.
 * @param mz The map square z coordinate.
 * @param lx The local x coordinate within the map square `[0-63]`.
 * @param lz The local z coordinate within the map square `[0-63]`.
 */
@JvmInline
public value class VertexCoord(public val packed: Int) {
    public val x: Int
        get() = (packed shr X_BIT_OFFSET) and X_BIT_MASK

    public val z: Int
        get() = (packed shr Z_BIT_OFFSET) and Z_BIT_MASK

    public val mx: Int
        get() = x / MapSquareGrid.LENGTH

    public val mz: Int
        get() = z / MapSquareGrid.LENGTH

    public val lx: Int
        get() = x % MapSquareGrid.LENGTH

    public val lz: Int
        get() = z % MapSquareGrid.LENGTH

    public constructor(mx: Int, mz: Int, lx: Int, lz: Int) : this(pack(mx, mz, lx, lz))

    private constructor(x: Int, z: Int) : this(pack(x, z))

    public fun translate(xOffset: Int, zOffset: Int): VertexCoord =
        VertexCoord(x = x + xOffset, z = z + zOffset)

    public fun toCoords(): CoordGrid = CoordGrid(x = x, z = z)

    public fun mapSquareKey(): Int =
        ((x / MapSquareGrid.LENGTH) and MapSquareKey.X_BIT_MASK shl MapSquareKey.X_BIT_OFFSET) or
            ((z / MapSquareGrid.LENGTH) and MapSquareKey.Z_BIT_MASK shl MapSquareKey.Z_BIT_OFFSET)

    public override fun toString(): String = "VertexCoord(${mx}_${mz}_${lx}_${lz})"

    public companion object {
        public const val Z_BIT_COUNT: Int = 14
        public const val X_BIT_COUNT: Int = 14

        public const val Z_BIT_OFFSET: Int = 0
        public const val X_BIT_OFFSET: Int = Z_BIT_OFFSET + Z_BIT_COUNT

        public const val Z_BIT_MASK: Int = (1 shl Z_BIT_COUNT) - 1
        public const val X_BIT_MASK: Int = (1 shl X_BIT_COUNT) - 1

        public fun from(coords: CoordGrid): VertexCoord {
            require(coords.level == 0) { "Cannot specify non-zero coord level. (coords=$coords)" }
            return VertexCoord(coords.mx, coords.mz, coords.lx, coords.lz)
        }

        private fun pack(x: Int, z: Int): Int {
            require(x in 0..X_BIT_MASK) {
                "`x` value must be within range [0..$X_BIT_MASK]. (x=$x)"
            }
            require(z in 0..Z_BIT_MASK) {
                "`z` value must be within range [0..$Z_BIT_MASK]. (z=$z)"
            }
            return ((x and X_BIT_MASK) shl X_BIT_OFFSET) or ((z and Z_BIT_MASK) shl Z_BIT_OFFSET)
        }

        private fun pack(mx: Int, mz: Int, lx: Int, lz: Int): Int {
            val key = MapSquareKey(mx, mz)
            val grid = MapSquareGrid(lx, lz)
            return pack(
                x = key.x * MapSquareGrid.LENGTH + grid.x,
                z = key.z * MapSquareGrid.LENGTH + grid.z,
            )
        }
    }
}
