package org.rsmod.plugins.api

import com.github.michaelbull.logging.InlineLogger
import org.rsmod.game.map.Coordinates
import org.rsmod.game.model.mob.Player
import org.rsmod.plugins.api.model.MessageGameType
import org.rsmod.plugins.api.model.event.TypePlayerEvent
import org.rsmod.plugins.api.model.event.TypePlayerKeyedEvent
import org.rsmod.plugins.api.model.ui.StandardGameframe
import org.rsmod.plugins.api.movement.MoveSpeed
import org.rsmod.plugins.api.net.downstream.IfOpenSub
import org.rsmod.plugins.api.net.downstream.IfOpenTop
import org.rsmod.plugins.api.net.downstream.MessageGame
import org.rsmod.plugins.api.net.downstream.MinimapFlagSet
import org.rsmod.plugins.api.net.downstream.VarpLarge
import org.rsmod.plugins.api.net.downstream.VarpSmall
import org.rsmod.plugins.api.net.info.ExtendedPlayerInfo
import org.rsmod.plugins.api.util.BitUtils
import org.rsmod.plugins.cache.config.varbit.VarbitType
import org.rsmod.plugins.cache.config.varp.VarpType
import org.rsmod.plugins.types.NamedComponent
import org.rsmod.plugins.types.NamedInterface
import org.rsmod.plugins.types.NamedVarp
import kotlin.collections.set

private val logger = InlineLogger()

public fun Player.message(
    text: String,
    type: MessageGameType = MessageGameType.GameMessage,
    username: String? = null
) {
    downstream += MessageGame(text, username, type.id)
}

public fun Player.openGameframe(gameframe: StandardGameframe) {
    val topLevel = gameframe.topLevel
    val overlays = gameframe.overlays
    openTopLevel(topLevel)
    overlays.forEach {
        val overlay = NamedInterface(it.interfaceId)
        val target = NamedComponent(topLevel.id, it.child)
        openOverlay(overlay, target)
    }
}

public fun Player.openTopLevel(topLevel: NamedInterface) {
    downstream += IfOpenTop(topLevel.id)
}

public fun Player.openOverlay(overlay: NamedInterface, target: NamedComponent) {
    downstream += IfOpenSub(overlay.id, target.id, 1)
}

public fun Player.getVarp(varp: NamedVarp): Int = vars[varp.id] ?: 0

public fun Player.getVarp(type: VarpType): Int = vars[type.id] ?: 0

public fun Player.setVarp(value: Int, type: VarpType) {
    // TODO: is there a scenario where we'd want to keep
    // track of a varp that doesn't persist?
    if (type.persist) {
        vars[type.id] = value
    }
    if (type.transmit) {
        syncVarp(type.id, value)
    }
}

public fun Player.setVarp(value: Boolean, type: VarpType) {
    setVarp(if (value) 1 else 0, type)
}

public fun Player.toggleVarp(type: VarpType) {
    val state = if (getVarp(type) == 1) 0 else 1
    setVarp(state, type)
}

public fun Player.getVarbit(type: VarbitType): Int {
    val varp = vars[type.varp] ?: 0
    return BitUtils.get(varp, type.lsb..type.msb)
}

public fun Player.setVarbit(value: Int, type: VarbitType, persist: Boolean = true) {
    val modifiedValue = BitUtils.modify(
        value = vars[type.varp] ?: 0,
        bitRange = type.lsb..type.msb,
        rangeValue = value
    )
    if (persist) {
        vars[type.varp] = modifiedValue
    }
    if (type.transmit) {
        syncVarp(type.varp, modifiedValue)
    }
}

public fun Player.setVarbit(value: Boolean, type: VarbitType, persist: Boolean = true) {
    setVarbit(if (value) 1 else 0, type, persist)
}

public fun Player.syncVarp(varp: Int, value: Int) {
    val packet = when (value) {
        in Byte.MIN_VALUE..Byte.MAX_VALUE -> VarpSmall(varp, value)
        else -> VarpLarge(varp, value)
    }
    downstream += packet
}

public fun Player.setMinimapFlag(x: Int, z: Int) {
    val dx = x - buildArea.base.x
    val dz = z - buildArea.base.z
    downstream += MinimapFlagSet(dx, dz)
}

public fun Player.setMinimapFlag(coords: Coordinates) {
    setMinimapFlag(coords.x, coords.z)
}

public fun Player.clearMinimapFlag() {
    downstream += MinimapFlagSet(255, 255)
}

public fun Player.displace(destination: Coordinates) {
    coords = destination
    movement.lastStep = destination
    sendTempMovement(MoveSpeed.Displace)
}

public fun Player.setMoveSpeed(speed: MoveSpeed) {
    movement.speed = speed
    sendPermMovement(speed)
}

public fun Player.sendTempMovement(speed: MoveSpeed) {
    extendedInfo += ExtendedPlayerInfo.MoveSpeedTemp(speed.infoId)
}

public fun Player.sendPermMovement(speed: MoveSpeed) {
    extendedInfo += ExtendedPlayerInfo.MoveSpeedPerm(speed.infoId)
}

public fun <T : TypePlayerEvent> Player.publish(event: T) {
    logger.trace { "Player $this publishing event $event." }
    events += event
}

public fun <T : TypePlayerKeyedEvent> Player.publish(id: Number, event: T) {
    logger.trace { "Player $this publishing keyed event $event." }
    events.add(id.toLong(), event)
}

public fun Player.refreshBuildArea(center: Coordinates) {
    val buildArea = center.toBuildArea()
    this.buildArea = buildArea
}

private val MoveSpeed.infoId: Int get() = when (this) {
    MoveSpeed.Crawl -> 0
    MoveSpeed.Walk -> 1
    MoveSpeed.Run -> 2
    MoveSpeed.Displace -> 127
}
