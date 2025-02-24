package org.rsmod.game.type.struct

import org.rsmod.game.type.CacheType
import org.rsmod.game.type.HashedCacheType
import org.rsmod.game.type.util.ParamMap

public sealed class StructType : CacheType()

public data class HashedStructType(
    override var startHash: Long?,
    override var internalName: String?,
    override var internalId: Int? = null,
) : HashedCacheType, StructType() {
    public val autoResolve: Boolean = startHash == null

    override fun toString(): String =
        "StructType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "supposedHash=$supposedHash" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HashedStructType) return false
        if (startHash != other.startHash) return false
        if (internalId != other.internalId) return false
        return true
    }

    override fun hashCode(): Int {
        var result = internalId?.hashCode() ?: 0
        result = 31 * result + (startHash?.hashCode() ?: 0)
        return result
    }
}

public data class UnpackedStructType(
    public val paramMap: ParamMap?,
    override var internalId: Int?,
    override var internalName: String?,
) : StructType() {
    public fun computeIdentityHash(): Long {
        val result = internalId.hashCode().toLong()
        return result and 0x7FFFFFFFFFFFFFFF
    }

    override fun toString(): String =
        "UnpackedStructType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "params=$paramMap" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UnpackedStructType) return false
        if (paramMap != other.paramMap) return false
        if (internalId != other.internalId) return false
        return true
    }

    override fun hashCode(): Int = computeIdentityHash().toInt()
}
