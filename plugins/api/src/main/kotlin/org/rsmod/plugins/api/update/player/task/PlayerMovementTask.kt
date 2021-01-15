package org.rsmod.plugins.api.update.player.task

import com.google.inject.Inject
import org.rsmod.game.model.domain.Direction
import org.rsmod.game.model.domain.repo.XteaRepository
import org.rsmod.game.model.map.Coordinates
import org.rsmod.game.model.map.MapIsolation
import org.rsmod.game.model.map.Viewport
import org.rsmod.game.model.map.viewport
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.mob.PlayerList
import org.rsmod.game.model.step.StepQueue
import org.rsmod.game.model.step.StepSpeed
import org.rsmod.game.update.task.UpdateTask
import org.rsmod.plugins.api.protocol.packet.server.RebuildNormal

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
            val rebuild = !viewport.contains(mapSquare) || coords.level != viewport.level
            if (rebuild) {
                val newViewport = coords.zone().viewport(mapIsolation)
                val rebuildNormal = RebuildNormal(
                    gpi = null,
                    playerZone = coords.zone(),
                    viewport = newViewport,
                    xteas = xteasRepository
                )
                player.write(rebuildNormal)
                player.viewport = Viewport(coords.level, newViewport)
            }
        }
    }

    private fun Player.pollSteps() {
        val coordinates = steps.pollSteps(speed)
        val direction = directionBetween(
            if (coordinates.size == 1) coords else coordinates[coordinates.lastIndex - 1],
            coordinates.last()
        )
        movement.addAll(coordinates)
        coords = coordinates.last()
        faceDirection = direction
    }

    private fun StepQueue.pollSteps(speed: StepSpeed): List<Coordinates> = when (speed) {
        StepSpeed.Run -> listOfNotNull(poll(), poll())
        StepSpeed.Walk -> listOfNotNull(poll())
    }

    private fun directionBetween(start: Coordinates, end: Coordinates): Direction {
        val diffX = end.x - start.x
        val diffY = end.y - start.y
        return when {
            diffX > 0 && diffY > 0 -> Direction.NorthEast
            diffX > 0 && diffY == 0 -> Direction.East
            diffX > 0 && diffY < 0 -> Direction.SouthEast
            diffX < 0 && diffY > 0 -> Direction.NorthWest
            diffX < 0 && diffY == 0 -> Direction.West
            diffX < 0 && diffY < 0 -> Direction.SouthWest
            diffX == 0 && diffY > 0 -> Direction.North
            else -> Direction.South
        }
    }
}
