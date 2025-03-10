package org.rsmod.api.combat.commons.styles

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
