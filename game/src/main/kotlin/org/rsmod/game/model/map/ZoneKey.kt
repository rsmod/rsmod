package org.rsmod.game.model.map

@JvmInline
public value class ZoneKey(public val packed: Int) {

    public val x: Int get() = packed and 0x7FFF

    public val y: Int get() = (packed shr 15) and 0x7FFF

    public val level: Int get() = (packed shr 30) and 0x3

    public constructor(x: Int, y: Int, level: Int = 0) : this(
        (x and 0x7FFF) or ((y and 0x7FFF) shl 15) or ((level and 0x3) shl 30)
    )

    public fun translate(xOffset: Int, yOffset: Int, levelOffset: Int = 0): ZoneKey = ZoneKey(
        x = x + xOffset,
        y = y + yOffset,
        level = level + levelOffset
    )

    public fun translateX(offset: Int): ZoneKey = translate(offset, 0, 0)

    public fun translateY(offset: Int): ZoneKey = translate(0, offset, 0)

    public fun translateLevel(offset: Int): ZoneKey = translate(0, 0, offset)

    public fun toCoords(): Coordinates = Coordinates(
        x = x * SIZE,
        y = y * SIZE,
        level = level
    )

    public fun toMapSquareKey(): MapSquareKey = MapSquareKey(
        x = (x / (MapSquareKey.SIZE / SIZE)),
        y = (y / (MapSquareKey.SIZE / SIZE))
    )

    public fun toViewport(): List<MapSquareKey> {
        val lx = (x - 6) / SIZE
        val ly = (y - 6) / SIZE
        val rx = (x + 6) / SIZE
        val ry = (y + 6) / SIZE
        val viewport = mutableListOf<MapSquareKey>()
        for (mx in lx..rx) {
            for (my in ly..ry) {
                val mapSquare = MapSquareKey(mx, my)
                viewport += mapSquare
            }
        }
        return viewport
    }

    public operator fun component1(): Int = x

    public operator fun component2(): Int = y

    public operator fun component3(): Int = level

    public operator fun minus(other: ZoneKey): ZoneKey {
        return translate(-other.x, -other.y)
    }

    public operator fun plus(other: ZoneKey): ZoneKey {
        return translate(other.x, other.y)
    }

    public override fun toString(): String {
        return "ZoneKey(x=$x, y=$y, level=$level)"
    }

    public companion object {

        public const val SIZE: Int = 8
    }
}
