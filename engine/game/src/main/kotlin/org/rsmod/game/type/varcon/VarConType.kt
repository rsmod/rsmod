package org.rsmod.game.type.varcon

import org.rsmod.game.type.varobjbit.UnpackedVarObjBitType

public sealed class VarConType(internal var internalId: Int?, internal var internalName: String) {
    public val id: Int
        get() = internalId ?: error("`internalId` must not be null.")

    public val internalNameGet: String
        get() = internalName
}

public class UnpackedVarConType(internalId: Int, internalName: String) :
    VarConType(internalId, internalName) {
    public fun hashCodeLong(): Long {
        val result = 61 * (internalId?.hashCode()?.toLong() ?: 0)
        return result and 0x7FFFFFFFFFFFFFFF
    }

    override fun toString(): String =
        "UnpackedVarConType(internalName='$internalName', internalId=$internalId)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UnpackedVarObjBitType) return false

        if (internalId != other.internalId) return false

        return true
    }

    override fun hashCode(): Int = hashCodeLong().toInt()
}
