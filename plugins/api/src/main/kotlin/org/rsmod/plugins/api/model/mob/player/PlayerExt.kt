package org.rsmod.plugins.api.model.mob.player

import org.rsmod.game.model.domain.Direction
import org.rsmod.game.model.map.Coordinates
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.move.MovementSpeed
import org.rsmod.game.model.stat.Stat
import org.rsmod.game.model.stat.StatKey
import org.rsmod.plugins.api.model.stat.StatLevelEvent
import org.rsmod.plugins.api.model.stat.Stats
import org.rsmod.plugins.api.protocol.packet.MapMove
import org.rsmod.plugins.api.protocol.packet.MoveType
import org.rsmod.plugins.api.protocol.packet.server.MessageGame
import org.rsmod.plugins.api.protocol.packet.server.MinimapFlagSet
import org.rsmod.plugins.api.protocol.packet.server.UpdateRunEnergy
import org.rsmod.plugins.api.protocol.packet.server.UpdateStat
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

fun Player.addXp(key: StatKey, xp: Double) {
    val stat = stats.getValue(key)
    val totalXp = (stat.experience + xp).coerceAtMost(Stats.MAX_EXP.toDouble())
    if (stat.experience == totalXp) return

    /* level up when applicable */
    val currLevel = stat.currLevel
    val baseLevel = Stats.levelForExp(stat.experience)
    val newLevel = Stats.levelForExp(totalXp)
    if (currLevel != newLevel) {
        /* set current level when necessary */
        if (currLevel == baseLevel) {
            stat.currLevel = newLevel
        }
        /* publish level up event */
        val event = StatLevelEvent(key, currLevel, newLevel, totalXp)
        eventBus.publish(event)
    }

    /* set stat experience */
    setXp(stat, key, totalXp)
}

fun Player.sendMessage(text: String, type: Int = MessageType.GAME, username: String? = null) {
    write(MessageGame(type, text, username))
}

fun Player.sendRunEnergy(energy: Int = runEnergy.toInt()) {
    write(UpdateRunEnergy(energy))
}

fun Player.setXp(stat: Stat, key: StatKey, xp: Double) {
    stat.experience = xp
    write(UpdateStat(key.id, stat.currLevel, stat.experience.toInt()))
}

fun Player.sendMinimapFlag(x: Int, y: Int) {
    val base = viewport.base
    val lx = (x - base.x)
    val ly = (y - base.y)
    write(MinimapFlagSet(lx, ly))
}
