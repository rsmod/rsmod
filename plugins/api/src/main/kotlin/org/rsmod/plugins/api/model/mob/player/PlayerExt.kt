package org.rsmod.plugins.api.model.mob.player

import org.rsmod.game.model.domain.Direction
import org.rsmod.game.model.item.container.ItemContainer
import org.rsmod.game.model.map.Coordinates
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.move.MovementSpeed
import org.rsmod.game.model.stat.Stat
import org.rsmod.game.model.stat.StatKey
import org.rsmod.game.model.ui.Component
import org.rsmod.game.model.vars.type.VarbitType
import org.rsmod.game.model.vars.type.VarpType
import org.rsmod.plugins.api.protocol.packet.MapMove
import org.rsmod.plugins.api.protocol.packet.MoveType
import org.rsmod.plugins.api.protocol.packet.server.MessageGame
import org.rsmod.plugins.api.protocol.packet.server.MinimapFlagSet
import org.rsmod.plugins.api.protocol.packet.server.RunClientScript
import org.rsmod.plugins.api.protocol.packet.server.UpdateInvFull
import org.rsmod.plugins.api.protocol.packet.server.UpdateRunEnergy
import org.rsmod.plugins.api.protocol.packet.server.UpdateStat
import org.rsmod.plugins.api.protocol.packet.server.VarpLarge
import org.rsmod.plugins.api.protocol.packet.server.VarpSmall
import org.rsmod.plugins.api.protocol.packet.update.AppearanceMask
import org.rsmod.plugins.api.protocol.packet.update.DirectionMask
import org.rsmod.plugins.api.update.player.mask.of
import org.rsmod.plugins.api.util.extractBitValue
import org.rsmod.plugins.api.util.withBitValue

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

fun Player.clearMinimapFlag() {
    sendMinimapFlag(-1, -1)
}

fun Player.sendMessage(text: String, type: Int = MessageType.GAME, username: String? = null) {
    write(MessageGame(type, text, username))
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

fun Player.sendItemContainer(key: Int? = null, component: Component? = null, container: ItemContainer) {
    check(key != null || component == null) { "Container key and/or component must be set." }
    val packet = UpdateInvFull(key ?: -1, component?.packed ?: -1, container)
    write(packet)
}

fun Player.sendVarp(varp: Int, value: Int) {
    val packet = when (value) {
        in Byte.MIN_VALUE..Byte.MAX_VALUE -> VarpSmall(varp, value)
        else -> VarpLarge(varp, value)
    }
    write(packet)
}

fun Player.sendStat(key: StatKey, stat: Stat) {
    write(UpdateStat(key.id, stat.currLevel, stat.experience.toInt()))
}

fun Player.runClientScript(id: Int, vararg args: Any) {
    write(RunClientScript(id, *args))
}

fun Player.getVarp(type: VarpType): Int {
    return varpMap[type.id] ?: 0
}

fun Player.setVarp(type: VarpType, value: Int) {
    varpMap[type.id] = value
}

fun Player.toggleVarp(type: VarpType, value1: Int = 0, value2: Int = 1) {
    val newValue = if (getVarp(type) == value1) value2 else value1
    setVarp(type, newValue)
}

fun Player.getVarbit(type: VarbitType): Int {
    val varpValue = varpMap[type.varp] ?: 0
    return varpValue.extractBitValue(type.lsb, type.msb)
}

fun Player.setVarbit(type: VarbitType, value: Int) {
    val curValue = varpMap[type.varp] ?: 0
    val newValue = curValue.withBitValue(type.lsb, type.msb, value)
    varpMap[type.varp] = newValue
}

fun Player.toggleVarbit(type: VarbitType, value1: Int = 0, value2: Int = 1) {
    val newValue = if (getVarbit(type) == value1) value2 else value1
    setVarbit(type, newValue)
}
