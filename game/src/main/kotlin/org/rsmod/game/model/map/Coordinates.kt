package org.rsmod.game.model.map

@JvmInline
public value class Coordinates(public val packed: Int) {

    public val x: Int get() = packed and 0x7FFF

    public val y: Int get() = (packed shr 15) and 0x7FFF

    public val level: Int get() = (packed shr 30) and 0x3

    public val packed30Bits: Int
        get() = (y and 0x3FFF) or ((x and 0x3FFF) shl 14) or ((level and 0x3) shl 28)

    public val packed18Bits: Int
        get() = (y shr 13) or ((x shr 13) shl 8) or ((level and 0x3) shl 16)

    public constructor(x: Int, y: Int, level: Int = 0) : this(
        (x and 0x7FFF) or ((y and 0x7FFF) shl 15) or ((level and 0x3) shl 30)
    )

    public fun translate(xOffset: Int, yOffset: Int, levelOffset: Int = 0): Coordinates = Coordinates(
        x = x + xOffset,
        y = y + yOffset,
        level = level + levelOffset
    )

    public fun translateX(offset: Int): Coordinates = translate(offset, 0, 0)

    public fun translateY(offset: Int): Coordinates = translate(0, offset, 0)

    public fun translateLevel(offset: Int): Coordinates = translate(0, 0, offset)

    public fun toZone(): Zone = Zone(
        x = x / Zone.SIZE,
        y = y / Zone.SIZE
    )

    public fun toMapSquare(): MapSquare = MapSquare(
        x = x / MapSquare.SIZE,
        y = y / MapSquare.SIZE
    )

    public operator fun component1(): Int = x

    public operator fun component2(): Int = y

    public operator fun component3(): Int = level

    public operator fun minus(other: Coordinates): Coordinates {
        return translate(-other.x, -other.y)
    }

    public operator fun plus(other: Coordinates): Coordinates {
        return translate(other.x, other.y)
    }

    public companion object {

        public val ZERO: Coordinates = Coordinates(0)

        public const val MAX_XY: Int = (1 shl 15) - 1
        public const val MAX_LEVEL: Int = (1 shl 2) - 1
    }
}
