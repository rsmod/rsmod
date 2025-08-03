package org.rsmod.api.type.builders.map.tile

import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import org.rsmod.api.cache.map.tile.MapTileByteDefinition
import org.rsmod.api.type.builders.resource.TypeResourceFile
import org.rsmod.map.square.MapSquareKey

public class MapTileCollector {
    public fun loadAndCollect(
        builders: Iterable<MapTileBuilder>
    ): Map<MapSquareKey, MapTileByteDefinition> {
        builders.forEach(MapTileBuilder::onPackMapTask)
        return builders.toMapDefinitions()
    }

    private fun Iterable<MapTileBuilder>.toMapDefinitions():
        Map<MapSquareKey, MapTileByteDefinition> {
        val resources = flatMap(MapTileBuilder::resources).resourceTerrainTypes()
        return resources.groupDistinctKeys()
    }

    private fun Iterable<TypeResourceFile>.resourceTerrainTypes(): List<MapTerrainType> {
        return map { it.mapTerrainType() }
    }

    private fun TypeResourceFile.mapTerrainType(): MapTerrainType {
        val fileName = relativePath.substringAfterLast('/')
        if (!fileName.startsWith('m')) {
            val message =
                "Terrain file name must be in format `m[x]_[z]` (e.g., `m50_50`): $fileName"
            throw IOException(message)
        }

        if (fileName.contains('.')) {
            val message = "Terrain file must not have an extension: $relativePath"
            throw IOException(message)
        }

        val input = clazz.getResourceAsStream(relativePath)
        if (input == null) {
            val message = "Terrain resource file not found: path=$relativePath, class=$clazz"
            throw FileNotFoundException(message)
        }

        val mapSquare = parseMapSquare(fileName)
        val bytes = input.use(InputStream::readAllBytes)
        val definition = MapTileByteDefinition(bytes)
        return MapTerrainType(mapSquare, definition)
    }

    private fun parseMapSquare(fileName: String): MapSquareKey {
        val parts = fileName.removePrefix("m").split('_')
        if (parts.size != 2) {
            val message =
                "Terrain file name must be in format `m[x]_[z]` (e.g., `m50_50`): $fileName"
            throw IOException(message)
        }
        val x = parts[0].toIntOrNull() ?: error("Invalid x-coordinate in file name: $fileName")
        val z = parts[1].toIntOrNull() ?: error("Invalid z-coordinate in file name: $fileName")
        return MapSquareKey(x, z)
    }

    private fun List<MapTerrainType>.groupDistinctKeys(): Map<MapSquareKey, MapTileByteDefinition> {
        val grouped = groupBy { it.mapSquare }

        val duplicates = grouped.filterValues { it.size > 1 }
        if (duplicates.isNotEmpty()) {
            val duplicateKeys = duplicates.keys.joinToString(", ")
            val message = "Duplicate MapSquareKeys found for map terrain files: $duplicateKeys"
            throw IllegalStateException(message)
        }

        return grouped.mapValues { (_, entries) -> entries.single().data }
    }

    private class MapTerrainType(val mapSquare: MapSquareKey, val data: MapTileByteDefinition)
}
