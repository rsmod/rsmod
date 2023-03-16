package org.rsmod.game.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import org.rsmod.game.config.GameConfig
import org.rsmod.game.map.Coordinates
import org.rsmod.game.model.GameEnv
import java.nio.file.Path

public object JacksonGameConfigDeserializer : StdDeserializer<GameConfig>(GameConfig::class.java) {

    private const val DEFAULT_NAME = "RS Mod"
    private const val DEFAULT_DATA_PATH = ".data"
    private const val DEFAULT_WORLD_ID = 1

    private val DEFAULT_GAME_ENV = GameEnv.Dev
    private val DEFAULT_SPAWN = Coordinates(3200, 3200)

    private val ROOT_PATH = Path.of("./")

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): GameConfig {
        val node = ctxt.readTree(p)
        val name = node.get("name")?.asText() ?: DEFAULT_NAME
        val world = node.get("world")?.asInt() ?: DEFAULT_WORLD_ID
        val dataPath = node.get("data_path")?.asText() ?: DEFAULT_DATA_PATH
        val env = node.get("env")?.asText()?.toGameEnv() ?: DEFAULT_GAME_ENV
        val spawn = node.get("spawn")?.toCoordinates(ctxt) ?: DEFAULT_SPAWN
        return GameConfig(
            name = name,
            world = world,
            env = env,
            spawn = spawn,
            dataPath = ROOT_PATH.resolve(dataPath).toAbsolutePath()
        )
    }

    private fun JsonNode.toCoordinates(ctxt: DeserializationContext): Coordinates? {
        return ctxt.readTreeAsValue(this, Coordinates::class.java)
    }

    private fun String.toGameEnv(): GameEnv = when (val name = lowercase()) {
        "live", "prod", "production" -> GameEnv.Prod
        "dev", "development" -> GameEnv.Dev
        "alpha", "beta", "test", "testing" -> GameEnv.Test
        else -> error("Invalid game-env value: $name")
    }
}
