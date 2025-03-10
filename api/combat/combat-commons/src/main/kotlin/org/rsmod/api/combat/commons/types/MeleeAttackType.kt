package org.rsmod.api.combat.commons.types

public enum class MeleeAttackType {
    Stab,
    Slash,
    Crush;

    public companion object {
        public fun from(type: AttackType?): MeleeAttackType? =
            when (type) {
                AttackType.Stab -> Stab
                AttackType.Slash -> Slash
                AttackType.Crush -> Crush
                else -> null
            }
    }
}
