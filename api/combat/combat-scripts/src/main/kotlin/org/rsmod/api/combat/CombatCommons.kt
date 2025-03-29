package org.rsmod.api.combat

import org.rsmod.api.config.constants

internal const val ACTIVE_COMBAT_DELAY = constants.combat_activecombat_delay

internal const val MAX_ATTACK_RANGE = 10
internal const val MAGIC_ATTACK_RANGE = MAX_ATTACK_RANGE
internal const val MAGIC_STAFF_ATTACK_RATE = 4
internal const val MAGIC_SPELL_ATTACK_RATE = 5

// TODO(combat): Multi combat areas
internal fun inMultiCombatArea(): Boolean {
    return false
}
