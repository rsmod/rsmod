package org.rsmod.game.type.varconbit

import org.rsmod.game.type.varcon.VarConType

public sealed class VarConBitType(
    internal var internalId: Int?,
    internal var internalName: String,
    internal var varcon: VarConType? = null,
    internal var lsb: Int? = null,
    internal var msb: Int? = null,
) {
    public val id: Int
        get() = internalId ?: error("`internalId` must not be null.")

    public val internalNameGet: String
        get() = internalName

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

public class UnpackedVarConBitType(
    public val varconId: Int,
    lsb: Int,
    msb: Int,
    varcon: VarConType?,
    internalId: Int,
    internalName: String,
) : VarConBitType(internalId, internalName, varcon, lsb, msb) {
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
