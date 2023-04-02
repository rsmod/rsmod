package org.rsmod.plugins.testing

import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.mob.list.PlayerList

public class GameTestScope {

    public val playerList: PlayerList = PlayerList()

    public fun withPlayer(
        player: Player = Player(),
        action: Player.() -> Unit
    ) {
        val index = playerList.nextAvailableIndex() ?: error("No available index.")
        playerList[index] = player
        action(player)
        playerList[index] = null
        // TODO: would we ever want [MobList.lastUsedIndex] reset here?
    }
}
