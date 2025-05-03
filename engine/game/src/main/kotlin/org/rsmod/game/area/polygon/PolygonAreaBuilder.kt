package org.rsmod.game.area.polygon

import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntArraySet
import org.rsmod.game.area.util.PolygonMapSquareClipper
import org.rsmod.map.CoordGrid
import org.rsmod.map.square.MapSquareKey
import org.rsmod.map.util.FastPack

@DslMarker private annotation class AreaBuilderDsl

@AreaBuilderDsl
public class PolygonAreaBuilder(public val area: Short, public val levels: Set<Int>) {
    private val points = IntArrayList()
    private var mapSquareKeys = IntArraySet()

    public fun point(coords: CoordGrid) {
        points += coords.packed
        mapSquareKeys += FastPack.mapSquareKey(coords)
    }

    public fun build(): PolygonArea {
        check(points.isNotEmpty()) { "Polygon area must have at least one point." }

        if (points.size == 1) {
            val singleCoord = CoordGrid(points.single())
            return createSinglePoint(singleCoord)
        }

        val coordList = points.map(::CoordGrid)
        if (mapSquareKeys.size == 1) {
            val singleMapSquare = MapSquareKey(mapSquareKeys.single())
            return createSingleSquare(singleMapSquare, coordList)
        }

        val clipped = PolygonMapSquareClipper.closeAndClip(coordList)
        return createMultiSquareClipped(clipped)
    }

    private fun createSinglePoint(point: CoordGrid): PolygonArea {
        val builder = PolygonMapSquareBuilder()
        builder.polygon(area, levels) { point(point) }

        val polygon = builder.build()
        val mapSquare = MapSquareKey.from(point)
        return PolygonArea(mapSquare, polygon)
    }

    private fun createSingleSquare(mapSquare: MapSquareKey, points: List<CoordGrid>): PolygonArea {
        val builder = PolygonMapSquareBuilder()
        builder.polygon(area, levels) { points.forEach { p -> point(p) } }

        val polygon = builder.build()
        return PolygonArea(mapSquare, polygon)
    }

    private fun createMultiSquareClipped(
        partitioned: Map<MapSquareKey, List<CoordGrid>>
    ): PolygonArea {
        val mapSquares = mutableMapOf<MapSquareKey, PolygonMapSquare>()
        for ((mapSquare, points) in partitioned) {
            val builder = PolygonMapSquareBuilder()
            builder.polygon(area, levels) { points.forEach { p -> point(p) } }
            mapSquares[mapSquare] = builder.build()
        }
        return PolygonArea(mapSquares)
    }
}
