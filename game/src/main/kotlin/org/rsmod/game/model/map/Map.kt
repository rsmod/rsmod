package org.rsmod.game.model.map

import com.google.common.base.MoreObjects

inline class Coordinates(private val packed: Int) {

    val x: Int
        get() = packed and 0x7FFF

    val y: Int
        get() = (packed shr 15) and 0x7FFF

    val level: Int
        get() = (packed shr 30) and 0x3

    val packed30Bits: Int
        get() = (y and 0x3FFF) or ((x and 0x3FFF) shl 14) or ((level and 0x3) shl 28)

    val packed18Bits: Int
        get() = (y shr 13) or ((x shr 13) shl 8) or ((level and 0x3) shl 16)

    constructor(x: Int, y: Int, level: Int = 0) : this(
        (x and 0x7FFF) or ((y and 0x7FFF) shl 15) or (level shl 30)
    )

    fun translate(xOffset: Int, yOffset: Int, levelOffset: Int = 0) = Coordinates(
        x = x + xOffset,
        y = y + yOffset,
        level = level + levelOffset
    )

    fun translateX(offset: Int) = translate(offset, 0, 0)

    fun translateY(offset: Int) = translate(0, offset, 0)

    fun translateLevel(offset: Int) = translate(0, 0, offset)

    fun zone() = Zone(
        x = x / Zone.SIZE,
        y = y / Zone.SIZE
    )

    fun mapSquare() = MapSquare(
        x = x / MapSquare.SIZE,
        y = y / MapSquare.SIZE
    )

    fun buildArea() = BuildArea(
        x = x / BuildArea.SIZE,
        y = y / BuildArea.SIZE
    )

    operator fun component1(): Int = x

    operator fun component2(): Int = y

    operator fun component3(): Int = level

    override fun toString(): String = MoreObjects
        .toStringHelper(this)
        .add("x", x)
        .add("y", y)
        .add("level", level)
        .toString()

    companion object {
        val ZERO = Coordinates(0)
    }
}

inline class Zone(private val packed: Int) {

    val x: Int
        get() = packed and 0x7FFF

    val y: Int
        get() = (packed shr 15) and 0x7FFF

    val level: Int
        get() = (packed shr 30) and 0x3

    constructor(x: Int, y: Int, level: Int = 0) : this(
        (x and 0x7FFF) or ((y and 0x7FFF) shl 15) or (level shl 30)
    )

    fun translate(xOffset: Int, yOffset: Int, levelOffset: Int = 0) = Zone(
        x = x + xOffset,
        y = y + yOffset,
        level = level + levelOffset
    )

    fun translateX(offset: Int) = translate(offset, 0, 0)

    fun translateY(offset: Int) = translate(0, offset, 0)

    fun translateLevel(offset: Int) = translate(0, 0, offset)

    fun coords() = Coordinates(
        x = x * SIZE,
        y = y * SIZE,
        level = level
    )

    fun mapSquare() = MapSquare(
        x = (x / (MapSquare.SIZE / SIZE)),
        y = (y / (MapSquare.SIZE / SIZE))
    )

    fun buildArea() = BuildArea(
        x = (x / (BuildArea.SIZE / SIZE)),
        y = (y / (BuildArea.SIZE / SIZE))
    )

    override fun toString(): String = MoreObjects
        .toStringHelper(this)
        .add("x", x)
        .add("y", y)
        .add("level", level)
        .toString()

    companion object {
        const val SIZE = 8
    }
}

inline class MapSquare(val id: Int) {

    val x: Int
        get() = id shr 8

    val y: Int
        get() = id and 0xFF

    constructor(x: Int, y: Int) : this(
        id = (x shl 8) or y
    )

    fun translate(xOffset: Int, yOffset: Int) = MapSquare(
        x = x + xOffset,
        y = y + yOffset
    )

    fun translateX(offset: Int) = translate(offset, 0)

    fun translateY(offset: Int) = translate(0, offset)

    fun coords(level: Int) = Coordinates(
        x = x * SIZE,
        y = y * SIZE,
        level = level
    )

    fun zone(level: Int) = Zone(
        x = x * (SIZE / Zone.SIZE),
        y = y * (SIZE / Zone.SIZE),
        level = level
    )

    fun buildArea() = BuildArea(
        x = (x / (BuildArea.SIZE / SIZE)),
        y = (y / (BuildArea.SIZE / SIZE))
    )

    override fun toString(): String = MoreObjects
        .toStringHelper(this)
        .add("id", id)
        .add("x", x)
        .add("y", y)
        .toString()

    companion object {
        const val SIZE = 64

        val ZERO = MapSquare(0)
    }
}

inline class BuildArea(private val packed: Int) {

    val x: Int
        get() = packed and 0xFFFF

    val y: Int
        get() = (packed shr 16) and 0xFFFF

    constructor(x: Int, y: Int) : this(
        (x and 0xFFFF) or ((y and 0xFFFF) shl 16)
    )

    fun translate(xOffset: Int, yOffset: Int) = BuildArea(
        x = x + xOffset,
        y = y + yOffset
    )

    fun translateX(offset: Int) = translate(offset, 0)

    fun translateY(offset: Int) = translate(0, offset)

    fun coords(level: Int) = Coordinates(
        x = x * SIZE,
        y = y * SIZE,
        level = level
    )

    fun zone(level: Int) = Zone(
        x = x * (SIZE / Zone.SIZE),
        y = y * (SIZE / Zone.SIZE),
        level = level
    )

    fun mapSquare() = MapSquare(
        x = x * (SIZE / MapSquare.SIZE),
        y = y * (SIZE / MapSquare.SIZE),
    )

    override fun toString(): String = MoreObjects
        .toStringHelper(this)
        .add("x", x)
        .add("y", y)
        .toString()

    companion object {

        const val SIZE = 104

        /**
         * The amount of tiles a player can be near the edge of
         * their current [BuildArea] before it must be rebuilt.
         */
        const val REBUILD_BOUNDARY = 16
    }
}

data class Viewport(
    val base: Coordinates,
    private val maps: List<MapSquare>
) : List<MapSquare> by maps {

    companion object {

        val ZERO = Viewport(Coordinates.ZERO, emptyList())
    }
}

fun Zone.viewport(isolation: MapIsolation): List<MapSquare> {
    val lx = (x - 6) / Zone.SIZE
    val ly = (y - 6) / Zone.SIZE
    val rx = (x + 6) / Zone.SIZE
    val ry = (y + 6) / Zone.SIZE

    val viewport = mutableListOf<MapSquare>()
    for (mx in lx..rx) {
        for (my in ly..ry) {
            val mapSquare = MapSquare(mx, my)
            viewport.add(mapSquare)
        }
    }

    val isolatedMap = isolation[mapSquare().id]
    if (isolatedMap != null) {
        viewport.removeIf { it.id in isolatedMap.hidden }
    }

    return viewport
}
