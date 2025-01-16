package org.rsmod.game.type.struct

import org.rsmod.game.type.util.ParamMap

public sealed class StructType(internal var internalId: Int?, internal var internalName: String?) {
    public val id: Int
        get() = internalId ?: error("`internalId` must not be null.")

    public val internalNameGet: String?
        get() = internalName
}

public class HashedStructType(
    internal var startHash: Long? = null,
    internalId: Int? = null,
    internalName: String? = null,
    public val autoResolve: Boolean = startHash == null,
) : StructType(internalId, internalName) {
    public val supposedHash: Long?
        get() = startHash

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

public class UnpackedStructType(
    public val paramMap: ParamMap?,
    internalId: Int,
    internalName: String,
) : StructType(internalId, internalName) {
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
