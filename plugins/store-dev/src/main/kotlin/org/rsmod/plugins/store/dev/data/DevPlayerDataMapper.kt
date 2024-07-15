package org.rsmod.plugins.store.dev.data

import org.rsmod.game.config.GameConfig
import org.rsmod.game.map.Coordinates
import org.rsmod.game.model.mob.Player
import org.rsmod.plugins.store.player.PlayerDataMapper
import org.rsmod.plugins.store.player.PlayerDataRequest
import org.rsmod.plugins.store.player.PlayerDataResponse
import jakarta.inject.Inject

public class DevPlayerDataMapper @Inject constructor(
    private val config: GameConfig
) : PlayerDataMapper<DevPlayerData>(DevPlayerData::class.java) {

    override fun serialize(player: Player): DevPlayerData {
        return DevPlayerData(
            username = player.username,
            displayName = player.displayName,
            coords = intArrayOf(player.coords.x, player.coords.z, player.coords.level)
        )
    }

    override fun deserialize(req: PlayerDataRequest, data: DevPlayerData): PlayerDataResponse {
        val player = Player().apply {
            username = data.username
            displayName = data.displayName
            coords = data.coords.toCoordinates()
        }
        return PlayerDataResponse.Success.ExistingPlayer(player)
    }

    override fun createPlayer(req: PlayerDataRequest): Player {
        return Player().apply {
            username = req.username
            displayName = req.username
            coords = config.spawn
        }
    }

    private fun IntArray.toCoordinates() = when (size) {
        2 -> Coordinates(this[0], this[1])
        3 -> Coordinates(this[0], this[1], this[2])
        else -> error("Invalid coordinate values: ${contentToString()}.")
    }
}
