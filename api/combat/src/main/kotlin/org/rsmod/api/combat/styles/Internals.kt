package org.rsmod.api.combat.styles

internal typealias PackedStyles = StyleInternals.PackedStyles

public object StyleInternals {
    @JvmInline
    public value class PackedStyles(public val packed: Int) {
        public val style1: AttackStyle?
            get() = AttackStyle[(packed shr STYLE_1_BIT_OFFSET) and STYLE_BIT_MASK]

        public val style2: AttackStyle?
            get() = AttackStyle[(packed shr STYLE_2_BIT_OFFSET) and STYLE_BIT_MASK]

        public val style3: AttackStyle?
            get() = AttackStyle[(packed shr STYLE_3_BIT_OFFSET) and STYLE_BIT_MASK]

        public val style4: AttackStyle?
            get() = AttackStyle[(packed shr STYLE_4_BIT_OFFSET) and STYLE_BIT_MASK]

        public constructor(
            style1: Int,
            style2: Int,
            style3: Int,
            style4: Int,
        ) : this(pack(style1, style2, style3, style4))

        public operator fun component1(): AttackStyle? = style1

        public operator fun component2(): AttackStyle? = style2

        public operator fun component3(): AttackStyle? = style3

        public operator fun component4(): AttackStyle? = style4

        private companion object {
            const val STYLE_BIT_COUNT: Int = 3
            const val STYLE_BIT_MASK = (1 shl STYLE_BIT_COUNT) - 1

            const val STYLE_1_BIT_OFFSET = 0
            const val STYLE_2_BIT_OFFSET = STYLE_1_BIT_OFFSET + STYLE_BIT_COUNT
            const val STYLE_3_BIT_OFFSET = STYLE_2_BIT_OFFSET + STYLE_BIT_COUNT
            const val STYLE_4_BIT_OFFSET = STYLE_3_BIT_OFFSET + STYLE_BIT_COUNT

            private fun pack(style1: Int, style2: Int, style3: Int, style4: Int): Int {
                require(style1 in 0..STYLE_BIT_MASK) {
                    "`style1` value must be within range [0..$STYLE_BIT_MASK]"
                }

                require(style2 in 0..STYLE_BIT_MASK) {
                    "`style2` value must be within range [0..$STYLE_BIT_MASK]"
                }

                require(style3 in 0..STYLE_BIT_MASK) {
                    "`style3` value must be within range [0..$STYLE_BIT_MASK]"
                }

                require(style4 in 0..STYLE_BIT_MASK) {
                    "`style4` value must be within range [0..$STYLE_BIT_MASK]"
                }

                return ((style1 and STYLE_BIT_MASK) shl STYLE_1_BIT_OFFSET) or
                    ((style2 and STYLE_BIT_MASK) shl STYLE_2_BIT_OFFSET) or
                    ((style3 and STYLE_BIT_MASK) shl STYLE_3_BIT_OFFSET) or
                    ((style4 and STYLE_BIT_MASK) shl STYLE_4_BIT_OFFSET)
            }
        }
    }
}
