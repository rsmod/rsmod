package org.runerealm.api.cache.enricher.map.area

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.inject.Inject
import org.runerealm.api.cache.map.area.MapAreaDefinition
import org.runerealm.api.config.refs.areas
import org.runerealm.api.parsers.json.Json
import org.runerealm.api.utils.io.InputStreams
import org.runerealm.game.area.polygon.PolygonArea
import org.runerealm.game.area.polygon.PolygonMapSquareBuilder
import org.runerealm.game.area.util.PolygonMapSquareClipper
import org.runerealm.map.CoordGrid
import org.runerealm.map.square.MapSquareGrid
import org.runerealm.map.square.MapSquareKey

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

        val builderLists = mutableMapOf<MapSquareKey, PolygonMapSquareBuilder>()
        for (multiway in multiwayAreas) {
            val levels = multiway.levels.toSet()

            for (polygon in multiway.polygons) {
                val clipped = PolygonMapSquareClipper.closeAndClip(polygon.coords)
                for ((mapSquare, polygonVertices) in clipped) {
                    val builder = builderLists.getOrPut(mapSquare) { PolygonMapSquareBuilder() }
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

        val groupedSquares = builderLists.mapValues { it.value.build() }
        return PolygonArea(groupedSquares)
    }

    private data class MultiwayArea(
        val name: String,
        val levels: List<Int>,
        val polygons: List<MultiwayPolygon>,
    )

    private data class MultiwayPolygon(val vertices: List<Point>) {
        val coords = vertices.map { CoordGrid(it.x, it.z) }
    }

    private data class Point(val x: Int, val z: Int)
}
