package org.rsmod.game.type.varn

import org.rsmod.game.type.CacheType
import org.rsmod.game.type.HashedCacheType

public sealed class VarnType : CacheType()

public data class HashedVarnType(
    override var startHash: Long?,
    override var internalName: String?,
    override var internalId: Int? = null,
) : HashedCacheType, VarnType() {
    public val autoResolve: Boolean = startHash == null

    override fun toString(): String =
        "VarnType(internalName='$internalName', internalId=$internalId, supposedHash=$supposedHash)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HashedVarnType) return false
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

public data class UnpackedVarnType(
    public val bitProtect: Boolean,
    override var internalId: Int?,
    override var internalName: String?,
) : VarnType() {
    override fun toString(): String =
        "UnpackedVarpType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "bitProtect=$bitProtect, " +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UnpackedVarnType) return false
        if (bitProtect != other.bitProtect) return false
        if (internalId != other.internalId) return false
        return true
    }

    override fun hashCode(): Int {
        var result = bitProtect.hashCode()
        result = 31 * result + (internalId ?: 0)
        return result
    }
}
