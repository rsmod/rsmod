package org.rsmod.api.type.builders.map.obj

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.longs.LongArrayList
import jakarta.inject.Inject
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import org.rsmod.api.cache.map.obj.MapObjDefinition
import org.rsmod.api.cache.map.obj.MapObjListDecoder
import org.rsmod.api.cache.map.obj.MapObjListDefinition
import org.rsmod.api.cache.util.InlineByteBuf
import org.rsmod.api.parsers.toml.Toml
import org.rsmod.api.type.builders.map.MapResourceFile
import org.rsmod.api.type.symbols.name.NameMapping
import org.rsmod.map.CoordGrid
import org.rsmod.map.square.MapSquareGrid
import org.rsmod.map.square.MapSquareKey

public class MapObjSpawnCollector
@Inject
constructor(@Toml private val objectMapper: ObjectMapper, private val nameMapping: NameMapping) {
    public fun loadAndCollect(
        builders: Iterable<MapObjSpawnBuilder>
    ): Map<MapSquareKey, MapObjListDefinition> {
        builders.forEach(MapObjSpawnBuilder::onPackMapTask)
        return builders.toMapDefinitions()
    }

    private fun Iterable<MapObjSpawnBuilder>.toMapDefinitions():
        Map<MapSquareKey, MapObjListDefinition> {
        val resources = flatMap(MapObjSpawnBuilder::resources).resourceSpawnTypes()
        return resources.mergeToMap()
    }

    private fun Iterable<MapResourceFile>.resourceSpawnTypes(): List<MapSpawnType> {
        return flatMap { it.mapSpawnType() }
    }

    private fun MapResourceFile.mapSpawnType(): List<MapSpawnType> {
        val fileName = relativePath.substringAfterLast('/')
        val input = clazz.getResourceAsStream(relativePath)
        if (input == null) {
            val message = "Obj spawn resource file not found: path=$relativePath, class=$clazz"
            throw FileNotFoundException(message)
        }
        return when {
            fileName.endsWith(".toml") -> {
                decodeTomlSpawn(input)
            }
            fileName.startsWith('o') -> {
                if (fileName.contains('.')) {
                    val message = "Obj binary file must not have an extension: $relativePath"
                    throw IOException(message)
                }
                listOf(decodeBinarySpawn(fileName, input))
            }
            else -> {
                val message = "Unsupported obj spawn file format: $relativePath"
                throw IOException(message)
            }
        }
    }

    private fun decodeTomlSpawn(input: InputStream): List<MapSpawnType> {
        val reference = object : TypeReference<Map<String, List<TomlObjSpawn>>>() {}
        val parsed = input.use { objectMapper.readValue(it, reference) }
        val spawns = parsed[TOML_SPAWN_KEY]
        if (spawns == null) {
            val message = "Could not extract `$TOML_SPAWN_KEY` value from input."
            throw IOException(message)
        }
        val names = nameMapping.objs
        val grouped = Int2ObjectOpenHashMap<LongArrayList>()
        for (spawn in spawns) {
            val obj = names[spawn.obj] ?: error("Invalid obj name '${spawn.obj}' in spawn: $spawn")
            val grid = MapSquareGrid.from(spawn.coords)
            val def =
                MapObjDefinition(
                    id = obj,
                    count = spawn.count,
                    localX = grid.x,
                    localZ = grid.z,
                    level = grid.level,
                )
            val mapSquare = MapSquareKey.from(spawn.coords)
            val spawnList = grouped.computeIfAbsent(mapSquare.id) { LongArrayList() }
            spawnList.add(def.packed)
        }

        return grouped.map { MapSpawnType(MapSquareKey(it.key), MapObjListDefinition(it.value)) }
    }

    private fun decodeBinarySpawn(fileName: String, input: InputStream): MapSpawnType {
        val mapSquare = parseMapSquare(fileName)
        val bytes = input.use(InputStream::readAllBytes)
        val definition = MapObjListDecoder.decode(InlineByteBuf(bytes))
        return MapSpawnType(mapSquare, definition)
    }

    private fun parseMapSquare(fileName: String): MapSquareKey {
        val parts = fileName.removePrefix("o").split('_')
        if (parts.size != 2) {
            val message = "Obj file name must be in format `o[x]_[z]` (e.g., `o50_50`): $fileName"
            throw IOException(message)
        }
        val x = parts[0].toIntOrNull() ?: error("Invalid x-coordinate in file name: $fileName")
        val z = parts[1].toIntOrNull() ?: error("Invalid z-coordinate in file name: $fileName")
        return MapSquareKey(x, z)
    }

    private fun List<MapSpawnType>.mergeToMap(): Map<MapSquareKey, MapObjListDefinition> {
        val merged = mutableMapOf<MapSquareKey, MapObjListDefinition>()
        for ((mapSquare, def) in this) {
            merged.merge(mapSquare, def, MapObjListDefinition::merge)
        }
        return merged
    }

    private data class MapSpawnType(val mapSquare: MapSquareKey, val spawns: MapObjListDefinition)

    private data class TomlObjSpawn(val obj: String, val count: Int = 1, val coords: CoordGrid)

    private companion object {
        const val TOML_SPAWN_KEY = "spawn"
    }
}
