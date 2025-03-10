package org.rsmod.api.combat.commons.styles

import org.rsmod.api.utils.vars.VarEnumDelegate

public enum class MeleeAttackStyle(override val varValue: Int) : VarEnumDelegate {
    Controlled(0),
    Accurate(1),
    Aggressive(2),
    Defensive(3);

    public companion object {
        public fun from(style: AttackStyle?): MeleeAttackStyle? =
            when (style) {
                AttackStyle.ControlledMelee -> Controlled
                AttackStyle.AccurateMelee -> Accurate
                AttackStyle.AggressiveMelee -> Aggressive
                AttackStyle.DefensiveMelee -> Defensive
                else -> null
            }
    }
}
