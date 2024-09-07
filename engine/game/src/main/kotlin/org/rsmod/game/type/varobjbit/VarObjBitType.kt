package org.rsmod.game.type.varobjbit

public sealed class VarObjBitType(
    internal var internalId: Int?,
    internal var internalName: String,
) {
    public val internalNameGet: String
        get() = internalName

    override fun toString(): String = "VarObjBitType(internalId=$internalId)"
}

public class UnpackedVarObjBitType(
    public val startBit: Int,
    public val endBit: Int,
    internalId: Int,
    internalName: String,
) : VarObjBitType(internalId, internalName) {
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
