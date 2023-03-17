package org.rsmod.plugins.info.player.model.coord

@JvmInline
public value class HighResCoord(public val packed: Int) {

    public val x: Int get() = (packed shr 14) and 0x3FFF

    public val z: Int get() = packed and 0x3FFF

    public val level: Int get() = (packed shr 28) and 0x3

    public constructor(x: Int, z: Int, level: Int = 0) : this(
        (z and 0x3FFF) or ((x and 0x3FFF) shl 14) or ((level and 0x3) shl 28)
    )

    public fun toLowRes(): LowResCoord {
        return LowResCoord((x shr 13) and 0xFF, z shr 13, level)
    }

    public operator fun component1(): Int = x

    public operator fun component2(): Int = z

    public operator fun component3(): Int = level

    public operator fun minus(other: HighResCoord): HighResCoord {
        return HighResCoord(x - other.x, z - other.z, level - other.level)
    }

    public operator fun plus(other: HighResCoord): HighResCoord {
        return HighResCoord(x + other.x, z + other.z, level + other.level)
    }

    public override fun toString(): String {
        return "HighResCoord(x=$x, z=$z, level=$level)"
    }

    public companion object {

        public val ZERO: HighResCoord = HighResCoord(0)
    }
}
