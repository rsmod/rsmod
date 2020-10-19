package gg.rsmod.plugins.api.model.mob.player

import gg.rsmod.game.model.domain.Direction
import gg.rsmod.game.model.map.Coordinates
import gg.rsmod.game.model.mob.Player
import gg.rsmod.game.model.step.StepSpeed
import gg.rsmod.plugins.api.update.of
import gg.rsmod.plugins.core.protocol.packet.MapMove
import gg.rsmod.plugins.core.protocol.update.AppearanceMask
import gg.rsmod.plugins.core.protocol.update.DirectionMask

fun Player.moveTo(destination: Coordinates, speed: StepSpeed = this.speed) {
    val action = MapMove(this, destination, speed)
    actionBus.publish(action)
}

fun Player.updateAppearance() {
    val mask = AppearanceMask.of(this)
    entity.updates.add(mask)
}

fun Player.faceDirection(direction: Direction) {
    faceDirection = direction
    val mask = DirectionMask.of(this)
    entity.updates.add(mask)
}
