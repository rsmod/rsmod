package org.rsmod.plugins.store.dev.data

import com.fasterxml.jackson.databind.ObjectMapper
import org.rsmod.game.config.GameConfig
import org.rsmod.game.model.mob.Player
import org.rsmod.json.Json
import org.rsmod.plugins.store.player.PlayerCodec
import org.rsmod.plugins.store.player.PlayerCodecData
import org.rsmod.plugins.store.player.PlayerDataMapper
import org.rsmod.plugins.store.player.PlayerDataRequest
import org.rsmod.plugins.store.player.PlayerDataResponse
import java.nio.file.Files
import java.nio.file.Path
import jakarta.inject.Inject

private const val RELATIVE_PATH = "dev/saves"
private const val FILE_EXT = "json"

public class DevJsonPlayerCodec<T : PlayerCodecData> @Inject constructor(
    @Json private val jsonMapper: ObjectMapper,
    private val playerMapper: PlayerDataMapper<T>,
    private val config: GameConfig
) : PlayerCodec {

    override fun serialize(player: Player) {
        val path = path(player.username)
        val mapped = playerMapper.serialize(player)
        Files.newBufferedWriter(path).use {
            jsonMapper.writeValue(it, mapped)
        }
    }

    override fun deserialize(req: PlayerDataRequest): PlayerDataResponse {
        val path = path(req.username)
        if (!Files.exists(path)) {
            val client = playerMapper.createPlayer(req)
            return PlayerDataResponse.Success.NewPlayer(client)
        }
        Files.newBufferedReader(path).use {
            return try {
                val data = jsonMapper.readValue(it, playerMapper.type)
                playerMapper.deserialize(req, data)
            } catch (t: Throwable) {
                PlayerDataResponse.Exception(t)
            }
        }
    }

    private fun path(username: String): Path {
        val directory = config.dataPath.resolve(RELATIVE_PATH)
        if (!Files.exists(directory)) {
            Files.createDirectories(directory)
        }
        return directory.resolve("$username.$FILE_EXT")
    }
}
