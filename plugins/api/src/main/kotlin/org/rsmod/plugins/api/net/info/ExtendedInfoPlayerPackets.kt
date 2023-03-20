package org.rsmod.plugins.api.net.info

import org.rsmod.game.model.mob.info.ExtendedInfo

public sealed class ExtendedPlayerInfo : ExtendedInfo {

    public data class ExtendedFlag(public val bitmasks: Int) : ExtendedPlayerInfo()

    public data class Appearance(
        public val gender: Int,
        public val overheadSkull: Int?,
        public val overheadPrayer: Int?,
        public val transmogId: Int?,
        public val looks: ByteArray,
        public val colors: IntArray,
        public val bas: IntArray,
        public val displayName: String,
        public val combatLevel: Int,
        public val skillLevel: Int,
        public val invisible: Boolean,
        public val unknownShortValue: Int,
        public val prefixes: Array<String>,
        public val unknownByteValue: Int
    ) : ExtendedPlayerInfo() {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Appearance

            if (gender != other.gender) return false
            if (overheadSkull != other.overheadSkull) return false
            if (overheadPrayer != other.overheadPrayer) return false
            if (transmogId != other.transmogId) return false
            if (!looks.contentEquals(other.looks)) return false
            if (!colors.contentEquals(other.colors)) return false
            if (!bas.contentEquals(other.bas)) return false
            if (displayName != other.displayName) return false
            if (combatLevel != other.combatLevel) return false
            if (skillLevel != other.skillLevel) return false
            if (invisible != other.invisible) return false
            if (unknownShortValue != other.unknownShortValue) return false
            if (!prefixes.contentEquals(other.prefixes)) return false
            if (unknownByteValue != other.unknownByteValue) return false

            return true
        }

        override fun hashCode(): Int {
            var result = gender
            result = 31 * result + (overheadSkull ?: 0)
            result = 31 * result + (overheadPrayer ?: 0)
            result = 31 * result + (transmogId ?: 0)
            result = 31 * result + looks.contentHashCode()
            result = 31 * result + colors.contentHashCode()
            result = 31 * result + bas.contentHashCode()
            result = 31 * result + displayName.hashCode()
            result = 31 * result + combatLevel
            result = 31 * result + skillLevel
            result = 31 * result + invisible.hashCode()
            result = 31 * result + unknownShortValue
            result = 31 * result + prefixes.contentHashCode()
            result = 31 * result + unknownByteValue
            return result
        }
    }
}
