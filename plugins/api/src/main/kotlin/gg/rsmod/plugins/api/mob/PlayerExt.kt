package gg.rsmod.plugins.api.mob

import gg.rsmod.game.model.map.Coordinates
import gg.rsmod.game.model.mob.Player
import gg.rsmod.game.model.step.StepSpeed
import gg.rsmod.plugins.core.protocol.packet.MapMove

fun Player.moveTo(destination: Coordinates, speed: StepSpeed = this.speed) {
    val action = MapMove(this, destination, speed)
    actionBus.publish(action)
}
