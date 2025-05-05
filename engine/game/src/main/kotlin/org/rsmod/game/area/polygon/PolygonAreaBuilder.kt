package org.rsmod.game.area.polygon

import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntArraySet
import org.rsmod.game.area.util.PolygonMapSquareClipper
import org.rsmod.map.CoordGrid
import org.rsmod.map.square.MapSquareGrid
import org.rsmod.map.square.MapSquareKey
import org.rsmod.map.util.FastPack

@DslMarker private annotation class AreaBuilderDsl

@AreaBuilderDsl
public class PolygonAreaBuilder(public val area: Short) {
    private val vertices = IntArrayList()
    private val mapSquareKeys = IntArraySet()
    private val levels = IntArraySet().apply { add(0) }

    public var allLevels: Boolean
        get() = levels.size == CoordGrid.LEVEL_COUNT
        set(value) {
            if (value) levels += 0 until CoordGrid.LEVEL_COUNT else levels.clear()
        }

    public var level0: Boolean
        get() = levels.contains(0)
        set(value) {
            if (value) levels += 0
        }

    public var level1: Boolean
        get() = levels.contains(1)
        set(value) {
            if (value) levels += 1
        }

    public var level2: Boolean
        get() = levels.contains(2)
        set(value) {
            if (value) levels += 2
        }

    public var level3: Boolean
        get() = levels.contains(3)
        set(value) {
            if (value) levels += 3
        }

    public fun vertex(coords: CoordGrid) {
        vertices += coords.packed
        mapSquareKeys += FastPack.mapSquareKey(coords)
    }

    public fun mapSquare(mapSquare: MapSquareKey) {
        val base = mapSquare.toCoords(0)
        for (level in 0 until CoordGrid.LEVEL_COUNT) {
            val sw = base.translateLevel(level)
            vertex(sw)

            val se = sw.translate(63, 0)
            vertex(se)

            val ne = sw.translate(63, 63)
            vertex(ne)

            val nw = sw.translate(0, 63)
            vertex(nw)
        }
    }

    public fun build(): PolygonArea {
        check(vertices.isNotEmpty()) { "Polygon area must have at least one vertex." }
        if (vertices.size == 1) {
            val singleCoord = CoordGrid(vertices.single())
            return createSingleVertex(singleCoord)
        }

        val coordList = vertices.map(::CoordGrid)
        if (mapSquareKeys.size == 1) {
            val singleMapSquare = MapSquareKey(mapSquareKeys.single())
            return createSingleSquare(singleMapSquare, coordList)
        }

        val clipped = PolygonMapSquareClipper.closeAndClip(coordList)
        return createMultiSquareClipped(clipped)
    }

    private fun createSingleVertex(vertex: CoordGrid): PolygonArea {
        val builder = PolygonMapSquareBuilder()
        builder.polygon(area, levels) { vertex(vertex.toMapSquareGrid()) }

        val polygon = builder.build()
        val mapSquare = MapSquareKey.from(vertex)
        return PolygonArea(mapSquare, polygon)
    }

    private fun createSingleSquare(
        mapSquare: MapSquareKey,
        vertices: List<CoordGrid>,
    ): PolygonArea {
        val builder = PolygonMapSquareBuilder()
        builder.polygon(area, levels) { vertices.forEach { p -> vertex(p.toMapSquareGrid()) } }

        val polygon = builder.build()
        return PolygonArea(mapSquare, polygon)
    }

    private fun createMultiSquareClipped(
        partitioned: Map<MapSquareKey, List<CoordGrid>>
    ): PolygonArea {
        val mapSquares = mutableMapOf<MapSquareKey, PolygonMapSquare>()
        for ((mapSquare, vertices) in partitioned) {
            val builder = PolygonMapSquareBuilder()
            builder.polygon(area, levels) { vertices.forEach { p -> vertex(p.toMapSquareGrid()) } }
            mapSquares[mapSquare] = builder.build()
        }
        return PolygonArea(mapSquares)
    }

    private fun CoordGrid.toMapSquareGrid(): MapSquareGrid = MapSquareGrid.from(this)
}
