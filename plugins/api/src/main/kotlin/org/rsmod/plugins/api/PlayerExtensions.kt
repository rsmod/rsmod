package org.rsmod.plugins.api

import com.github.michaelbull.logging.InlineLogger
import org.rsmod.game.map.Coordinates
import org.rsmod.game.model.mob.Player
import org.rsmod.plugins.api.model.MessageGameType
import org.rsmod.plugins.api.model.event.TypePlayerEvent
import org.rsmod.plugins.api.model.event.TypePlayerKeyedEvent
import org.rsmod.plugins.api.move.MoveSpeed
import org.rsmod.plugins.api.net.downstream.MessageGame
import org.rsmod.plugins.api.net.downstream.MinimapFlagSet
import org.rsmod.plugins.api.net.info.ExtendedPlayerInfo

private val logger = InlineLogger()

public fun Player.message(
    text: String,
    type: MessageGameType = MessageGameType.GameMessage,
    username: String? = null
) {
    downstream += MessageGame(text, username, type.id)
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
