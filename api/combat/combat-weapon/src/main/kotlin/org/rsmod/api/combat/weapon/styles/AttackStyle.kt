package org.rsmod.api.combat.weapon.styles

import org.rsmod.api.utils.vars.VarEnumDelegate

public enum class AttackStyle(public val id: Int) {
    /* `id` 0 is reserved for "null" value. */
    ControlledMelee(1),
    AccurateMelee(2),
    AggressiveMelee(3),
    DefensiveMelee(4),
    AccurateRanged(5),
    RapidRanged(6),
    LongRangeRanged(7);

    public val isRanged: Boolean
        get() = this == AccurateRanged || this == RapidRanged || this == LongRangeRanged

    public companion object {
        public operator fun get(id: Int): AttackStyle? =
            when (id) {
                AccurateMelee.id -> AccurateMelee
                AggressiveMelee.id -> AggressiveMelee
                DefensiveMelee.id -> DefensiveMelee
                ControlledMelee.id -> ControlledMelee
                AccurateRanged.id -> AccurateRanged
                RapidRanged.id -> RapidRanged
                LongRangeRanged.id -> LongRangeRanged
                else -> null
            }
    }
}

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

public enum class RangedAttackStyle {
    Accurate,
    Rapid,
    LongRange;

    public companion object {
        public fun from(style: AttackStyle?): RangedAttackStyle? =
            when (style) {
                AttackStyle.AccurateRanged -> Accurate
                AttackStyle.RapidRanged -> Rapid
                AttackStyle.LongRangeRanged -> LongRange
                else -> null
            }
    }
}
