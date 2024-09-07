package org.rsmod.game.obj

@JvmInline
public value class ObjEntity(public val packed: Long) {
    public val id: Int
        get() = ((packed shr ID_BIT_OFFSET) and ID_BIT_MASK).toInt()

    public val count: Int
        get() = ((packed shr COUNT_BIT_OFFSET) and COUNT_BIT_MASK).toInt()

    public val scope: Int
        get() = ((packed shr SCOPE_BIT_OFFSET) and SCOPE_BIT_MASK).toInt()

    public constructor(id: Int, count: Int, scope: Int) : this(pack(id, count, scope))

    public fun copy(
        id: Int = this.id,
        count: Int = this.count,
        scope: Int = this.scope,
    ): ObjEntity = ObjEntity(id, count, scope)

    public operator fun component1(): Int = id

    public operator fun component2(): Int = count

    public operator fun component3(): Int = scope

    override fun toString(): String = "ObjectEntity(id=$id, count=$count, scope=$scope)"

    public companion object {
        public const val ID_BIT_COUNT: Int = 16
        public const val COUNT_BIT_COUNT: Int = 32
        public const val SCOPE_BIT_COUNT: Int = 2

        public const val ID_BIT_OFFSET: Int = 0
        public const val COUNT_BIT_OFFSET: Int = ID_BIT_OFFSET + ID_BIT_COUNT
        public const val SCOPE_BIT_OFFSET: Int = COUNT_BIT_OFFSET + COUNT_BIT_COUNT

        public const val ID_BIT_MASK: Long = (1L shl ID_BIT_COUNT) - 1
        public const val COUNT_BIT_MASK: Long = (1L shl COUNT_BIT_COUNT) - 1
        public const val SCOPE_BIT_MASK: Long = (1L shl SCOPE_BIT_COUNT) - 1

        private fun pack(id: Int, count: Int, scope: Int): Long {
            require(id in 0..ID_BIT_MASK) { "`id` value must be within range [0..$ID_BIT_MASK]." }
            require(count > 0) { "`count` value must be positive." }
            require(scope in 0..SCOPE_BIT_MASK) {
                "`scope` value must be within range [0..$SCOPE_BIT_MASK]."
            }
            return ((id.toLong() and ID_BIT_MASK) shl ID_BIT_OFFSET) or
                ((count.toLong() and COUNT_BIT_MASK) shl COUNT_BIT_OFFSET) or
                ((scope.toLong() and SCOPE_BIT_MASK) shl SCOPE_BIT_OFFSET)
        }
    }
}
