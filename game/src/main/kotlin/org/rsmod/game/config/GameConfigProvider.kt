package org.rsmod.game.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.rsmod.toml.Toml
import java.nio.file.Files
import java.nio.file.Path
import jakarta.inject.Inject
import jakarta.inject.Provider

public class GameConfigProvider @Inject constructor(
    @Toml private val mapper: ObjectMapper
) : Provider<GameConfig> {

    override fun get(): GameConfig {
        if (!Files.exists(CONFIG_PATH)) {
            Files.copy(EXAMPLE_CONFIG_PATH, CONFIG_PATH)
        }
        return Files.newBufferedReader(CONFIG_PATH).use {
            mapper.readValue(it, GameConfig::class.java)
        }
    }

    private companion object {

        private val CONFIG_PATH = Path.of("./config.toml")
        private val EXAMPLE_CONFIG_PATH = Path.of("./config.example.toml")
    }
}
