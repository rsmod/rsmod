package org.rsmod.api.combat.commons.styles

public enum class MagicAttackStyle {
    Accurate,
    Longrange;

    public companion object {
        public fun from(style: AttackStyle?): MagicAttackStyle? =
            when (style) {
                AttackStyle.AccurateMagic -> Accurate
                AttackStyle.LongrangeMagic -> Longrange
                else -> null
            }
    }
}
