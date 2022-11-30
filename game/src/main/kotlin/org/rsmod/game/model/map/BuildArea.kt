package org.rsmod.game.model.map

@JvmInline
public value class BuildArea(public val packed: Int) {

    public val x: Int get() = packed and 0xFFFF

    public val y: Int get() = (packed shr 16) and 0xFFFF

    public constructor(x: Int, y: Int) : this((x and 0xFFFF) or ((y and 0xFFFF) shl 16))

    public fun translate(xOffset: Int, yOffset: Int): BuildArea = BuildArea(
        x = x + xOffset,
        y = y + yOffset
    )

    public fun translateX(offset: Int): BuildArea = translate(offset, 0)

    public fun translateY(offset: Int): BuildArea = translate(0, offset)

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

    public fun toMapSquare(): MapSquare = MapSquare(
        x = x * (SIZE / MapSquare.SIZE),
        y = y * (SIZE / MapSquare.SIZE),
    )

    public operator fun component1(): Int = x

    public operator fun component2(): Int = y

    public operator fun minus(other: BuildArea): BuildArea {
        return translate(-other.x, -other.y)
    }

    public operator fun plus(other: BuildArea): BuildArea {
        return translate(other.x, other.y)
    }

    public companion object {
        public const val SIZE: Int = 104
    }
}
