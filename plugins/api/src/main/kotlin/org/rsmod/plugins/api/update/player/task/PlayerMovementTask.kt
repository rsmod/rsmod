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
import org.rsmod.game.model.move.MovementSpeed
import org.rsmod.game.model.move.MovementQueue
import org.rsmod.game.model.move.Step
import org.rsmod.game.update.task.UpdateTask
import org.rsmod.plugins.api.collision.canTraverse
import org.rsmod.plugins.api.model.map.of
import org.rsmod.plugins.api.model.mob.player.clearMinimapFlag
import org.rsmod.plugins.api.model.mob.player.sendRunEnergy
import org.rsmod.plugins.api.protocol.packet.server.RebuildNormal
import org.rsmod.plugins.api.protocol.packet.update.MovementPermMask
import org.rsmod.plugins.api.protocol.packet.update.MovementTempMask
import org.rsmod.plugins.api.update.player.mask.of

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
            player.processMovement()
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

    private fun Player.processMovement() {
        pollSteps()
        if (displace) {
            displace()
        } else if (movement.nextSteps.isNotEmpty()) {
            updateMovementSpeed()
        }
    }

    private fun Player.pollSteps() {
        if (movement.isEmpty()) return
        if (speed() == MovementSpeed.Run && runEnergy <= 0) {
            movement.speed = null
            speed = MovementSpeed.Walk
        }
        movement.pollSteps(coords, speed())
        val lastStep = movement.nextSteps.lastOrNull()
        if (lastStep == null) {
            clearMinimapFlag()
            return
        }
        coords = lastStep.dest
        faceDirection = lastStep.dir
    }

    private fun MovementQueue.pollSteps(src: Coordinates, speed: MovementSpeed) {
        var lastCoords = src
        for (i in 0 until speed.stepCount) {
            val dest = poll() ?: break
            val dir = directionBetween(lastCoords, dest)
            if (!noclip && !collision.canTraverse(lastCoords, dir)) {
                break
            }
            val step = Step(dest, dir)
            nextSteps.add(step)
            lastCoords = dest
        }
    }

    private fun Player.updateMovementSpeed() {
        val movementSpeed = if (movement.nextSteps.size <= 1) MovementSpeed.Walk else MovementSpeed.Run
        if (movementSpeed != MovementSpeed.Walk) {
            drainRunEnergy()
        }
        if (movementSpeed != lastSpeed) {
            val mask = MovementPermMask.of(movementSpeed.stepCount)
            entity.updates.add(mask)
            lastSpeed = movementSpeed
        }
    }

    private fun Player.displace() {
        val mask = MovementTempMask.of(127)
        entity.updates.add(mask)
    }

    private fun Player.drainRunEnergy() {
        // TODO: drain run energy
        sendRunEnergy()
    }

    private fun Player.shouldRebuildMap(): Boolean {
        val dx = coords.x - viewport.base.x
        val dy = coords.y - viewport.base.y
        return dx < BuildArea.REBUILD_BOUNDARY || dx >= BuildArea.SIZE - BuildArea.REBUILD_BOUNDARY ||
            dy < BuildArea.REBUILD_BOUNDARY || dy >= BuildArea.SIZE - BuildArea.REBUILD_BOUNDARY
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

    private fun Player.speed(): MovementSpeed {
        return movement.speed ?: speed
    }

    private val MovementSpeed.stepCount: Int
        get() = when (this) {
            MovementSpeed.Run -> 2
            MovementSpeed.Walk -> 1
        }
}
