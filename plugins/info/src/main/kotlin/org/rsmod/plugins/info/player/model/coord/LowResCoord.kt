package org.rsmod.plugins.info.player.model.coord

@JvmInline
public value class LowResCoord(public val packed: Int) {

    public val x: Int get() = (packed shr 8) and 0xFF

    public val z: Int get() = packed and 0xFF

    public val level: Int get() = (packed shr 16) and 0x3

    public constructor(x: Int, z: Int, level: Int = 0) : this(
        (z and 0xFF) or ((x and 0xFF) shl 8) or ((level and 0x3) shl 16)
    )

    public fun toHighRes(): HighResCoord {
        return HighResCoord((x shl 13) and 0x3FFF, z shl 13, level)
    }

    public operator fun component1(): Int = x

    public operator fun component2(): Int = z

    public operator fun component3(): Int = level

    public operator fun minus(other: LowResCoord): LowResCoord {
        return LowResCoord(x - other.x, z - other.z, level - other.level)
    }

    public operator fun plus(other: LowResCoord): LowResCoord {
        return LowResCoord(x + other.x, z + other.z, level + other.level)
    }

    public override fun toString(): String {
        return "LowResCoord(x=$x, z=$z, level=$level)"
    }

    public companion object {

        public val ZERO: LowResCoord = LowResCoord(0)
    }
}
