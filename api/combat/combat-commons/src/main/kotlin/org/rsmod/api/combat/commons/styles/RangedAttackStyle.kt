package org.rsmod.api.combat.commons.styles

public enum class RangedAttackStyle {
    Accurate,
    Rapid,
    Longrange;

    public companion object {
        public fun from(style: AttackStyle?): RangedAttackStyle? =
            when (style) {
                AttackStyle.AccurateRanged -> Accurate
                AttackStyle.RapidRanged -> Rapid
                AttackStyle.LongrangeRanged -> Longrange
                else -> null
            }
    }
}
