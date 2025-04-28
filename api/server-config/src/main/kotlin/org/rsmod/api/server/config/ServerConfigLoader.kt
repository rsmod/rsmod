package org.rsmod.api.server.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.notExists
import kotlin.io.path.writeText
import org.rsmod.api.parsers.toml.Toml

public class ServerConfigLoader @Inject constructor(@Toml private val objectMapper: ObjectMapper) {
    private val logger = InlineLogger()

    public fun loadOrCreate(file: Path): ServerConfig =
        if (file.exists()) {
            load(file)
        } else {
            create(file)
        }

    public fun load(file: Path): ServerConfig =
        objectMapper.readValue(file.toFile(), ServerConfig::class.java)

    public fun create(file: Path): ServerConfig {
        require(file.notExists()) { "File already exists: ${file.toAbsolutePath()}" }
        val config = createDefault()
        val contents = objectMapper.writeValueAsString(config.toWriteConfig())
        file.writeText(contents)
        logger.info { "Created default server config in file: $file" }
        return config
    }

    private fun createDefault(): ServerConfig {
        return ServerConfig(realm = DEFAULT_REALM, world = DEFAULT_WORLD, firstLaunch = true)
    }

    // We want to strip the `firstLaunch` flag in the config that will be written to disk.
    private fun ServerConfig.toWriteConfig() = copy(firstLaunch = false)

    private companion object {
        private const val DEFAULT_REALM = "dev"
        private const val DEFAULT_WORLD = 1
    }
}
