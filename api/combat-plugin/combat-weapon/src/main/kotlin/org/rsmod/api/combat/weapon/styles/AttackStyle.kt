package org.rsmod.api.combat.weapon.styles

public enum class AttackStyle(public val id: Int) {
    /* `id` 0 is reserved for "null" value. */
    AccurateMelee(1),
    AggressiveMelee(2),
    DefensiveMelee(3),
    ControlledMelee(4),
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

public enum class MeleeAttackStyle {
    Accurate,
    Aggressive,
    Defensive,
    Controlled;

    public companion object {
        public fun from(style: AttackStyle?): MeleeAttackStyle? =
            when (style) {
                AttackStyle.AccurateMelee -> Accurate
                AttackStyle.AggressiveMelee -> Aggressive
                AttackStyle.DefensiveMelee -> Defensive
                AttackStyle.ControlledMelee -> Controlled
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
