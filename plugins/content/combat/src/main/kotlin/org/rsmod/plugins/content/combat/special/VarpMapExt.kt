package org.rsmod.plugins.content.combat.special

import org.rsmod.game.model.vars.VarpMap
import org.rsmod.game.model.vars.type.VarpType
import org.rsmod.plugins.api.model.vars.getVarp
import org.rsmod.plugins.api.model.vars.setVarp
import org.rsmod.plugins.content.combat.special.SpecialAttack.MAX_SPECIAL_ENERGY

internal lateinit var specialEnergyVarp: VarpType
internal lateinit var specialEnabledVarp: VarpType

var VarpMap.specialAttackEnergy: Int
    get() = getVarp(specialEnergyVarp).fromEnergyVarp()
    set(value) { setVarp(specialEnergyVarp, value.toEnergyVarp()) }

var VarpMap.specialAttackEnabled: Boolean
    get() = getVarp(specialEnabledVarp) != 0
    set(value) { setVarp(specialEnabledVarp, value) }

internal fun configureInternalVarps(specialEnergy: VarpType, specialState: VarpType) {
    specialEnergyVarp = specialEnergy
    specialEnabledVarp = specialState
}

private fun Int.fromEnergyVarp(): Int = (this / 10).coerceAtLeast(0)

private fun Int.toEnergyVarp(): Int = (this * 10).coerceAtMost(MAX_SPECIAL_ENERGY * 10)
