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
import org.rsmod.plugins.api.model.vars.getVarbit
import org.rsmod.plugins.api.model.vars.getVarp
import org.rsmod.plugins.api.model.vars.setVarbit
import org.rsmod.plugins.api.model.vars.setVarp
import org.rsmod.plugins.api.model.vars.toggleVarbit
import org.rsmod.plugins.api.model.vars.toggleVarp
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

fun Player.sendMessage(text: String, type: Int = MessageType.GAME_MESSAGE, username: String? = null) {
    write(MessageGame(type, text, username))
}

fun Player.sendFilteredMessage(text: String, username: String? = null) {
    sendMessage(text, MessageType.FILTERED, username)
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

fun Player.getVarp(type: VarpType): Int = varpMap.getVarp(type)

fun Player.setVarp(type: VarpType, value: Int) = varpMap.setVarp(type, value)

fun Player.setVarp(type: VarpType, flag: Boolean, falseValue: Int = 0, trueValue: Int = 1) {
    return varpMap.setVarp(type, flag, falseValue, trueValue)
}

fun Player.toggleVarp(type: VarpType, value1: Int = 0, value2: Int = 1) {
    return varpMap.toggleVarp(type, value1, value2)
}

fun Player.getVarbit(type: VarbitType): Int = varpMap.getVarbit(type)

fun Player.setVarbit(type: VarbitType, value: Int) = varpMap.setVarbit(type, value)

fun Player.setVarbit(type: VarbitType, flag: Boolean, falseValue: Int = 0, trueValue: Int = 1) {
    return varpMap.setVarbit(type, flag, falseValue, trueValue)
}

fun Player.toggleVarbit(type: VarbitType, value1: Int = 0, value2: Int = 1) {
    return varpMap.toggleVarbit(type, value1, value2)
}
