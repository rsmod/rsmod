package org.rsmod.game.config.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import org.rsmod.game.config.GameConfig
import java.nio.file.Path

public object JacksonGameConfigDeserializer : StdDeserializer<GameConfig>(GameConfig::class.java) {

    private const val DEFAULT_NAME = "RS Mod"
    private const val DEFAULT_DATA_PATH = ".data"
    private const val DEFAULT_BUILD_MAJOR = 209
    private const val DEFAULT_BUILD_MINOR = 1
    private const val DEFAULT_WORLD_ID = 1

    private val ROOT_PATH = Path.of("./")

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): GameConfig {
        val node = ctxt.readTree(p)
        val name = node.get("name").asText(DEFAULT_NAME)
        val world = node.get("world_id").asInt(DEFAULT_WORLD_ID)
        val dataPath = node.get("data_path").asText(DEFAULT_DATA_PATH)
        val build = node.readGameBuild()
        return GameConfig(
            name = name,
            world = world,
            build = build,
            dataPath = ROOT_PATH.resolve(dataPath).toAbsolutePath()
        )
    }

    private fun JsonNode.readGameBuild(): GameConfig.Build = if (has("revision")) {
        val major = get("revision").asInt(DEFAULT_BUILD_MAJOR)
        GameConfig.Build(major, DEFAULT_BUILD_MINOR)
    } else {
        val major = get("revision.major").asInt(DEFAULT_BUILD_MAJOR)
        val minor = get("revision.minor").asInt(DEFAULT_BUILD_MINOR)
        GameConfig.Build(major, minor)
    }
}
