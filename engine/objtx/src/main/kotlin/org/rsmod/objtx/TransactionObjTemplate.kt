package org.rsmod.objtx

@JvmInline
public value class TransactionObjTemplate(public val packed: Long) {
    public val link: Int
        get() = ((packed shr LINK_BIT_OFFSET) and LINK_BIT_MASK).toInt()

    public val template: Int
        get() = ((packed shr MODEL_BIT_OFFSET) and MODEL_BIT_MASK).toInt()

    public constructor(link: Int, template: Int) : this(pack(link, template))

    public operator fun component1(): Int = link

    public operator fun component2(): Int = template

    override fun toString(): String = "TransactionObjTemplate(link=$link, template=$template)"

    public companion object {
        public val NULL: TransactionObjTemplate = TransactionObjTemplate(0)

        private const val LINK_BIT_COUNT = 32
        private const val MODEL_BIT_COUNT = 32

        private const val LINK_BIT_MASK: Long = (1L shl LINK_BIT_COUNT) - 1
        private const val MODEL_BIT_MASK: Long = (1L shl MODEL_BIT_COUNT) - 1

        private const val LINK_BIT_OFFSET = 0
        private const val MODEL_BIT_OFFSET = LINK_BIT_COUNT

        private fun pack(link: Int, template: Int): Long {
            return ((link.toLong() and LINK_BIT_MASK) shl LINK_BIT_OFFSET) or
                ((template.toLong() and MODEL_BIT_MASK) shl MODEL_BIT_OFFSET)
        }
    }
}
