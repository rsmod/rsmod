package org.rsmod.plugins.api.update

import org.rsmod.game.model.mob.Mob
import org.rsmod.plugins.api.protocol.packet.update.DirectionMask
import org.rsmod.plugins.api.protocol.packet.update.MovementPermMask
import org.rsmod.plugins.api.protocol.packet.update.MovementTempMask

fun DirectionMask.Companion.of(mob: Mob, orientation: Int = mob.orientation): DirectionMask {
    return DirectionMask(orientation)
}

fun MovementTempMask.Companion.of(type: Int): MovementTempMask {
    return MovementTempMask(type)
}

fun MovementPermMask.Companion.of(type: Int): MovementPermMask {
    return MovementPermMask(type)
}
