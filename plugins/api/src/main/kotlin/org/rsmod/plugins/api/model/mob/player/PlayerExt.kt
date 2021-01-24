package org.rsmod.plugins.api.model.mob.player

import org.rsmod.game.model.domain.Direction
import org.rsmod.game.model.map.Coordinates
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.step.StepSpeed
import org.rsmod.plugins.api.protocol.packet.MapMove
import org.rsmod.plugins.api.protocol.packet.update.AppearanceMask
import org.rsmod.plugins.api.protocol.packet.update.DirectionMask
import org.rsmod.plugins.api.update.player.mask.of

fun Player.message(message: String) {
    // TODO
}

fun Player.moveTo(destination: Coordinates, speed: StepSpeed = this.speed) {
    val action = MapMove(this, destination, speed)
    actionBus.publish(action)
}

fun Player.updateAppearance() {
    val mask = AppearanceMask.of(this)
    entity.updates.add(mask)
}

fun Player.faceDirection(direction: Direction) {
    val mask = DirectionMask.of(this, direction)
    entity.updates.add(mask)
    faceDirection = direction
}
