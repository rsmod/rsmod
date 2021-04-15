package org.rsmod.plugins.api.spawn

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.michaelbull.logging.InlineLogger
import org.rsmod.game.model.domain.Direction
import org.rsmod.game.model.map.Coordinates
import org.rsmod.game.model.mob.Npc
import org.rsmod.game.model.mob.NpcList
import org.rsmod.game.model.npc.type.NpcTypeList
import org.rsmod.plugins.api.cache.config.ConfigFileLoader
import org.rsmod.plugins.api.cache.config.file.DefaultExtensions
import org.rsmod.plugins.api.cache.config.file.NamedConfigFileMap
import org.rsmod.plugins.api.cache.name.npc.NpcNameMap
import org.rsmod.plugins.api.model.mob.faceDirection
import org.rsmod.plugins.api.util.toPlural
import javax.inject.Inject

private val logger = InlineLogger()

private const val NAME_KEY = "name"
private const val COORDS_X_KEY = "x"
private const val COORDS_Y_KEY = "y"
private const val COORDS_LEVEL_KEY = "level"
private const val FACE_DIR_KEY = "face"
private const val WANDER_RANGE_KEY = "wander"

private val FACE_DIRECTIONS = mapOf(
    "S" to Direction.South,
    "SW" to Direction.SouthWest,
    "W" to Direction.West,
    "NW" to Direction.NorthWest,
    "N" to Direction.North,
    "NE" to Direction.NorthEast,
    "E" to Direction.East,
    "SE" to Direction.SouthEast
)

private const val DEFAULT_WANDER = 0
private val DEFAULT_FACE = FACE_DIRECTIONS.keys.first()

class NpcSpawnLoader @Inject constructor(
    override val mapper: ObjectMapper,
    private val files: NamedConfigFileMap,
    private val types: NpcTypeList,
    private val names: NpcNameMap,
    private val npcList: NpcList
) : ConfigFileLoader<NpcSpawnConfig> {

    fun spawnAll() {
        val files = files.getValue(DefaultExtensions.NPC_SPAWNS)
        val spawns = loadAll(files)
        spawns.forEach(::spawn)
        logger.info { "Loaded ${spawns.size} npc ${"spawn".toPlural(spawns.size)}" }
    }

    fun spawn(config: NpcSpawnConfig) {
        val type = types[config.id]
        val npc = Npc(type = type).apply {
            coords = Coordinates(config.x, config.y, config.level)
            wanderRange = config.wander
            faceDirection(config.face)
        }
        npcList.register(npc)
    }

    override fun JsonNode.toConfigType(): NpcSpawnConfig {
        val name = this[NAME_KEY].asText()
        val x = this[COORDS_X_KEY].asInt()
        val y = this[COORDS_Y_KEY].asInt()
        val level = if (has(COORDS_LEVEL_KEY)) this[COORDS_LEVEL_KEY].asInt() else 0
        val faceStr = if (has(FACE_DIR_KEY)) this[FACE_DIR_KEY].asText() else DEFAULT_FACE
        val wander = if (has(WANDER_RANGE_KEY)) this[WANDER_RANGE_KEY].asInt() else DEFAULT_WANDER
        val type = names[name] ?: error("Invalid name for npc: $name")
        val face = FACE_DIRECTIONS[faceStr] ?: error("Invalid face direction key: $faceStr")
        return NpcSpawnConfig(
            id = type.id,
            x = x,
            y = y,
            level = level,
            face = face,
            wander = wander
        )
    }
}
