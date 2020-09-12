package gg.rsmod.game.model.map

inline class Coordinates(private val packed: Int) {

    val x: Int
        get() = packed and 0x7FFF

    val y: Int
        get() = (packed shr 15) and 0x7FFF

    val plane: Int
        get() = (packed shr 30)

    val packed30Bits: Int
        get() = (y and 0x3FFF) or ((x and 0x3FFF) shl 14) or ((plane and 0x3) shl 28)

    constructor(x: Int, y: Int, plane: Int = 0) :
        this((x and 0x7FFF) or ((y and 0x7FFF) shl 15) or (plane shl 30))

    fun translate(xOffset: Int, yOffset: Int, planeOffset: Int): Coordinates =
        Coordinates(x + xOffset, y + yOffset, plane + planeOffset)

    fun translateX(offset: Int) = translate(offset, 0, 0)

    fun translateY(offset: Int) = translate(0, offset, 0)

    fun translatePlane(offset: Int) = translate(0, 0, offset)

    companion object {
        val ZERO = Coordinates(0)
    }
}

inline class Zone(private val swCoords: Coordinates) {
    companion object {
        const val SIZE = 8
    }
}

inline class ZoneKey(private val packed: Int) {

    private val x: Int
        get() = packed and 0x7FFF

    private val y: Int
        get() = (packed shr 15) and 0x7FFF

    private val plane: Int
        get() = (packed shr 30)

    constructor(x: Int, y: Int, plane: Int = 0) :
        this((x and 0x7FFF) or ((y and 0x7FFF) shl 15) or (plane shl 30))

    fun toZone() = Zone(Coordinates(x shl 3, y shl 3, plane))
}

inline class MapSquare(private val swCoords: Coordinates) {
    companion object {
        const val SIZE = 64
    }
}

inline class Region(private val swCoords: Coordinates) {
    companion object {
        const val SIZE = 104
    }
}
