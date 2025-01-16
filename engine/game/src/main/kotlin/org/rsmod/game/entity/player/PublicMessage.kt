package org.rsmod.game.entity.player

public data class PublicMessage(
    val text: String,
    val colour: Int,
    val effect: Int,
    val clanType: Int?,
    val modIcon: Int,
    val autoTyper: Boolean,
    val pattern: ByteArray?,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PublicMessage

        if (colour != other.colour) return false
        if (effect != other.effect) return false
        if (clanType != other.clanType) return false
        if (modIcon != other.modIcon) return false
        if (autoTyper != other.autoTyper) return false
        if (text != other.text) return false
        if (!pattern.contentEquals(other.pattern)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = colour
        result = 31 * result + effect
        result = 31 * result + (clanType ?: 0)
        result = 31 * result + modIcon
        result = 31 * result + autoTyper.hashCode()
        result = 31 * result + text.hashCode()
        result = 31 * result + (pattern?.contentHashCode() ?: 0)
        return result
    }
}
