package org.rsmod.map.zone

import org.rsmod.map.CoordGrid

/**
 * [ZoneKey] serves as a unique identifier for individual zones within the game world. A zone
 * represents a specific 8x8x1 area within the map, making it smaller and more granular compared to
 * a map square.
 *
 * This class uses a compact integer representation [packed] to encode the [x], [z], and [level]
 * coordinates of a zone. Unlike [org.rsmod.map.square.MapSquareKey], which identifies an entire map
 * square across all levels (0-3), [ZoneKey] includes the specific level in its identification,
 * allowing more precise localization.
 *
 * ### Example Usage:
 * Given absolute coordinates (3220, 3205, 2) within the game world, you can convert them to a
 * [ZoneKey] by calculating their corresponding zone coordinates and level:
 * ```
 * - Absolute Coordinates: 3220, 3205, 2
 * - ZoneKey:               402,  400, 2
 * ```
 *
 * The [ZoneKey] allows for efficient indexing and retrieval of zones, supporting operations that
 * need to account for both the position on the map and the level of elevation within the game.
 *
 * @property packed The packed integer representation of the zone's coordinates and level.
 * @property x The X coordinate of the zone, ranging from 0 to [CoordGrid.MAP_WIDTH] /
 *   [ZoneGrid.LENGTH].
 * @property z The Z coordinate of the zone, ranging from 0 to [CoordGrid.MAP_LENGTH] /
 *   [ZoneGrid.LENGTH].
 * @property level The level or height coordinate of the zone, ranging from 0 to 3.
 * @constructor Creates a [ZoneKey] instance using local [x], [z], and [level] zone coordinates. To
 *   convert from absolute coordinates, use [fromAbsolute] or [from].
 */
@JvmInline
public value class ZoneKey(public val packed: Int) {
    public val x: Int
        get() = (packed shr X_BIT_OFFSET) and X_BIT_MASK

    public val z: Int
        get() = (packed shr Z_BIT_OFFSET) and Z_BIT_MASK

    public val level: Int
        get() = (packed shr LEVEL_BIT_OFFSET) and LEVEL_BIT_MASK

    public constructor(x: Int, z: Int, level: Int) : this(pack(x, z, level))

    public fun translate(xOffset: Int, zOffset: Int, levelOffset: Int = 0): ZoneKey =
        ZoneKey(x = x + xOffset, z = z + zOffset, level = level + levelOffset)

    public fun translateX(offset: Int): ZoneKey = translate(offset, 0, 0)

    public fun translateZ(offset: Int): ZoneKey = translate(0, offset, 0)

    public fun translateLevel(offset: Int): ZoneKey = translate(0, 0, offset)

    public fun toCoords(): CoordGrid =
        CoordGrid(x = x * ZoneGrid.LENGTH, z = z * ZoneGrid.LENGTH, level = level)

    public operator fun component1(): Int = x

    public operator fun component2(): Int = z

    public operator fun component3(): Int = level

    public override fun toString(): String = "ZoneKey(x=$x, z=$z, level=$level)"

    public companion object {
        public val NULL: ZoneKey = ZoneKey(-1)

        public const val Z_BIT_COUNT: Int = 11
        public const val X_BIT_COUNT: Int = 11
        public const val LEVEL_BIT_COUNT: Int = 2

        public const val Z_BIT_OFFSET: Int = 0
        public const val X_BIT_OFFSET: Int = Z_BIT_OFFSET + Z_BIT_COUNT
        public const val LEVEL_BIT_OFFSET: Int = X_BIT_OFFSET + X_BIT_COUNT

        public const val Z_BIT_MASK: Int = (1 shl Z_BIT_COUNT) - 1
        public const val X_BIT_MASK: Int = (1 shl X_BIT_COUNT) - 1
        public const val LEVEL_BIT_MASK: Int = (1 shl LEVEL_BIT_COUNT) - 1

        public fun fromAbsolute(x: Int, z: Int, level: Int): ZoneKey =
            ZoneKey(x = x / ZoneGrid.LENGTH, z = z / ZoneGrid.LENGTH, level = level)

        public fun from(coords: CoordGrid): ZoneKey =
            fromAbsolute(x = coords.x, z = coords.z, level = coords.level)

        private fun pack(x: Int, z: Int, level: Int): Int {
            require(x in 0..X_BIT_MASK) { "`x` value must be within range [0..$X_BIT_MASK]." }
            require(z in 0..Z_BIT_MASK) { "`z` value must be within range [0..$Z_BIT_MASK]." }
            require(level in 0..LEVEL_BIT_MASK) {
                "`level` value must be within range [0..$LEVEL_BIT_MASK]."
            }
            return ((x and X_BIT_MASK) shl X_BIT_OFFSET) or
                ((z and Z_BIT_MASK) shl Z_BIT_OFFSET) or
                ((level and LEVEL_BIT_MASK) shl LEVEL_BIT_OFFSET)
        }
    }
}
