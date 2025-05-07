package org.rsmod.game.area.polygon

import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntArraySet
import org.rsmod.game.area.util.PolygonMapSquareClipper
import org.rsmod.map.CoordGrid
import org.rsmod.map.square.MapSquareGrid
import org.rsmod.map.square.MapSquareKey

@DslMarker private annotation class AreaBuilderDsl

@AreaBuilderDsl
public class PolygonAreaBuilder(private val area: Short, private val levels: IntArraySet) {
    private val packedVertices = IntArrayList()
    private val mapSquareKeys = IntArraySet()

    init {
        require(levels.isNotEmpty()) { "Set of levels must not be empty." }

        val validLevels = levels.all { it in 0 until CoordGrid.LEVEL_COUNT }
        require(validLevels) { "Invalid level(s) specified: $levels" }
    }

    public fun vertex(vertex: VertexCoord) {
        packedVertices += vertex.packed
        mapSquareKeys += vertex.mapSquareKey()
    }

    public fun build(): PolygonArea {
        check(packedVertices.isNotEmpty()) { "Polygon area must have at least one vertex." }
        if (packedVertices.size == 1) {
            val singleCoord = CoordGrid(packedVertices.single())
            return createSingleVertex(singleCoord)
        }

        val coordList = packedVertices.map { VertexCoord(it).toCoords() }
        if (mapSquareKeys.size == 1) {
            val singleMapSquare = MapSquareKey(mapSquareKeys.single())
            return createSingleSquare(singleMapSquare, coordList)
        }

        val clipped = PolygonMapSquareClipper.closeAndClip(coordList)
        return createMultiSquareClipped(clipped)
    }

    private fun createSingleVertex(coord: CoordGrid): PolygonArea {
        val builder = PolygonMapSquareBuilder()
        builder.polygon(area, levels) { coord(coord) }

        val polygon = builder.build()
        val mapSquare = MapSquareKey.from(coord)
        return PolygonArea(mapSquare, polygon)
    }

    private fun createSingleSquare(
        mapSquare: MapSquareKey,
        vertices: List<CoordGrid>,
    ): PolygonArea {
        val builder = PolygonMapSquareBuilder()
        builder.polygon(area, levels) { vertices.forEach { coord -> coord(coord) } }

        val polygon = builder.build()
        return PolygonArea(mapSquare, polygon)
    }

    private fun createMultiSquareClipped(
        partitioned: Map<MapSquareKey, List<CoordGrid>>
    ): PolygonArea {
        val mapSquares = mutableMapOf<MapSquareKey, PolygonMapSquare>()
        for ((mapSquare, vertices) in partitioned) {
            val builder = PolygonMapSquareBuilder()
            builder.polygon(area, levels) { vertices.forEach { coord -> coord(coord) } }
            mapSquares[mapSquare] = builder.build()
        }
        return PolygonArea(mapSquares)
    }

    private fun PolygonMapSquareBuilder.PolygonBuilder.coord(coord: CoordGrid) {
        val grid = MapSquareGrid.from(coord)
        vertex(grid.x, grid.z)
    }

    public companion object {
        public fun withLevels(area: Short, levels: Iterable<Int>): PolygonAreaBuilder {
            val levels = IntArraySet().apply { addAll(levels) }
            return PolygonAreaBuilder(area, levels)
        }

        public fun withAllLevels(area: Short): PolygonAreaBuilder {
            return withLevels(area, 0 until CoordGrid.LEVEL_COUNT)
        }
    }
}
