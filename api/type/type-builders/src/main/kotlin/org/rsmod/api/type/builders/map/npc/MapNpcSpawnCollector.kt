package org.rsmod.api.type.builders.map.npc

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.ints.IntArrayList
import jakarta.inject.Inject
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import org.rsmod.api.cache.map.npc.MapNpcDefinition
import org.rsmod.api.cache.map.npc.MapNpcListDecoder
import org.rsmod.api.cache.map.npc.MapNpcListDefinition
import org.rsmod.api.cache.util.InlineByteBuf
import org.rsmod.api.parsers.toml.Toml
import org.rsmod.api.type.builders.map.MapResourceFile
import org.rsmod.api.type.symbols.name.NameMapping
import org.rsmod.map.CoordGrid
import org.rsmod.map.square.MapSquareGrid
import org.rsmod.map.square.MapSquareKey

public class MapNpcSpawnCollector
@Inject
constructor(@Toml private val objectMapper: ObjectMapper, private val nameMapping: NameMapping) {
    public fun loadAndCollect(
        builders: Iterable<MapNpcSpawnBuilder>
    ): Map<MapSquareKey, MapNpcListDefinition> {
        builders.forEach(MapNpcSpawnBuilder::onPackMapTask)
        return builders.toMapDefinitions()
    }

    private fun Iterable<MapNpcSpawnBuilder>.toMapDefinitions():
        Map<MapSquareKey, MapNpcListDefinition> {
        val resources = flatMap(MapNpcSpawnBuilder::resources).resourceSpawnTypes()
        return resources.mergeToMap()
    }

    private fun Iterable<MapResourceFile>.resourceSpawnTypes(): List<MapSpawnType> {
        return flatMap { it.mapSpawnType() }
    }

    private fun MapResourceFile.mapSpawnType(): List<MapSpawnType> {
        val fileName = relativePath.substringAfterLast('/')
        val input = clazz.getResourceAsStream(relativePath)
        if (input == null) {
            val message = "Npc spawn resource file not found: path=$relativePath, class=$clazz"
            throw FileNotFoundException(message)
        }
        return when {
            fileName.endsWith(".toml") -> {
                decodeTomlSpawn(input)
            }
            fileName.startsWith('n') -> {
                if (fileName.contains('.')) {
                    val message = "Npc binary file must not have an extension: $relativePath"
                    throw IOException(message)
                }
                listOf(decodeBinarySpawn(fileName, input))
            }
            else -> {
                val message = "Unsupported npc spawn file format: $relativePath"
                throw IOException(message)
            }
        }
    }

    private fun decodeTomlSpawn(input: InputStream): List<MapSpawnType> {
        val reference = object : TypeReference<Map<String, List<TomlNpcSpawn>>>() {}
        val parsed = input.use { objectMapper.readValue(it, reference) }
        val spawns = parsed[TOML_SPAWN_KEY]
        if (spawns == null) {
            val message = "Could not extract `$TOML_SPAWN_KEY` value from input."
            throw IOException(message)
        }
        val names = nameMapping.npcs
        val grouped = Int2ObjectOpenHashMap<IntArrayList>()
        for (spawn in spawns) {
            val npc = names[spawn.npc] ?: error("Invalid npc name '${spawn.npc}' in spawn: $spawn")
            val grid = MapSquareGrid.from(spawn.coords)
            val def = MapNpcDefinition(npc, localX = grid.x, localZ = grid.z, level = grid.level)
            val mapSquare = MapSquareKey.from(spawn.coords)
            val spawnList = grouped.computeIfAbsent(mapSquare.id) { IntArrayList() }
            spawnList.add(def.packed)
        }
        return grouped.map { MapSpawnType(MapSquareKey(it.key), MapNpcListDefinition(it.value)) }
    }

    private fun decodeBinarySpawn(fileName: String, input: InputStream): MapSpawnType {
        val mapSquare = parseMapSquare(fileName)
        val bytes = input.use(InputStream::readAllBytes)
        val definition = MapNpcListDecoder.decode(InlineByteBuf(bytes))
        return MapSpawnType(mapSquare, definition)
    }

    private fun parseMapSquare(fileName: String): MapSquareKey {
        val parts = fileName.removePrefix("n").split('_')
        if (parts.size != 2) {
            val message = "Npc file name must be in format `n[x]_[z]` (e.g., `n50_50`): $fileName"
            throw IOException(message)
        }
        val x = parts[0].toIntOrNull() ?: error("Invalid x-coordinate in file name: $fileName")
        val z = parts[1].toIntOrNull() ?: error("Invalid z-coordinate in file name: $fileName")
        return MapSquareKey(x, z)
    }

    private fun List<MapSpawnType>.mergeToMap(): Map<MapSquareKey, MapNpcListDefinition> {
        val merged = mutableMapOf<MapSquareKey, MapNpcListDefinition>()
        for ((mapSquare, def) in this) {
            merged.merge(mapSquare, def, MapNpcListDefinition::merge)
        }
        return merged
    }

    private data class MapSpawnType(val mapSquare: MapSquareKey, val spawns: MapNpcListDefinition)

    private data class TomlNpcSpawn(val npc: String, val coords: CoordGrid)

    private companion object {
        const val TOML_SPAWN_KEY = "spawn"
    }
}
