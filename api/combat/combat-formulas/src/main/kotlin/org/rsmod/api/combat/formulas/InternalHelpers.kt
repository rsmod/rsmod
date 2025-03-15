package org.rsmod.api.combat.formulas

import org.rsmod.game.entity.Player
import org.rsmod.game.type.npc.UnpackedNpcType

/**
 * Hit chance formulas internally use decimals (e.g., `1%` = `0.01`, `100%` = `1.0`). To maintain
 * consistency with other combat formulas that use whole integers, we scale them using this
 * constant.
 */
internal const val HIT_CHANCE_SCALE: Int = 10_000

internal fun scale(base: Int, multiplier: Int, divisor: Int): Int = (base * multiplier) / divisor

internal fun UnpackedNpcType.isSlayerTask(player: Player): Boolean {
    // TODO(combat): Resolve if type is slayer task.
    return false
}
