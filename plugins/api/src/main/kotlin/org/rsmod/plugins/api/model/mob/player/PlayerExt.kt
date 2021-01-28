package org.rsmod.plugins.api.model.mob.player

import org.rsmod.game.model.domain.Direction
import org.rsmod.game.model.map.BuildArea
import org.rsmod.game.model.map.Coordinates
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.step.StepSpeed
import org.rsmod.plugins.api.protocol.packet.MapMove
import org.rsmod.plugins.api.protocol.packet.server.SetMapFlag
import org.rsmod.plugins.api.protocol.packet.server.UpdateRunEnergy
import org.rsmod.plugins.api.protocol.packet.update.AppearanceMask
import org.rsmod.plugins.api.protocol.packet.update.DirectionMask
import org.rsmod.plugins.api.update.player.mask.of

fun Player.sendMessage(message: String) {
    // TODO
}

fun Player.sendRunEnergy(energy: Int = runEnergy.toInt()) {
    write(UpdateRunEnergy(energy))
}

fun Player.sendMinimapFlag(x: Int, y: Int) {
    // TODO: what's with the magic values?
    val lx = (x - viewport.base.x) - 2
    val ly = (y - viewport.base.y) + 3
    write(SetMapFlag(lx, ly))
}

fun Player.clearMinimapFlag() {
    sendMinimapFlag(-1, -1)
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
