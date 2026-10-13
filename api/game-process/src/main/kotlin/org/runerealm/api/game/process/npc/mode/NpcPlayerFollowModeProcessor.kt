package org.runerealm.api.game.process.npc.mode

import jakarta.inject.Inject
import org.runerealm.api.route.StepFactory
import org.runerealm.game.entity.Npc
import org.runerealm.game.entity.Player
import org.runerealm.game.entity.PlayerList
import org.runerealm.game.movement.RouteRequestPathingEntity
import org.runerealm.routefinder.collision.CollisionFlagMap

public class NpcPlayerFollowModeProcessor
@Inject
constructor(
    private val playerList: PlayerList,
    private val collision: CollisionFlagMap,
    private val stepFactory: StepFactory,
) {
    public fun process(npc: Npc) {
        val following = npc.facingTarget(playerList)
        if (following == null) {
            npc.resetMode()
        } else if (!npc.inValidDistance(following)) {
            npc.teleportTo(following)
        } else {
            npc.followTarget(following)
        }
    }

    private fun Npc.inValidDistance(target: Player): Boolean =
        isWithinDistance(target, VALID_DISTANCE)

    private fun Npc.teleportTo(target: Player): Unit = teleport(collision, target.coords)

    private fun Npc.followTarget(target: Player) {
        // TODO: Create a general, public function for this. (moving towards a PathingEntity)
        val request = RouteRequestPathingEntity(target.avatar)
        routeRequest = request
    }

    public companion object {
        public const val VALID_DISTANCE: Int = 15
    }
}
