package org.rsmod.plugins.api.protocol.update

import org.rsmod.game.update.mask.UpdateMask

inline class BitMask(val packed: Int) : UpdateMask

data class DirectionMask(val angle: Int) : UpdateMask {
    companion object
}

data class AppearanceMask(
    val gender: Int,
    val skull: Int,
    val overheadPrayer: Int,
    val npc: Int,
    val looks: ByteArray,
    val colors: IntArray,
    val bas: IntArray,
    val username: String,
    val combatLevel: Int,
    val invisible: Boolean
) : UpdateMask {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AppearanceMask

        if (gender != other.gender) return false
        if (skull != other.skull) return false
        if (overheadPrayer != other.overheadPrayer) return false
        if (npc != other.npc) return false
        if (!looks.contentEquals(other.looks)) return false
        if (!colors.contentEquals(other.colors)) return false
        if (!bas.contentEquals(other.bas)) return false
        if (username != other.username) return false
        if (combatLevel != other.combatLevel) return false
        if (invisible != other.invisible) return false

        return true
    }

    override fun hashCode(): Int {
        var result = gender
        result = 31 * result + skull
        result = 31 * result + overheadPrayer
        result = 31 * result + npc
        result = 31 * result + looks.contentHashCode()
        result = 31 * result + colors.contentHashCode()
        result = 31 * result + bas.contentHashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + combatLevel
        result = 31 * result + invisible.hashCode()
        return result
    }

    companion object
}
