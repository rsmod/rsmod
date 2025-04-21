package org.rsmod.api.server.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.notExists
import kotlin.io.path.writeText
import org.rsmod.api.parsers.toml.Toml
import org.rsmod.api.realm.Realm
import org.rsmod.map.CoordGrid

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

    private fun createDefault(): ServerConfig =
        ServerConfig(
            name = DEFAULT_NAME,
            world =
                WorldConfig(
                    realm = DEFAULT_REALM,
                    worldId = DEFAULT_WORLD,
                    requireRegistration = false,
                    ignorePasswords = true,
                    autoAssignDisplayName = true,
                ),
            game =
                GameConfig(
                    xpRate = DEFAULT_XP_RATE,
                    spawn = DEFAULT_SPAWN,
                    respawn = DEFAULT_RESPAWN,
                ),
            meta = MetaConfig(firstLaunch = true),
        )

    // We want to strip the `firstLaunch` flag in the config that will be written to disk.
    private fun ServerConfig.toWriteConfig() = copy(meta = meta.copy(firstLaunch = false))

    private companion object {
        private const val DEFAULT_NAME = "RS Mod"
        private const val DEFAULT_WORLD = 1
        private const val DEFAULT_XP_RATE = 100

        private val DEFAULT_REALM = Realm.Dev
        private val DEFAULT_SPAWN = CoordGrid(0, 50, 50, 21, 18)
        private val DEFAULT_RESPAWN = CoordGrid(0, 50, 50, 21, 18)
    }
}
