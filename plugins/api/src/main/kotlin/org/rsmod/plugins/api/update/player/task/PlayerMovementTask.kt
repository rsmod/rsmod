package org.rsmod.plugins.api.update.player.task

import com.google.inject.Inject
import org.rsmod.game.collision.CollisionMap
import org.rsmod.game.model.domain.Direction
import org.rsmod.game.model.domain.repo.XteaRepository
import org.rsmod.game.model.map.Coordinates
import org.rsmod.game.model.map.MapIsolation
import org.rsmod.game.model.map.BuildArea
import org.rsmod.game.model.map.Viewport
import org.rsmod.game.model.map.viewport
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.mob.PlayerList
import org.rsmod.game.model.step.StepQueue
import org.rsmod.game.model.step.StepSpeed
import org.rsmod.game.update.task.UpdateTask
import org.rsmod.plugins.api.collision.canTraverse
import org.rsmod.plugins.api.model.map.of
import org.rsmod.plugins.api.model.mob.player.clearMinimapFlag
import org.rsmod.plugins.api.model.mob.player.sendRunEnergy
import org.rsmod.plugins.api.protocol.packet.server.RebuildNormal

class PlayerMovementTask @Inject constructor(
    private val playerList: PlayerList,
    private val xteasRepository: XteaRepository,
    private val mapIsolation: MapIsolation,
    private val collision: CollisionMap
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
            val rebuild = player.shouldRebuildMap()
            if (rebuild) {
                val newViewport = coords.zone().viewport(mapIsolation)
                val rebuildNormal = RebuildNormal(
                    gpi = null,
                    playerZone = coords.zone(),
                    viewport = newViewport,
                    xteas = xteasRepository
                )
                player.write(rebuildNormal)
                player.viewport = Viewport.of(coords, newViewport)
            }
        }
    }

    private fun Player.pollSteps() {
        if (speed == StepSpeed.Run && runEnergy <= 0) {
            speed = StepSpeed.Walk
        }
        val coordinates = steps.pollSteps(coords, speed)
        if (coordinates.isEmpty()) {
            clearMinimapFlag()
            return
        }
        val direction = directionBetween(
            if (coordinates.size < 2) coords else coordinates[coordinates.lastIndex - 1],
            coordinates.last()
        )
        movement.addAll(coordinates)
        coords = coordinates.last()
        faceDirection = direction
        if (coordinates.size > 1) {
            drainRunEnergy()
        }
    }

    private fun StepQueue.pollSteps(src: Coordinates, speed: StepSpeed): List<Coordinates> {
        val steps = mutableListOf<Coordinates>()
        var lastCoords = src
        for (i in 0 until speed.stepCount) {
            val dest = poll()
            val dir = directionBetween(lastCoords, dest)
            if (!noclip && !collision.canTraverse(lastCoords, dir)) {
                break
            }
            steps.add(dest)
            lastCoords = dest
        }
        return steps
    }

    private fun Player.drainRunEnergy() {
        sendRunEnergy()
    }

    private fun Player.shouldRebuildMap(): Boolean {
        val dx = coords.x - viewport.base.x
        val dy = coords.y - viewport.base.y
        return dx < BuildArea.REBUILD_BOUNDARY || dx >= BuildArea.SIZE - BuildArea.REBUILD_BOUNDARY
                || dy < BuildArea.REBUILD_BOUNDARY || dy >= BuildArea.SIZE - BuildArea.REBUILD_BOUNDARY
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

    private val StepSpeed.stepCount: Int
        get() = when (this) {
            StepSpeed.Run -> 2
            StepSpeed.Walk -> 1
        }
}
