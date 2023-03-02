package org.rsmod.game.model.map

@JvmInline
public value class BuildAreaKey(public val packed: Int) {

    public val x: Int get() = packed and 0xFFFF

    public val y: Int get() = (packed shr 16) and 0xFFFF

    public constructor(x: Int, y: Int) : this((x and 0xFFFF) or ((y and 0xFFFF) shl 16))

    public fun translate(xOffset: Int, yOffset: Int): BuildAreaKey = BuildAreaKey(
        x = x + xOffset,
        y = y + yOffset
    )

    public fun translateX(offset: Int): BuildAreaKey = translate(offset, 0)

    public fun translateY(offset: Int): BuildAreaKey = translate(0, offset)

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

    public fun toMapSquareKey(): MapSquareKey = MapSquareKey(
        x = x * (SIZE / MapSquareKey.SIZE),
        y = y * (SIZE / MapSquareKey.SIZE)
    )

    public operator fun component1(): Int = x

    public operator fun component2(): Int = y

    public operator fun minus(other: BuildAreaKey): BuildAreaKey {
        return translate(-other.x, -other.y)
    }

    public operator fun plus(other: BuildAreaKey): BuildAreaKey {
        return translate(other.x, other.y)
    }

    public override fun toString(): String {
        return "BuildAreaKey(x=$x, y=$y)"
    }

    public companion object {

        public const val SIZE: Int = 104
    }
}
