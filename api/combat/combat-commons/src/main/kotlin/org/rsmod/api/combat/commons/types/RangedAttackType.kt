package org.rsmod.api.combat.commons.types

public enum class RangedAttackType {
    Light,
    Standard,
    Heavy;

    public companion object {
        public fun from(type: AttackType?): RangedAttackType? =
            when (type) {
                AttackType.Light -> Light
                AttackType.Standard -> Standard
                AttackType.Heavy -> Heavy
                else -> null
            }
    }
}
