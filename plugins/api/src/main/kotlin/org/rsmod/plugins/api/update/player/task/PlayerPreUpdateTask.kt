package org.rsmod.plugins.api.update.player.task

import org.rsmod.game.collision.CollisionMap
import org.rsmod.game.model.client.PlayerEntity
import org.rsmod.game.model.domain.repo.XteaRepository
import org.rsmod.game.model.map.BuildArea
import org.rsmod.game.model.map.MapIsolation
import org.rsmod.game.model.map.Viewport
import org.rsmod.game.model.map.viewport
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.mob.PlayerList
import org.rsmod.game.model.move.MovementSpeed
import org.rsmod.game.update.task.UpdateTask
import org.rsmod.plugins.api.model.angle
import org.rsmod.plugins.api.model.map.of
import org.rsmod.plugins.api.model.mob.player.clearMinimapFlag
import org.rsmod.plugins.api.model.mob.player.sendRunEnergy
import org.rsmod.plugins.api.model.mob.player.updateAppearance
import org.rsmod.plugins.api.protocol.packet.server.RebuildNormal
import org.rsmod.plugins.api.protocol.packet.update.MovementPermMask
import org.rsmod.plugins.api.protocol.packet.update.MovementTempMask
import org.rsmod.plugins.api.update.of
import org.rsmod.plugins.api.update.pollSteps
import org.rsmod.plugins.api.update.speed
import org.rsmod.plugins.api.update.stepCount
import javax.inject.Inject

class PlayerPreUpdateTask @Inject constructor(
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
            player.entityUpdate()
            player.movementUpdate()
        }
    }

    private fun Player.entityUpdate(
        oldEntity: PlayerEntity = snapshot.entity,
        curEntity: PlayerEntity = entity
    ) {
        // TODO: other flags that require an appearance update
        val appearanceUpdate = oldEntity.username != curEntity.username
        if (appearanceUpdate) {
            updateAppearance()
        }
    }

    private fun Player.movementUpdate() {
        processMovement()
        val rebuild = shouldRebuildMap()
        if (rebuild) {
            val newViewport = coords.zone().viewport(mapIsolation)
            val rebuildNormal = RebuildNormal(
                gpi = null,
                playerZone = coords.zone(),
                viewport = newViewport,
                xteas = xteasRepository
            )
            viewport = Viewport.of(coords, newViewport)
            write(rebuildNormal)
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
        movement.pollSteps(coords, speed(), collision)
        val lastStep = movement.nextSteps.lastOrNull()
        if (lastStep == null) {
            clearMinimapFlag()
            return
        }
        coords = lastStep.dest
        orientation = lastStep.dir.angle
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
}
