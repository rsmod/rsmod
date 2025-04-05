package org.rsmod.game.type.varobjbit

import org.rsmod.game.type.CacheType

public sealed class VarObjBitType : CacheType()

public class UnpackedVarObjBitType(
    public val startBit: Int,
    public val endBit: Int,
    override var internalId: Int?,
    override var internalName: String?,
) : VarObjBitType() {
    public val bits: IntRange
        get() = startBit..endBit

    public fun hashCodeLong(): Long {
        var result = 61 * (internalId?.hashCode()?.toLong() ?: 0)
        result = 61 * result + startBit
        result = 61 * result + endBit
        return result and 0x7FFFFFFFFFFFFFFF
    }

    override fun toString(): String =
        "UnpackedVarObjBitType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "startBit=$startBit, " +
            "endBit=$endBit" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UnpackedVarObjBitType) return false
        if (startBit != other.startBit) return false
        if (endBit != other.endBit) return false
        if (internalId != other.internalId) return false
        return true
    }

    override fun hashCode(): Int = hashCodeLong().toInt()
}
