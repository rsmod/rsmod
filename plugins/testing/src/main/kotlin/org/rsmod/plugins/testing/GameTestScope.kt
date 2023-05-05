package org.rsmod.plugins.testing

import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.mob.list.PlayerList
import org.rsmod.game.pathfinder.collision.CollisionFlagMap
import org.rsmod.plugins.api.pathfinder.BoundValidator
import org.rsmod.plugins.api.pathfinder.PathValidator
import org.rsmod.plugins.api.pathfinder.RayCastFactory
import org.rsmod.plugins.api.pathfinder.RouteFactory
import org.rsmod.plugins.api.pathfinder.StepFactory

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

    public fun withCollisionState(
        collision: CollisionFlagMap = CollisionFlagMap(),
        action: (GameCollisionState) -> Unit
    ) {
        val rf = RouteFactory(collision)
        val rcf = RayCastFactory(collision)
        val sf = StepFactory(collision)
        val pv = PathValidator(collision)
        val bv = BoundValidator(collision)
        action(GameCollisionState(collision, rf, rcf, sf, pv, bv))
    }
}
