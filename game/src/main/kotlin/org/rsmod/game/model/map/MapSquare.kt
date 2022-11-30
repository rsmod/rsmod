package org.rsmod.game.model.map

@JvmInline
public value class MapSquare(public val id: Int) {

    public val x: Int get() = id shr 8

    public val y: Int get() = id and 0xFF

    public constructor(x: Int, y: Int) : this((x shl 8) or y)

    public fun translate(xOffset: Int, yOffset: Int): MapSquare = MapSquare(
        x = x + xOffset,
        y = y + yOffset
    )

    public fun translateX(offset: Int): MapSquare = translate(offset, 0)

    public fun translateY(offset: Int): MapSquare = translate(0, offset)

    public fun toCoords(level: Int): Coordinates = Coordinates(
        x = x * SIZE,
        y = y * SIZE,
        level = level
    )

    public fun toZone(level: Int): Zone = Zone(
        x = x * (SIZE / Zone.SIZE),
        y = y * (SIZE / Zone.SIZE),
        level = level
    )

    public operator fun component1(): Int = x

    public operator fun component2(): Int = y

    public operator fun minus(other: MapSquare): MapSquare {
        return translate(-other.x, -other.y)
    }

    public operator fun plus(other: MapSquare): MapSquare {
        return translate(other.x, other.y)
    }

    public companion object {
        public const val SIZE: Int = 64
        public val ZERO: MapSquare = MapSquare(0)
    }
}
