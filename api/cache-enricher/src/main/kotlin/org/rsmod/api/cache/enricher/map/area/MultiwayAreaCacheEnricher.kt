package org.rsmod.api.cache.enricher.map.area

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.inject.Inject
import org.rsmod.api.cache.map.area.MapAreaDefinition
import org.rsmod.api.config.refs.areas
import org.rsmod.api.parsers.json.Json
import org.rsmod.api.utils.io.InputStreams
import org.rsmod.game.area.polygon.PolygonArea
import org.rsmod.game.area.polygon.PolygonMapSquareBuilder
import org.rsmod.game.area.util.PolygonMapSquareClipper
import org.rsmod.map.CoordGrid
import org.rsmod.map.square.MapSquareGrid
import org.rsmod.map.square.MapSquareKey

public class MultiwayAreaCacheEnricher @Inject constructor(@Json private val mapper: ObjectMapper) :
    AreaCacheEnricher {
    override fun generate(): List<EnrichedAreaConfig> {
        val multiway = loadMultiwayAreas()
        val areas = collectAreas(multiway)
        return areas.toAreaConfigList()
    }

    private fun loadMultiwayAreas(): Array<MultiwayArea> {
        val input = InputStreams.readAllBytes<MultiwayAreaCacheEnricher>("multiway.json")
        return mapper.readValue(input, Array<MultiwayArea>::class.java)
    }

    private fun PolygonArea.toAreaConfigList(): List<EnrichedAreaConfig> {
        return mapSquares.map { (square, polygon) ->
            val areaDef = MapAreaDefinition.from(polygon)
            EnrichedAreaConfig(square, areaDef)
        }
    }

    private fun collectAreas(multiwayAreas: Array<MultiwayArea>): PolygonArea {
        // TODO: Yet another place we have to hardcode this value. I think we should heavily
        //  consider moving this to an `AreaType` flag.
        val multiwayArea = areas.multiway.id.toShort()

        val builders = mutableMapOf<MapSquareKey, PolygonMapSquareBuilder>()
        for (multiway in multiwayAreas) {
            val levels = multiway.levels.toSet()

            val mapSquares = multiway.collectMapSquareKeys()
            for (mapSquare in mapSquares) {
                val builder = builders.getOrPut(mapSquare) { PolygonMapSquareBuilder() }
                for (polygon in multiway.polygons) {
                    if (polygon !in mapSquare) {
                        continue
                    }
                    val clipped = PolygonMapSquareClipper.closeAndClip(polygon.coords)
                    val polygonVertices = clipped[mapSquare] ?: continue
                    builder.polygon(multiwayArea, levels) {
                        for (vertex in polygonVertices) {
                            val localX = vertex.x % MapSquareGrid.LENGTH
                            val localZ = vertex.z % MapSquareGrid.LENGTH
                            vertex(localX, localZ)
                        }
                    }
                }
            }
        }

        val groupedSquares = builders.mapValues { it.value.build() }
        return PolygonArea(groupedSquares)
    }

    private operator fun MapSquareKey.contains(polygon: MultiwayPolygon): Boolean {
        return this in polygon.mapSquares
    }
}

private data class MultiwayArea(
    val name: String,
    val levels: List<Int>,
    val polygons: List<MultiwayPolygon>,
) {
    fun collectMapSquareKeys(): Set<MapSquareKey> {
        return polygons.asSequence().flatMap(MultiwayPolygon::mapSquares).toSet()
    }
}

private data class MultiwayPolygon(val vertices: List<Point>) {
    val coords = vertices.map { CoordGrid(it.x, it.z) }
    val mapSquares = vertices.asSequence().map { MapSquareKey.fromAbsolute(it.x, it.z) }.toSet()
}

private data class Point(val x: Int, val z: Int)
