package org.rsmod.game.map.zone

import org.rsmod.game.map.Coordinates
import org.rsmod.game.map.square.MapSquareKey

@JvmInline
public value class ZoneKey(public val packed: Int) {

    public val x: Int get() = (packed shr X_BIT_OFFSET) and X_BIT_MASK

    public val z: Int get() = (packed shr Z_BIT_OFFSET) and Z_BIT_MASK

    public val level: Int get() = (packed shr LEVEL_BIT_OFFSET) and LEVEL_BIT_MASK

    public constructor(x: Int, z: Int, level: Int) : this(pack(x, z, level))

    public fun translate(xOffset: Int, zOffset: Int, levelOffset: Int = 0): ZoneKey = ZoneKey(
        x = x + xOffset,
        z = z + zOffset,
        level = level + levelOffset
    )

    public fun toCoords(): Coordinates = Coordinates(
        x = x * Zone.SIZE,
        z = z * Zone.SIZE,
        level = level
    )

    public fun toViewport(zoneRadius: Int): List<MapSquareKey> {
        val lx = (x - zoneRadius) / Zone.SIZE
        val lz = (z - zoneRadius) / Zone.SIZE
        val rx = (x + zoneRadius) / Zone.SIZE
        val rz = (z + zoneRadius) / Zone.SIZE
        val viewport = mutableListOf<MapSquareKey>()
        for (mx in lx..rx) {
            for (mz in lz..rz) {
                val key = MapSquareKey(mx, mz)
                viewport += key
            }
        }
        return viewport
    }

    public operator fun component1(): Int = x

    public operator fun component2(): Int = z

    public operator fun component3(): Int = level

    public override fun toString(): String {
        return "ZoneKey(x=$x, z=$z, level=$level)"
    }

    public companion object {

        public const val X_BIT_COUNT: Int = 11
        public const val X_BIT_MASK: Int = (1 shl X_BIT_COUNT) - 1

        public const val Z_BIT_COUNT: Int = 11
        public const val Z_BIT_MASK: Int = (1 shl Z_BIT_COUNT) - 1

        public const val LEVEL_BIT_COUNT: Int = 2
        public const val LEVEL_BIT_MASK: Int = (1 shl LEVEL_BIT_COUNT) - 1

        public const val Z_BIT_OFFSET: Int = 0
        public const val X_BIT_OFFSET: Int = Z_BIT_COUNT
        public const val LEVEL_BIT_OFFSET: Int = Z_BIT_COUNT + X_BIT_COUNT

        public fun from(coords: Coordinates): ZoneKey = ZoneKey(
            x = coords.x / Zone.SIZE,
            z = coords.z / Zone.SIZE,
            level = coords.level
        )

        private fun pack(x: Int, z: Int, level: Int): Int {
            if (x !in 0..X_BIT_MASK) {
                throw IllegalArgumentException("`x` value must be within range [0..$X_BIT_MASK].")
            } else if (z !in 0..Z_BIT_MASK) {
                throw IllegalArgumentException("`z` value must be within range [0..$Z_BIT_MASK].")
            } else if (level !in 0..LEVEL_BIT_MASK) {
                throw IllegalArgumentException("`level` value must be within range [0..$LEVEL_BIT_MASK].")
            }
            return ((x and X_BIT_MASK) shl X_BIT_OFFSET) or
                ((z and Z_BIT_MASK) shl Z_BIT_OFFSET) or
                ((level and LEVEL_BIT_MASK) shl LEVEL_BIT_OFFSET)
        }
    }
}
