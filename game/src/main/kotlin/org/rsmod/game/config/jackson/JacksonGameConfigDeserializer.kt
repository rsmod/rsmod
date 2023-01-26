package org.rsmod.game.config.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import org.rsmod.game.config.GameConfig
import java.nio.file.Path

public object JacksonGameConfigDeserializer : StdDeserializer<GameConfig>(GameConfig::class.java) {

    private const val DEFAULT_NAME = "RS Mod"
    private const val DEFAULT_DATA_PATH = ".data"
    private const val DEFAULT_WORLD_ID = 1

    private val ROOT_PATH = Path.of("./")

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): GameConfig {
        val node = ctxt.readTree(p)
        val name = node.get("name").asText(DEFAULT_NAME)
        val world = node.get("world_id").asInt(DEFAULT_WORLD_ID)
        val dataPath = node.get("data_path").asText(DEFAULT_DATA_PATH)
        return GameConfig(
            name = name,
            world = world,
            dataPath = ROOT_PATH.resolve(dataPath).toAbsolutePath()
        )
    }
}
