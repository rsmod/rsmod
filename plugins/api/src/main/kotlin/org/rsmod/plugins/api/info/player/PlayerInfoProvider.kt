package org.rsmod.plugins.api.info.player

import jakarta.inject.Inject
import jakarta.inject.Provider
import org.rsmod.game.model.mob.list.PlayerList
import org.rsmod.plugins.info.player.PlayerInfo

public class PlayerInfoProvider @Inject constructor(
    private val players: PlayerList
) : Provider<PlayerInfo> {

    override fun get(): PlayerInfo {
        return PlayerInfo(players.capacity)
    }
}
