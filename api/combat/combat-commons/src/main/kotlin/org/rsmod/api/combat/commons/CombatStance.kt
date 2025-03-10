package org.rsmod.api.combat.commons

import org.rsmod.api.combat.commons.styles.AttackStyle
import org.rsmod.api.combat.commons.types.AttackType
import org.rsmod.api.utils.vars.VarEnumDelegate

/**
 * Represents the selectable stances available in the combat tab.
 *
 * ### Clarification
 * This is **not** the same as [AttackStyle]. Attack styles include `Accurate`, `Aggressive`,
 * `Defensive`, and `Controlled`.
 *
 * This distinction becomes necessary when a weapon type has repeated attack styles in the combat
 * tab selection.
 *
 * For example, in the godsword's combat tab, the second and third `attackStyle` selections both
 * represent `Aggressive`, even though they are separate stance selections with different attack
 * animations and sound effects (`Slash` and `Crush` types, respectively). To avoid confusion, we
 * decouple the selection variable from [AttackStyle].
 *
 * Likewise, this **cannot** be represented by [AttackType]. Using the `Unarmed` weapon type as an
 * example, all its selections are `Crush` types. However, these selections also differ in
 * animations and sound effects, making it necessary to decouple the selection variable from
 * [AttackType].
 */
public enum class CombatStance(override val varValue: Int) : VarEnumDelegate {
    Stance1(varValue = 0),
    Stance2(varValue = 1),
    Stance3(varValue = 2),
    Stance4(varValue = 3);

    public companion object {
        public operator fun get(varValue: Int): CombatStance? =
            when (varValue) {
                Stance1.varValue -> Stance1
                Stance2.varValue -> Stance2
                Stance3.varValue -> Stance3
                Stance4.varValue -> Stance4
                else -> null
            }
    }
}
