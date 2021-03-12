package org.rsmod.plugins.content.combat.special

import org.rsmod.game.model.mob.Player
import org.rsmod.plugins.content.combat.special.SpecialAttack.MAX_SPECIAL_ENERGY

fun Player.hasSpecialAttackEnergy(amount: Int): Boolean {
    return varpMap.specialAttackEnergy >= amount
}

fun Player.isSpecialAttackEnabled(): Boolean {
    return varpMap.specialAttackEnabled
}

internal fun Player.setDefaultSpecialAttackVarps() {
    varpMap.specialAttackEnergy = MAX_SPECIAL_ENERGY
}
