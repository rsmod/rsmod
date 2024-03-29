package org.rsmod.game.ui

@JvmInline
public value class Component(public val packed: Int) {

    public val parent: Int get() = (packed shr PARENT_BIT_OFFSET) and PARENT_BIT_MASK

    public val child: Int get() = (packed shr CHILD_BIT_OFFSET) and CHILD_BIT_MASK

    public constructor(parent: Int, child: Int) : this(pack(parent, child))

    public override fun toString(): String {
        return "Component(parent=$parent, child=$child)"
    }

    @Suppress("MemberVisibilityCanBePrivate")
    public companion object {

        public const val PARENT_BIT_COUNT: Int = 16
        public const val PARENT_BIT_MASK: Int = (1 shl PARENT_BIT_COUNT) - 1

        public const val CHILD_BIT_COUNT: Int = 16
        public const val CHILD_BIT_MASK: Int = (1 shl CHILD_BIT_COUNT) - 1

        public const val CHILD_BIT_OFFSET: Int = 0
        public const val PARENT_BIT_OFFSET: Int = CHILD_BIT_COUNT

        private fun pack(parent: Int, child: Int): Int {
            if (parent !in 0..PARENT_BIT_MASK) {
                throw IllegalArgumentException("`parent` value must be within range [0..$PARENT_BIT_MASK].")
            } else if (child !in 0..CHILD_BIT_MASK) {
                throw IllegalArgumentException("`child` value must be within range [0..$CHILD_BIT_MASK].")
            }
            return ((parent and PARENT_BIT_MASK) shl PARENT_BIT_OFFSET) or
                ((child and CHILD_BIT_MASK) shl CHILD_BIT_OFFSET)
        }
    }
}
