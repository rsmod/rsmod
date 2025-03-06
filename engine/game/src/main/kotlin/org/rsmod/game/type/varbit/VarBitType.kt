package org.rsmod.game.type.varbit

import org.rsmod.game.type.CacheType
import org.rsmod.game.type.HashedCacheType
import org.rsmod.game.type.varp.VarpType

public sealed class VarBitType : CacheType() {
    internal abstract var internalVarp: VarpType?
    internal abstract var internalLsb: Int?
    internal abstract var internalMsb: Int?

    public val baseVar: VarpType
        get() = internalVarp ?: error("`internalVarp` must not be null.")

    public val lsb: Int
        get() = internalLsb ?: error("`internalLsb` must not be null.")

    public val msb: Int
        get() = internalMsb ?: error("`internalMsb` must not be null.")

    public val bits: IntRange
        get() = lsb..msb
}

public data class HashedVarBitType(
    override var startHash: Long?,
    override var internalName: String?,
    override var internalId: Int? = null,
    override var internalVarp: VarpType? = null,
    override var internalLsb: Int? = null,
    override var internalMsb: Int? = null,
) : HashedCacheType, VarBitType() {
    public val autoResolve: Boolean = startHash == null

    override fun toString(): String =
        "VarBitType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "baseVar=$internalVarp, " +
            "bits=$internalLsb..$internalMsb, " +
            "supposedHash=$supposedHash" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HashedVarBitType) return false
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

public data class UnpackedVarBitType(
    public val varpId: Int,
    override var internalVarp: VarpType?,
    override var internalLsb: Int?,
    override var internalMsb: Int?,
    override var internalId: Int?,
    override var internalName: String?,
) : VarBitType() {
    public fun computeIdentityHash(): Long {
        var result = internalVarp?.hashCode()?.toLong() ?: 0L
        result = 61 * result + (internalLsb?.hashCode() ?: 0)
        result = 61 * result + (internalMsb?.hashCode() ?: 0)
        result = 61 * result + (internalId?.hashCode() ?: 0)
        return result and 0x7FFFFFFFFFFFFFFF
    }

    override fun toString(): String =
        "UnpackedVarBitType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "baseVar=$internalVarp, " +
            "bits=$bits" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UnpackedVarBitType) return false
        if (varpId != other.varpId) return false
        if (lsb != other.lsb) return false
        if (msb != other.msb) return false
        if (internalId != other.internalId) return false
        return true
    }

    override fun hashCode(): Int = computeIdentityHash().toInt()
}
