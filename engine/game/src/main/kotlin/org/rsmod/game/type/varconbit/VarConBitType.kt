package org.rsmod.game.type.varconbit

import org.rsmod.game.type.CacheType
import org.rsmod.game.type.varcon.VarConType

public sealed class VarConBitType : CacheType() {
    internal abstract var varcon: VarConType?
    internal abstract var lsb: Int?
    internal abstract var msb: Int?

    public val baseVar: VarConType
        get() = varcon ?: error("`varcon` must not be null.")

    public val bits: IntRange
        get() = bitRange()

    private fun bitRange(): IntRange {
        val lsb = lsb ?: error("`lsb` must not be null.")
        val msb = msb ?: error("`msb` must not be null.")
        return lsb..msb
    }
}

public data class UnpackedVarConBitType(
    public val varconId: Int,
    override var varcon: VarConType?,
    override var lsb: Int?,
    override var msb: Int?,
    override var internalId: Int?,
    override var internalName: String?,
) : VarConBitType() {
    public fun hashCodeLong(): Long {
        var result = varconId.hashCode().toLong()
        result = 61 * result + (lsb?.hashCode() ?: 0)
        result = 61 * result + (msb?.hashCode() ?: 0)
        result = 61 * result + (internalId?.hashCode()?.toLong() ?: 0)
        return result and 0x7FFFFFFFFFFFFFFF
    }

    override fun toString(): String =
        "UnpackedVarConBitType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "baseVar=$varcon, " +
            "lsb=$lsb, " +
            "msb=$msb" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UnpackedVarConBitType) return false
        if (varconId != other.varconId) return false
        if (lsb != other.lsb) return false
        if (msb != other.msb) return false
        if (internalId != other.internalId) return false
        return true
    }

    override fun hashCode(): Int = hashCodeLong().toInt()
}
