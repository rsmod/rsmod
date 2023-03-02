package org.rsmod.game.model.map

@JvmInline
public value class MapSquareKey(public val id: Int) {

    public val x: Int get() = id shr 8

    public val y: Int get() = id and 0xFF

    public constructor(x: Int, y: Int) : this((x shl 8) or y)

    public fun translate(xOffset: Int, yOffset: Int): MapSquareKey = MapSquareKey(
        x = x + xOffset,
        y = y + yOffset
    )

    public fun translateX(offset: Int): MapSquareKey = translate(offset, 0)

    public fun translateY(offset: Int): MapSquareKey = translate(0, offset)

    public fun toCoords(level: Int): Coordinates = Coordinates(
        x = x * SIZE,
        y = y * SIZE,
        level = level
    )

    public fun toZoneKey(level: Int): ZoneKey = ZoneKey(
        x = x * (SIZE / ZoneKey.SIZE),
        y = y * (SIZE / ZoneKey.SIZE),
        level = level
    )

    public operator fun component1(): Int = x

    public operator fun component2(): Int = y

    public operator fun minus(other: MapSquareKey): MapSquareKey {
        return translate(-other.x, -other.y)
    }

    public operator fun plus(other: MapSquareKey): MapSquareKey {
        return translate(other.x, other.y)
    }

    public override fun toString(): String {
        return "MapSquareKey(x=$x, y=$y)"
    }

    public companion object {

        public val ZERO: MapSquareKey = MapSquareKey(0)

        public const val SIZE: Int = 64
    }
}
