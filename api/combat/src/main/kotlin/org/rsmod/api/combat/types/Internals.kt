package org.rsmod.api.combat.types

internal typealias PackedTypes = TypeInternals.PackedTypes

public object TypeInternals {
    @JvmInline
    public value class PackedTypes(public val packed: Int) {
        public val type1: AttackType?
            get() = AttackType[(packed shr TYPE_1_BIT_OFFSET) and TYPE_BIT_MASK]

        public val type2: AttackType?
            get() = AttackType[(packed shr TYPE_2_BIT_OFFSET) and TYPE_BIT_MASK]

        public val type3: AttackType?
            get() = AttackType[(packed shr TYPE_3_BIT_OFFSET) and TYPE_BIT_MASK]

        public val type4: AttackType?
            get() = AttackType[(packed shr TYPE_4_BIT_OFFSET) and TYPE_BIT_MASK]

        public constructor(
            type1: Int,
            type2: Int,
            type3: Int,
            type4: Int,
        ) : this(pack(type1, type2, type3, type4))

        public operator fun component1(): AttackType? = type1

        public operator fun component2(): AttackType? = type2

        public operator fun component3(): AttackType? = type3

        public operator fun component4(): AttackType? = type4

        private companion object {
            const val TYPE_BIT_COUNT: Int = 3
            const val TYPE_BIT_MASK = (1 shl TYPE_BIT_COUNT) - 1

            const val TYPE_1_BIT_OFFSET = 0
            const val TYPE_2_BIT_OFFSET = TYPE_1_BIT_OFFSET + TYPE_BIT_COUNT
            const val TYPE_3_BIT_OFFSET = TYPE_2_BIT_OFFSET + TYPE_BIT_COUNT
            const val TYPE_4_BIT_OFFSET = TYPE_3_BIT_OFFSET + TYPE_BIT_COUNT

            private fun pack(type1: Int, type2: Int, type3: Int, type4: Int): Int {
                require(type1 in 0..TYPE_BIT_MASK) {
                    "`type1` value must be within range [0..$TYPE_BIT_MASK]"
                }

                require(type2 in 0..TYPE_BIT_MASK) {
                    "`type2` value must be within range [0..$TYPE_BIT_MASK]"
                }

                require(type3 in 0..TYPE_BIT_MASK) {
                    "`type3` value must be within range [0..$TYPE_BIT_MASK]"
                }

                require(type4 in 0..TYPE_BIT_MASK) {
                    "`type4` value must be within range [0..$TYPE_BIT_MASK]"
                }

                return ((type1 and TYPE_BIT_MASK) shl TYPE_1_BIT_OFFSET) or
                    ((type2 and TYPE_BIT_MASK) shl TYPE_2_BIT_OFFSET) or
                    ((type3 and TYPE_BIT_MASK) shl TYPE_3_BIT_OFFSET) or
                    ((type4 and TYPE_BIT_MASK) shl TYPE_4_BIT_OFFSET)
            }
        }
    }
}
