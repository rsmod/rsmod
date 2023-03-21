package org.rsmod.plugins.api.net.info

import org.rsmod.game.model.mob.info.ExtendedInfo

public sealed class ExtendedPlayerInfo : ExtendedInfo {

    public data class Anim(
        public val sequence: Int,
        public val delay: Int
    ) : ExtendedPlayerInfo()

    public data class Appearance(
        public val gender: Int,
        public val overheadSkull: Int?,
        public val overheadPrayer: Int?,
        public val transmog: Int?,
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
            if (transmog != other.transmog) return false
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
            result = 31 * result + (transmog ?: 0)
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

    public data class Chat(
        public val text: String,
        public val colors: Int,
        public val effects: Int,
        public val crown: Int,
        public val autoChat: Boolean
    ) : ExtendedPlayerInfo()

    public data class ExactMove(
        public val deltaX1: Int,
        public val deltaZ1: Int,
        public val deltaX2: Int,
        public val deltaZ2: Int,
        public val arriveDelay1: Int,
        public val arriveDelay2: Int,
        public val direction: Int
    ) : ExtendedPlayerInfo()

    public data class ExtendedFlag(public val bitmasks: Int) : ExtendedPlayerInfo()

    public data class FaceEntity(public val index: Int) : ExtendedPlayerInfo()

    public data class FaceSquare(public val orientation: Int) : ExtendedPlayerInfo()

    // TODO: Hit model
    public data class Hit(public val placeholder: Int) : ExtendedPlayerInfo()

    public data class MoveSpeedTemp(public val type: Int) : ExtendedPlayerInfo()

    public data class MoveSpeedPerm(public val type: Int) : ExtendedPlayerInfo()

    public data class Prefix(
        public val string1: String?,
        public val string2: String?,
        public val string3: String?
    ) : ExtendedPlayerInfo()

    public data class Recolor(
        public val startDelay: Int,
        public val endDelay: Int,
        public val hue: Int,
        public val sat: Int,
        public val lum: Int,
        public val amount: Int
    ) : ExtendedPlayerInfo()

    public data class Spotanim(
        public val id: Int,
        public val height: Int,
        public val delay: Int
    ) : ExtendedPlayerInfo()

    /**
     * @param expose if true, all high-res players will have [text] added in
     * their chat box.
     */
    public data class Say(
        public val text: String,
        public val expose: Boolean
    ) : ExtendedPlayerInfo()
}
