package org.rsmod.plugins.info.model.bitcode

@JvmInline
public value class I26BitCode private constructor(private val packed: Int) {

    public val value: Int get() = packed and VALUE_BITMASK

    public val bitCount: Int get() = (packed shr VALUE_BITS) and VALUE_BITMASK

    public constructor(value: Int, bitCount: Int) : this(
        (value and VALUE_BITMASK) or ((bitCount and BIT_COUNT_BITMASK) shl VALUE_BITS)
    )

    public operator fun component1(): Int = value

    public operator fun component2(): Int = bitCount

    public override fun toString(): String {
        return "I26BitCode(value=$value, bitCount=$bitCount)"
    }

    public companion object {

        public val ZERO: I26BitCode = I26BitCode(0)

        public const val VALUE_BITS: Int = 26
        public const val VALUE_BITMASK: Int = (1 shl VALUE_BITS) - 1

        public const val BIT_COUNT_BITS: Int = 5
        public const val BIT_COUNT_BITMASK: Int = (1 shl BIT_COUNT_BITS) - 1
    }
}
