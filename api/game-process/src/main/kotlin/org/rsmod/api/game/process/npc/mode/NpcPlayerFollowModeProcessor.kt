package org.rsmod.api.game.process.npc.mode

import jakarta.inject.Inject
import org.rsmod.api.route.StepFactory
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.PlayerList
import org.rsmod.game.movement.RouteRequestPathingEntity
import org.rsmod.pathfinder.collision.CollisionFlagMap

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
