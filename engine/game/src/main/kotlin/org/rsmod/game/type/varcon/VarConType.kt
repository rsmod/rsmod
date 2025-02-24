package org.rsmod.game.type.varcon

import org.rsmod.game.type.CacheType
import org.rsmod.game.type.varobjbit.UnpackedVarObjBitType

public sealed class VarConType : CacheType()

public data class UnpackedVarConType(
    override var internalId: Int?,
    override var internalName: String?,
) : VarConType() {
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
