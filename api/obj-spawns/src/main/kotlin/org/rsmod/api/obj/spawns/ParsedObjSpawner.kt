package org.rsmod.api.obj.spawns

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import org.rsmod.api.parsers.toml.Toml
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.type.symbols.name.NameMapping
import org.rsmod.game.obj.Obj
import org.rsmod.game.obj.ObjEntity
import org.rsmod.game.obj.ObjScope

public class ParsedObjSpawner
@Inject
constructor(
    private val repo: ObjRepository,
    private val nameMapping: NameMapping,
    @Toml private val mapper: ObjectMapper,
) {
    public val inputContentsList: MutableList<() -> ByteArray> = mutableListOf()

    private val logger = InlineLogger()

    private val names: Map<String, Int>
        get() = nameMapping.objs

    public fun spawnAll(spawns: Iterable<ParsedObjSpawn>) {
        val mapped = spawns.mapNotNull { it.toObj() }
        for (obj in mapped) {
            repo.add(obj, Int.MAX_VALUE)
        }
    }

    public fun addStaticSpawns(inputContents: () -> ByteArray) {
        inputContentsList += inputContents
    }

    public suspend fun loadStaticSpawns(): Collection<ParsedObjSpawn> = supervisorScope {
        val deferred = ArrayList<Deferred<List<ParsedObjSpawn>>>(inputContentsList.size)
        for (i in inputContentsList.indices) {
            deferred += async {
                val inputData = inputContentsList[i].invoke()
                val spawnList = inputData.toObjSpawns()
                logger.trace { "Loaded list[$i] with ${spawnList.size} spawns." }
                spawnList
            }
        }
        deferred.awaitAll().flatten()
    }

    private fun ByteArray.toObjSpawns(): List<ParsedObjSpawn> {
        val type = object : TypeReference<Map<String, List<ParsedObjSpawn>>>() {}
        val map = mapper.readValue(this, type)
        return map[SPAWN_MAP_KEY] ?: error("Could not extract `$SPAWN_MAP_KEY` value from input.")
    }

    private fun ParsedObjSpawn.toObj(): Obj? {
        val internalId = names[obj] ?: return null
        val entity = ObjEntity(id = internalId, count = count, scope = ObjScope.Perm.id)
        return Obj(coords, entity, creationCycle = 0, receiverId = Obj.NULL_RECEIVER_ID)
    }

    public companion object {
        public const val SPAWN_MAP_KEY: String = "spawn"
    }
}
