package org.rsmod.api.npc.spawn

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import org.rsmod.api.parsers.toml.Toml
import org.rsmod.api.repo.npc.NpcRepository
import org.rsmod.api.type.symbols.name.NameMapping
import org.rsmod.game.entity.Npc
import org.rsmod.game.type.npc.NpcTypeList

public class ParsedNpcSpawner
@Inject
constructor(
    private val repo: NpcRepository,
    private val npcTypes: NpcTypeList,
    private val nameMapping: NameMapping,
    @Toml private val mapper: ObjectMapper,
) {
    public val inputContentsList: MutableList<() -> ByteArray> = mutableListOf()

    private val logger = InlineLogger()

    private val names: Map<String, Int>
        get() = nameMapping.npcs

    public fun spawnAll(spawns: Iterable<ParsedNpcSpawn>) {
        val mapped = spawns.mapNotNull { it.toNpc() }
        for (npc in mapped) {
            repo.add(npc, Int.MAX_VALUE)
        }
    }

    public fun addStaticSpawns(inputContents: () -> ByteArray) {
        inputContentsList += inputContents
    }

    public suspend fun loadStaticSpawns(): Collection<ParsedNpcSpawn> = supervisorScope {
        val deferred = ArrayList<Deferred<List<ParsedNpcSpawn>>>(inputContentsList.size)
        for (i in inputContentsList.indices) {
            deferred += async {
                val inputData = inputContentsList[i].invoke()
                val spawnList = inputData.toNpcSpawns()
                logger.trace { "Loaded list[$i] with ${spawnList.size} spawns." }
                spawnList
            }
        }
        deferred.awaitAll().flatten()
    }

    private fun ByteArray.toNpcSpawns(): List<ParsedNpcSpawn> {
        val type = object : TypeReference<Map<String, List<ParsedNpcSpawn>>>() {}
        val map = mapper.readValue(this, type)
        return map[SPAWN_MAP_KEY] ?: error("Could not extract `$SPAWN_MAP_KEY` value from input.")
    }

    private fun ParsedNpcSpawn.toNpc(): Npc? {
        val internalId = names[npc] ?: return null
        val type = npcTypes[internalId] ?: return null
        return Npc(type, coords)
    }

    public companion object {
        public const val SPAWN_MAP_KEY: String = "spawn"
    }
}
