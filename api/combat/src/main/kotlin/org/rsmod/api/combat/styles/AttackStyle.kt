package org.rsmod.api.combat.styles

public enum class AttackStyle(public val id: Int) {
    /* `id` 0 is reserved for "null" value. */
    AccurateMelee(1),
    AggressiveMelee(2),
    DefensiveMelee(3),
    ControlledMelee(4),
    AccurateRanged(5),
    RapidRanged(6),
    LongRangeRanged(7);

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
