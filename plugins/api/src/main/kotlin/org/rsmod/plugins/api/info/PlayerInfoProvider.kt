package org.rsmod.plugins.api.info

import org.rsmod.game.model.mob.list.PlayerList
import org.rsmod.plugins.info.PlayerInfo
import javax.inject.Inject
import javax.inject.Provider

public class PlayerInfoProvider @Inject constructor(
    private val players: PlayerList
) : Provider<PlayerInfo> {

    override fun get(): PlayerInfo {
        return PlayerInfo(players.capacity)
    }
}
