package org.rsmod.game.type.stat

import org.rsmod.game.type.CacheType
import org.rsmod.game.type.HashedCacheType

public sealed class StatType : CacheType() {
    internal abstract var internalMaxLevel: Int?

    internal abstract var internalDisplayName: String?

    public val maxLevel: Int
        get() = internalMaxLevel ?: error("`internalMaxLevel` must not be null.")

    public val displayName: String
        get() = internalDisplayName ?: error("`internalDisplayName` must not be null.")
}

public data class HashedStatType(
    override var startHash: Long?,
    override var internalName: String?,
    override var internalId: Int? = null,
    override var internalMaxLevel: Int? = null,
    override var internalDisplayName: String? = null,
) : HashedCacheType, StatType() {
    public val autoResolve: Boolean = startHash == null

    override fun toString(): String =
        "StatType(internalName='$internalName', internalId=$internalId, supposedHash=$supposedHash)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HashedStatType) return false
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

public data class UnpackedStatType(
    public val unreleased: Boolean,
    override var internalMaxLevel: Int?,
    override var internalDisplayName: String?,
    override var internalId: Int?,
    override var internalName: String?,
) : StatType() {
    private val identityHash by lazy { computeIdentityHash() }

    public fun toHashedType(): HashedStatType =
        HashedStatType(
            startHash = identityHash,
            internalName = internalName,
            internalId = internalId,
            internalMaxLevel = internalMaxLevel,
            internalDisplayName = displayName,
        )

    public fun computeIdentityHash(): Long {
        val result = (internalId?.hashCode()?.toLong() ?: 0)
        return result and 0x7FFFFFFFFFFFFFFF
    }

    override fun toString(): String =
        "UnpackedStatType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "internalHash=${computeIdentityHash()}, " +
            "displayName='$displayName', " +
            "maxLevel=$maxLevel, " +
            "unreleased=$unreleased" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UnpackedStatType) return false
        if (unreleased != other.unreleased) return false
        if (maxLevel != other.maxLevel) return false
        if (displayName != other.displayName) return false
        if (internalId != other.internalId) return false
        return true
    }

    override fun hashCode(): Int = computeIdentityHash().toInt()
}
