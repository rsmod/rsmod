package org.rsmod.game.pathfinder

@JvmInline
public value class RouteCoordinates(public val packed: Int) {

    public val x: Int get() = (packed shr 14) and 0x3FFF

    public val z: Int get() = packed and 0x3FFF

    public val level: Int get() = (packed shr 28) and 0x3

    public constructor(x: Int, z: Int, level: Int = 0) : this(
        (z and 0x3FFF) or ((x and 0x3FFF) shl 14) or ((level and 0x3) shl 28)
    )

    public fun translate(xOffset: Int, zOffset: Int, levelOffset: Int = 0): RouteCoordinates = RouteCoordinates(
        x = x + xOffset,
        z = z + zOffset,
        level = level + levelOffset
    )

    public operator fun component1(): Int = x

    public operator fun component2(): Int = z

    public operator fun component3(): Int = level

    override fun toString(): String {
        return "RouteCoordinates(x=$x, z=$z, level=$level)"
    }
}
