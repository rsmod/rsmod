package org.rsmod.game.type.interf

import kotlin.contracts.contract
import org.rsmod.game.type.CacheType
import org.rsmod.game.type.HashedCacheType
import org.rsmod.game.type.comp.ComponentType

public sealed class InterfaceType : CacheType()

public data class HashedInterfaceType(
    override var startHash: Long?,
    override var internalName: String?,
    override var internalId: Int? = null,
) : HashedCacheType, InterfaceType() {
    public val autoResolve: Boolean = startHash == null

    override fun toString(): String =
        "InterfaceType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "supposedHash=$supposedHash" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HashedInterfaceType) return false
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

public data class UnpackedInterfaceType(
    public val components: List<ComponentType>,
    override var internalId: Int?,
    override var internalName: String?,
) : InterfaceType() {
    private val identityHash by lazy { computeIdentityHash() }

    public fun toHashedType(): HashedInterfaceType =
        HashedInterfaceType(
            startHash = identityHash,
            internalName = internalName,
            internalId = internalId,
        )

    public fun computeIdentityHash(): Long {
        var result = internalId?.hashCode()?.toLong() ?: 0
        result = 61 * result + components.hashCode()
        return result and 0x7FFFFFFFFFFFFFFF
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UnpackedInterfaceType) return false
        if (other.internalId != internalId) return false
        if (other.components != components) return false
        return true
    }

    override fun hashCode(): Int = computeIdentityHash().toInt()
}

public fun InterfaceType?.isType(other: InterfaceType): Boolean {
    contract { returns(true) implies (this@isType != null) }
    return this != null && this.id == other.id
}
