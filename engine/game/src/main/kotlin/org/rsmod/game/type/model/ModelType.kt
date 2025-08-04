package org.rsmod.game.type.model

import org.rsmod.game.type.CacheType
import org.rsmod.game.type.HashedCacheType

public sealed class ModelType : CacheType()

public data class HashedModelType(
    override var startHash: Long?,
    override var internalName: String?,
    override var internalId: Int? = null,
) : HashedCacheType, ModelType() {
    public val autoResolve: Boolean = startHash == null

    override fun toString(): String =
        "ModelType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "supposedHash=$supposedHash" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HashedModelType) return false
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

public class SimpleModelType(
    public val checksum: Int,
    override var internalId: Int?,
    override var internalName: String?,
) : ModelType() {
    private val identityHash by lazy { computeIdentityHash() }

    public fun toHashedType(): HashedModelType =
        HashedModelType(
            startHash = identityHash,
            internalName = internalName,
            internalId = internalId,
        )

    public fun computeIdentityHash(): Long {
        var result = checksum.toLong()
        result = 61 * result + (internalId ?: 0)
        return result and 0x7FFFFFFFFFFFFFFF
    }
}
