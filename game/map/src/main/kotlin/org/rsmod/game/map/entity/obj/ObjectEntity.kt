package org.rsmod.game.map.entity.obj

@JvmInline
public value class ObjectEntity(public val packed: Int) {

    public val id: Int get() = (packed shr ID_BIT_OFFSET) and ID_BIT_MASK

    public val shape: Int get() = (packed shr SHAPE_BIT_OFFSET) and SHAPE_BIT_MASK

    public val rot: Int get() = (packed shr ROT_BIT_OFFSET) and ROT_BIT_MASK

    public constructor(id: Int, shape: Int, rot: Int) : this(pack(id, shape, rot))

    public operator fun component1(): Int = id

    public operator fun component2(): Int = shape

    public operator fun component3(): Int = rot

    override fun toString(): String {
        return "ObjectEntity(id=$id, shape=$shape, rot=$rot)"
    }

    @Suppress("MemberVisibilityCanBePrivate")
    public companion object {

        public const val ID_BIT_COUNT: Int = 17
        public const val ID_BIT_MASK: Int = (1 shl ID_BIT_COUNT) - 1

        public const val SHAPE_BIT_COUNT: Int = 5
        public const val SHAPE_BIT_MASK: Int = (1 shl SHAPE_BIT_COUNT) - 1

        public const val ROT_BIT_COUNT: Int = 2
        public const val ROT_BIT_MASK: Int = (1 shl ROT_BIT_COUNT) - 1

        public const val ID_BIT_OFFSET: Int = 0
        public const val SHAPE_BIT_OFFSET: Int = ID_BIT_COUNT
        public const val ROT_BIT_OFFSET: Int = ID_BIT_COUNT + SHAPE_BIT_COUNT

        @Suppress("DuplicatedCode")
        private fun pack(id: Int, shape: Int, rot: Int): Int {
            if (id !in 0..ID_BIT_MASK) {
                throw IllegalArgumentException("`id` value must be within range [0..$ID_BIT_MASK].")
            } else if (shape !in 0..SHAPE_BIT_MASK) {
                throw IllegalArgumentException("`shape` value must be within range [0..$SHAPE_BIT_MASK].")
            } else if (rot !in 0..ROT_BIT_MASK) {
                throw IllegalArgumentException("`rot` value must be within range [0..$ROT_BIT_MASK].")
            }
            return ((id and ID_BIT_MASK) shl ID_BIT_OFFSET) or
                ((shape and SHAPE_BIT_MASK) shl SHAPE_BIT_OFFSET) or
                ((rot and ROT_BIT_MASK) shl ROT_BIT_OFFSET)
        }
    }
}
