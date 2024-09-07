package org.rsmod.game.ui

@JvmInline
public value class Component(public val packed: Int) {
    public val parent: Int
        get() = (packed shr PARENT_BIT_OFFSET) and PARENT_BIT_MASK

    public val child: Int
        get() = (packed shr CHILD_BIT_OFFSET) and CHILD_BIT_MASK

    public constructor(parent: Int, child: Int) : this(pack(parent, child))

    public override fun toString(): String {
        return "Component(parent=$parent, child=$child)"
    }

    public companion object {
        public val NULL: Component = Component(-1)

        public const val PARENT_BIT_COUNT: Int = 16
        public const val CHILD_BIT_COUNT: Int = 16

        public const val CHILD_BIT_OFFSET: Int = 0
        public const val PARENT_BIT_OFFSET: Int = CHILD_BIT_OFFSET + CHILD_BIT_COUNT

        public const val PARENT_BIT_MASK: Int = (1 shl PARENT_BIT_COUNT) - 1
        public const val CHILD_BIT_MASK: Int = (1 shl CHILD_BIT_COUNT) - 1

        private fun pack(parent: Int, child: Int): Int {
            require(parent in 0..PARENT_BIT_MASK) {
                "`parent` value must be within range [0..$PARENT_BIT_MASK]."
            }
            require(child in 0..CHILD_BIT_MASK) {
                "`child` value must be within range [0..$CHILD_BIT_MASK]."
            }
            return ((parent and PARENT_BIT_MASK) shl PARENT_BIT_OFFSET) or
                ((child and CHILD_BIT_MASK) shl CHILD_BIT_OFFSET)
        }
    }
}
