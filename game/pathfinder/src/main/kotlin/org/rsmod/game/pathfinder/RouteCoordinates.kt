package org.rsmod.game.pathfinder

@JvmInline
public value class RouteCoordinates(public val packed: Int) {

    public val x: Int
        get() = packed and 0x7FFF

    public val y: Int
        get() = (packed shr 15) and 0x7FFF

    public val level: Int
        get() = (packed shr 30) and 0x3

    public constructor(x: Int, y: Int, level: Int = 0) : this(
        (x and 0x7FFF) or ((y and 0x7FFF) shl 15) or ((level and 0x3) shl 30)
    )

    public fun translate(xOffset: Int, yOffset: Int, levelOffset: Int = 0): RouteCoordinates = RouteCoordinates(
        x = x + xOffset,
        y = y + yOffset,
        level = level + levelOffset
    )

    override fun toString(): String {
        return "RouteCoordinates(x=$x, y=$y, level=$level)"
    }
}
