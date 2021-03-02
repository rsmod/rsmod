package org.rsmod.plugins.api.model.mob.player

import org.rsmod.game.model.domain.Direction
import org.rsmod.game.model.map.Coordinates
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.move.MovementSpeed
import org.rsmod.plugins.api.protocol.packet.MapMove
import org.rsmod.plugins.api.protocol.packet.MoveType
import org.rsmod.plugins.api.protocol.packet.server.MinimapFlagSet
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
    val base = viewport.base
    val lx = (x - base.x)
    val ly = (y - base.y)
    write(MinimapFlagSet(lx, ly))
}

fun Player.clearMinimapFlag() = sendMinimapFlag(-1, -1)

fun Player.moveTo(destination: Coordinates, speed: MovementSpeed = this.speed, noclip: Boolean = false) {
    val type = when (speed) {
        MovementSpeed.Walk -> MoveType.ForceWalk
        MovementSpeed.Run -> MoveType.ForceRun
    }
    val action = MapMove(this, destination, type, noclip)
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
