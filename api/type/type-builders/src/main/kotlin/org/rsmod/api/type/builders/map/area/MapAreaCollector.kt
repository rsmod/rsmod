package org.rsmod.api.type.builders.map.area

import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import org.rsmod.api.cache.map.area.MapAreaDecoder
import org.rsmod.api.cache.map.area.MapAreaDefinition
import org.rsmod.api.cache.util.InlineByteBuf
import org.rsmod.api.type.builders.resource.TypeResourceFile
import org.rsmod.game.area.polygon.PolygonArea
import org.rsmod.map.square.MapSquareKey

public class MapAreaCollector {
    public fun loadAndCollect(
        builders: Iterable<MapAreaBuilder>
    ): Map<MapSquareKey, MapAreaDefinition> {
        builders.forEach(MapAreaBuilder::onPackMapTask)
        return builders.toMapDefinitions()
    }

    private fun Iterable<MapAreaBuilder>.toMapDefinitions(): Map<MapSquareKey, MapAreaDefinition> {
        val polygons = flatMap(MapAreaBuilder::polygons).polygonAreaTypes()
        val resources = flatMap(MapAreaBuilder::resources).resourceAreaTypes()
        return (polygons + resources).mergeToMap()
    }

    private fun Iterable<PolygonArea>.polygonAreaTypes(): List<MapAreaType> {
        return flatMap { it.mapAreaTypes() }
    }

    private fun PolygonArea.mapAreaTypes(): List<MapAreaType> {
        return mapSquares.map { MapAreaType(it.key, MapAreaDefinition.from(it.value)) }
    }

    private fun Iterable<TypeResourceFile>.resourceAreaTypes(): List<MapAreaType> {
        return map { it.mapAreaType() }
    }

    private fun TypeResourceFile.mapAreaType(): MapAreaType {
        val fileName = relativePath.substringAfterLast('/')
        if (!fileName.startsWith('a')) {
            val message = "Area file name must be in format `a[x]_[z]` (e.g., `a50_50`): $fileName"
            throw IOException(message)
        }

        if (fileName.contains('.')) {
            val message = "Area file must not have an extension: $relativePath"
            throw IOException(message)
        }

        val input = clazz.getResourceAsStream(relativePath)
        if (input == null) {
            val message = "Area resource file not found: path=$relativePath, class=$clazz"
            throw FileNotFoundException(message)
        }

        val mapSquare = parseMapSquare(fileName)
        val bytes = input.use(InputStream::readAllBytes)
        val definition = MapAreaDecoder.decode(InlineByteBuf(bytes))
        return MapAreaType(mapSquare, definition)
    }

    private fun parseMapSquare(fileName: String): MapSquareKey {
        val parts = fileName.removePrefix("a").split('_')
        if (parts.size != 2) {
            val message = "Area file name must be in format `a[x]_[z]` (e.g., `a50_50`): $fileName"
            throw IOException(message)
        }
        val x = parts[0].toIntOrNull() ?: error("Invalid x-coordinate in file name: $fileName")
        val z = parts[1].toIntOrNull() ?: error("Invalid z-coordinate in file name: $fileName")
        return MapSquareKey(x, z)
    }

    private fun List<MapAreaType>.mergeToMap(): Map<MapSquareKey, MapAreaDefinition> {
        val merged = mutableMapOf<MapSquareKey, MapAreaDefinition>()
        for ((mapSquare, def) in this) {
            merged.merge(mapSquare, def, MapAreaDefinition::merge)
        }
        return merged
    }

    private data class MapAreaType(val mapSquare: MapSquareKey, val definition: MapAreaDefinition)
}
