package gg.rsmod.plugins.core.update.player

import com.google.inject.Inject
import gg.rsmod.game.model.domain.Direction
import gg.rsmod.game.model.domain.repo.XteaRepository
import gg.rsmod.game.model.map.MapIsolation
import gg.rsmod.game.model.map.Viewport
import gg.rsmod.game.model.map.viewport
import gg.rsmod.game.model.mob.Player
import gg.rsmod.game.model.mob.PlayerList
import gg.rsmod.game.model.step.StepQueue
import gg.rsmod.game.model.step.StepSpeed
import gg.rsmod.game.update.task.UpdateTask
import gg.rsmod.plugins.core.protocol.packet.server.RebuildNormal

class PlayerMovementTask @Inject constructor(
    private val playerList: PlayerList,
    private val xteasRepository: XteaRepository,
    private val mapIsolation: MapIsolation
) : UpdateTask {

    override suspend fun execute() {
        playerList.forEach { player ->
            if (player == null) {
                return@forEach
            }
            if (player.steps.isNotEmpty()) {
                player.pollSteps()
            }
            val coords = player.coords
            val viewport = player.viewport
            val mapSquare = coords.mapSquare()
            val rebuild = !viewport.contains(mapSquare) || coords.plane != viewport.plane
            if (rebuild) {
                val newViewport = coords.zone().viewport(mapIsolation)
                val rebuildNormal = RebuildNormal(
                    gpi = null,
                    playerZone = coords.zone(),
                    viewport = newViewport,
                    xteas = xteasRepository
                )
                player.write(rebuildNormal)
                player.viewport = Viewport(coords.plane, newViewport)
            }
        }
    }

    private fun Player.pollSteps() {
        val directions = steps.pollSteps(speed)
        val translateX = directions.sumBy { it.x }
        val translateY = directions.sumBy { it.y }
        movement.addAll(directions)
        faceDirection = directions.last()
        coords = coords.translate(translateX, translateY)
    }

    private fun StepQueue.pollSteps(speed: StepSpeed): List<Direction> = when (speed) {
        StepSpeed.Run -> listOfNotNull(poll(), poll())
        StepSpeed.Walk -> listOfNotNull(poll())
    }
}
