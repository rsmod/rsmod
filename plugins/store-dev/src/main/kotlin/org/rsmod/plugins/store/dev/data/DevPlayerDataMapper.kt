package org.rsmod.plugins.store.dev.data

import org.rsmod.game.config.GameConfig
import org.rsmod.game.model.mob.Player
import org.rsmod.game.store.player.PlayerDataMapper
import org.rsmod.game.store.player.PlayerDataRequest
import org.rsmod.game.store.player.PlayerDataResponse
import javax.inject.Inject

public class DevPlayerDataMapper @Inject constructor(
    private val config: GameConfig
) : PlayerDataMapper<DevPlayerData>(DevPlayerData::class.java) {

    override fun serialize(player: Player): DevPlayerData {
        return DevPlayerData(
            username = player.username,
            displayName = player.displayName,
            coords = player.coords
        )
    }

    override fun deserialize(req: PlayerDataRequest, data: DevPlayerData): PlayerDataResponse {
        val player = Player().apply {
            username = data.username
            displayName = data.displayName
            coords = data.coords
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
}
