package org.rsmod.game.type.interf

import org.rsmod.game.type.comp.ComponentType

public sealed class InterfaceType(
    internal var internalId: Int?,
    internal var internalName: String?,
) {
    public val id: Int
        get() = internalId ?: error("`internalId` must not be null.")

    public val internalNameGet: String?
        get() = internalName
}

public class HashedInterfaceType(
    internal val startHash: Long? = null,
    internalId: Int? = null,
    internalName: String? = null,
) : InterfaceType(internalId, internalName) {
    public val supposedHash: Long?
        get() = startHash

    override fun toString(): String =
        "InterfaceType(internalName='$internalName', internalId=$internalId, " +
            "supposedHash=$supposedHash)"

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

public class UnpackedInterfaceType(
    public val components: List<ComponentType>,
    internalName: String,
    id: Int,
) : InterfaceType(id, internalName) {
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
