package org.rsmod.api.combat.formulas

import org.rsmod.game.entity.Player
import org.rsmod.game.type.npc.UnpackedNpcType

internal fun scale(base: Int, multiplier: Int, divisor: Int): Int = (base * multiplier) / divisor

internal fun UnpackedNpcType.isSlayerTask(player: Player): Boolean {
    // TODO(combat): Resolve if type is slayer task.
    return false
}
