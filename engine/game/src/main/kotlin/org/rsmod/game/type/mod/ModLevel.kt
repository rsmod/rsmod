package org.rsmod.game.type.mod

import org.rsmod.game.type.CacheType
import org.rsmod.game.type.HashedCacheType

public sealed class ModLevelType : CacheType()

public data class HashedModLevelType(
    override var startHash: Long?,
    override var internalName: String?,
    override var internalId: Int? = null,
) : HashedCacheType, ModLevelType() {
    public val autoResolve: Boolean = startHash == null

    override fun toString(): String =
        "ModLevelType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "supposedHash=$supposedHash" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HashedModLevelType) return false
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

public data class UnpackedModLevelType(
    public val clientCode: Int,
    public val accessFlags: Long,
    override var internalId: Int?,
    override var internalName: String?,
) : ModLevelType() {
    private val identityHash by lazy { computeIdentityHash() }

    public fun hasAccessTo(level: ModLevelType): Boolean {
        return id == level.id || (accessFlags and (1L shl level.id)) != 0L
    }

    public fun toHashedType(): HashedModLevelType =
        HashedModLevelType(
            startHash = identityHash,
            internalName = internalName,
            internalId = internalId,
        )

    public fun computeIdentityHash(): Long {
        var result = accessFlags
        result = 61 * result + clientCode
        result = 61 * result + id
        return result and 0x7FFFFFFFFFFFFFFF
    }

    override fun toString(): String =
        "UnpackedModLevelType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "accessFlags=$accessFlags, " +
            "clientCode=$clientCode" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as UnpackedModLevelType
        if (internalId != other.internalId) return false
        if (clientCode != other.clientCode) return false
        if (accessFlags != other.accessFlags) return false
        return true
    }

    override fun hashCode(): Int {
        var result = accessFlags.hashCode()
        result = 31 * result + clientCode
        result = 31 * result + (internalId ?: 0)
        return result
    }
}
