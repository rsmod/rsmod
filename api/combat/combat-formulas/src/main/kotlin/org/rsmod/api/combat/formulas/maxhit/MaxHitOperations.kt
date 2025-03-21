package org.rsmod.api.combat.formulas.maxhit

import java.util.EnumSet
import kotlin.math.max
import org.rsmod.api.combat.formulas.attributes.DamageReductionAttributes
import org.rsmod.api.combat.formulas.scale

internal object MaxHitOperations {
    fun applyDamageReductions(
        startDamage: Int,
        activeDefenceBonus: Int,
        reductionAttributes: EnumSet<DamageReductionAttributes>,
    ): Int {
        var modified = startDamage

        if (DamageReductionAttributes.ElysianProc in reductionAttributes) {
            modified = scale(modified, multiplier = 3, divisor = 4)
        }

        if (DamageReductionAttributes.DinhsBlock in reductionAttributes) {
            modified = scale(modified, multiplier = 4, divisor = 5)
        }

        if (DamageReductionAttributes.Justiciar in reductionAttributes) {
            val factor = activeDefenceBonus / 3000.0
            // Damage reduction effect will always reduce at least `1`.
            val reduction = max(1, (modified * factor).toInt())
            modified = max(0, modified - reduction)
        }

        return modified
    }
}
