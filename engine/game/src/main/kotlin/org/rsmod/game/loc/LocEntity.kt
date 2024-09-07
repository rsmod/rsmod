package org.rsmod.game.loc

@JvmInline
public value class LocEntity(public val packed: Int) {
    public val id: Int
        get() = (packed shr ID_BIT_OFFSET) and ID_BIT_MASK

    public val shape: Int
        get() = (packed shr SHAPE_BIT_OFFSET) and SHAPE_BIT_MASK

    public val angle: Int
        get() = (packed shr ANGLE_BIT_OFFSET) and ANGLE_BIT_MASK

    public constructor(id: Int, shape: Int, angle: Int) : this(pack(id, shape, angle))

    public fun copy(
        id: Int = this.id,
        shape: Int = this.shape,
        angle: Int = this.angle,
    ): LocEntity = LocEntity(id, shape, angle)

    public operator fun component1(): Int = id

    public operator fun component2(): Int = shape

    public operator fun component3(): Int = angle

    override fun toString(): String = "LocEntity(id=$id, shape=$shape, angle=$angle)"

    public companion object {
        public val NULL: LocEntity = LocEntity(-1)

        public const val ID_BIT_COUNT: Int = 17
        public const val SHAPE_BIT_COUNT: Int = 5
        public const val ANGLE_BIT_COUNT: Int = 2

        public const val ID_BIT_OFFSET: Int = 0
        public const val SHAPE_BIT_OFFSET: Int = ID_BIT_OFFSET + ID_BIT_COUNT
        public const val ANGLE_BIT_OFFSET: Int = SHAPE_BIT_OFFSET + SHAPE_BIT_COUNT

        public const val ID_BIT_MASK: Int = (1 shl ID_BIT_COUNT) - 1
        public const val SHAPE_BIT_MASK: Int = (1 shl SHAPE_BIT_COUNT) - 1
        public const val ANGLE_BIT_MASK: Int = (1 shl ANGLE_BIT_COUNT) - 1

        private fun pack(id: Int, shape: Int, angle: Int): Int {
            require(id in 0..ID_BIT_MASK) { "`id` value must be within range [0..$ID_BIT_MASK]." }
            require(shape in 0..SHAPE_BIT_MASK) {
                "`shape` value must be within range [0..$SHAPE_BIT_MASK]."
            }
            require(angle in 0..ANGLE_BIT_MASK) {
                "`angle` value must be within range [0..$ANGLE_BIT_MASK]."
            }
            return ((id and ID_BIT_MASK) shl ID_BIT_OFFSET) or
                ((shape and SHAPE_BIT_MASK) shl SHAPE_BIT_OFFSET) or
                ((angle and ANGLE_BIT_MASK) shl ANGLE_BIT_OFFSET)
        }
    }
}
