package org.rsmod.api.type.builders.map.loc

import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import org.rsmod.api.cache.map.loc.MapLocListDecoder
import org.rsmod.api.cache.map.loc.MapLocListDefinition
import org.rsmod.api.cache.util.InlineByteBuf
import org.rsmod.api.type.builders.resource.TypeResourceFile
import org.rsmod.map.square.MapSquareKey

public class MapLocSpawnCollector {
    public fun loadAndCollect(
        builders: Iterable<MapLocSpawnBuilder>
    ): Map<MapSquareKey, MapLocListDefinition> {
        builders.forEach(MapLocSpawnBuilder::onPackMapTask)
        return builders.toMapDefinitions()
    }

    private fun Iterable<MapLocSpawnBuilder>.toMapDefinitions():
        Map<MapSquareKey, MapLocListDefinition> {
        val resources = flatMap(MapLocSpawnBuilder::resources).resourceSpawnTypes()
        return resources.groupDistinctKeys()
    }

    private fun Iterable<TypeResourceFile>.resourceSpawnTypes(): List<MapSpawnType> {
        return map { it.mapSpawnType() }
    }

    private fun TypeResourceFile.mapSpawnType(): MapSpawnType {
        val fileName = relativePath.substringAfterLast('/')
        if (!fileName.startsWith('l')) {
            val message = "Loc file name must be in format `l[x]_[z]` (e.g., `l50_50`): $fileName"
            throw IOException(message)
        }

        if (fileName.contains('.')) {
            val message = "Loc file must not have an extension: $relativePath"
            throw IOException(message)
        }

        val input = clazz.getResourceAsStream(relativePath)
        if (input == null) {
            val message = "Loc resource file not found: path=$relativePath, class=$clazz"
            throw FileNotFoundException(message)
        }

        val mapSquare = parseMapSquare(fileName)
        val bytes = input.use(InputStream::readAllBytes)
        val definition = MapLocListDecoder.decode(InlineByteBuf(bytes))
        return MapSpawnType(mapSquare, definition)
    }

    private fun parseMapSquare(fileName: String): MapSquareKey {
        val parts = fileName.removePrefix("l").split('_')
        if (parts.size != 2) {
            val message = "Loc file name must be in format `l[x]_[z]` (e.g., `l50_50`): $fileName"
            throw IOException(message)
        }
        val x = parts[0].toIntOrNull() ?: error("Invalid x-coordinate in file name: $fileName")
        val z = parts[1].toIntOrNull() ?: error("Invalid z-coordinate in file name: $fileName")
        return MapSquareKey(x, z)
    }

    private fun List<MapSpawnType>.groupDistinctKeys(): Map<MapSquareKey, MapLocListDefinition> {
        val grouped = groupBy { it.mapSquare }

        val duplicates = grouped.filterValues { it.size > 1 }
        if (duplicates.isNotEmpty()) {
            val duplicateKeys = duplicates.keys.joinToString(", ")
            val message = "Duplicate MapSquareKeys found for loc spawn files: $duplicateKeys"
            throw IllegalStateException(message)
        }

        return grouped.mapValues { (_, entries) -> entries.single().spawns }
    }

    private data class MapSpawnType(val mapSquare: MapSquareKey, val spawns: MapLocListDefinition)
}
