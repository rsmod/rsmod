package org.rsmod.game.loc

import org.rsmod.map.zone.ZoneGrid

@JvmInline
public value class LocZoneKey(public val packed: Byte) {
    public val x: Int
        get() = (packed.toInt() shr X_BIT_OFFSET) and X_BIT_MASK

    public val z: Int
        get() = (packed.toInt() shr Z_BIT_OFFSET) and Z_BIT_MASK

    public val layer: Int
        get() = (packed.toInt() shr LAYER_BIT_OFFSET) and LAYER_BIT_MASK

    public constructor(x: Int, z: Int, layer: Int) : this(pack(x, z, layer))

    public constructor(grid: ZoneGrid, layer: Int) : this(grid.x, grid.z, layer)

    public operator fun component1(): Int = x

    public operator fun component2(): Int = z

    public operator fun component3(): Int = layer

    override fun toString(): String = "LocZoneKey(x=$x, z=$z, layer=$layer)"

    public companion object {
        public const val Z_BIT_COUNT: Int = 3
        public const val X_BIT_COUNT: Int = 3
        public const val LAYER_BIT_COUNT: Int = 2

        public const val Z_BIT_OFFSET: Int = 0
        public const val X_BIT_OFFSET: Int = Z_BIT_OFFSET + Z_BIT_COUNT
        public const val LAYER_BIT_OFFSET: Int = X_BIT_OFFSET + X_BIT_COUNT

        public const val Z_BIT_MASK: Int = (1 shl Z_BIT_COUNT) - 1
        public const val X_BIT_MASK: Int = (1 shl X_BIT_COUNT) - 1
        public const val LAYER_BIT_MASK: Int = (1 shl LAYER_BIT_COUNT) - 1

        private fun pack(x: Int, z: Int, layer: Int): Byte {
            require(x in 0..X_BIT_MASK) { "`x` value must be within range [0..$X_BIT_MASK]." }
            require(z in 0..Z_BIT_MASK) { "`z` value must be within range [0..$Z_BIT_MASK]." }
            require(layer in 0..LAYER_BIT_MASK) {
                "`layer` value must be within range [0..$LAYER_BIT_MASK]."
            }
            val packed =
                ((x and X_BIT_MASK) shl X_BIT_OFFSET) or
                    ((z and Z_BIT_MASK) shl Z_BIT_OFFSET) or
                    ((layer and LAYER_BIT_MASK) shl LAYER_BIT_OFFSET)
            return packed.toByte()
        }
    }
}
