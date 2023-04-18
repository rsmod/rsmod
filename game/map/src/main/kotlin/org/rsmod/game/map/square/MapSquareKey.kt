package org.rsmod.game.map.square

import org.rsmod.game.map.Coordinates

@JvmInline
public value class MapSquareKey(public val id: Int) {

    public val x: Int get() = (id shr X_BIT_OFFSET) and X_BIT_MASK

    public val z: Int get() = (id shr Z_BIT_OFFSET) and Z_BIT_MASK

    public constructor(x: Int, z: Int) : this(pack(x, z))

    public fun toCoords(level: Int): Coordinates = Coordinates(
        x = x * MapSquare.SIZE,
        z = z * MapSquare.SIZE,
        level = level
    )

    public operator fun component1(): Int = x

    public operator fun component2(): Int = z

    public override fun toString(): String {
        return "MapSquareKey(id=$id, x=$x, z=$z)"
    }

    @Suppress("MemberVisibilityCanBePrivate")
    public companion object {

        public const val X_BIT_COUNT: Int = 8
        public const val X_BIT_MASK: Int = (1 shl X_BIT_COUNT) - 1

        public const val Z_BIT_COUNT: Int = 8
        public const val Z_BIT_MASK: Int = (1 shl Z_BIT_COUNT) - 1

        public const val Z_BIT_OFFSET: Int = 0
        public const val X_BIT_OFFSET: Int = Z_BIT_COUNT

        private fun pack(x: Int, z: Int): Int {
            if (x !in 0..X_BIT_MASK) {
                throw IllegalArgumentException("`x` value must be within range [0..$X_BIT_MASK].")
            } else if (z !in 0..Z_BIT_MASK) {
                throw IllegalArgumentException("`z` value must be within range [0..$Z_BIT_MASK].")
            }
            return ((x and X_BIT_MASK) shl X_BIT_OFFSET) or
                ((z and Z_BIT_MASK) shl Z_BIT_OFFSET)
        }
    }
}
