package org.rsmod.game.type.area

import org.rsmod.game.type.CacheType
import org.rsmod.game.type.HashedCacheType

public sealed class AreaType : CacheType()

public data class HashedAreaType(
    override var startHash: Long?,
    override var internalName: String?,
    override var internalId: Int? = null,
) : HashedCacheType, AreaType() {
    public val autoResolve: Boolean = startHash == null

    override fun toString(): String =
        "AreaType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "supposedHash=$supposedHash" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HashedAreaType) return false
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

public data class UnpackedAreaType(
    val colour: Int,
    override var internalId: Int?,
    override var internalName: String?,
) : AreaType() {
    private val identityHash by lazy { computeIdentityHash() }

    public fun toHashedType(): HashedAreaType =
        HashedAreaType(
            startHash = identityHash,
            internalName = internalName,
            internalId = internalId,
        )

    public fun computeIdentityHash(): Long = id.toLong()

    override fun toString(): String =
        "UnpackedAreaType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "colour=$colour" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UnpackedAreaType) return false
        if (internalId != other.internalId) return false
        if (colour != other.colour) return false
        return true
    }

    override fun hashCode(): Int = id
}
