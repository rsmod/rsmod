package org.rsmod.game.type.varnbit

import org.rsmod.game.type.CacheType
import org.rsmod.game.type.HashedCacheType
import org.rsmod.game.type.varn.VarnType

public sealed class VarnBitType : CacheType() {
    internal abstract var internalVarn: VarnType?
    internal abstract var internalLsb: Int?
    internal abstract var internalMsb: Int?

    public val baseVar: VarnType
        get() = internalVarn ?: error("`internalVarn` must not be null.")

    public val lsb: Int
        get() = internalLsb ?: error("`internalLsb` must not be null.")

    public val msb: Int
        get() = internalMsb ?: error("`internalMsb` must not be null.")

    public val bits: IntRange
        get() = lsb..msb
}

public data class HashedVarnBitType(
    override var startHash: Long?,
    override var internalName: String?,
    override var internalId: Int? = null,
    override var internalVarn: VarnType? = null,
    override var internalLsb: Int? = null,
    override var internalMsb: Int? = null,
) : HashedCacheType, VarnBitType() {
    public val autoResolve: Boolean = startHash == null

    override fun toString(): String =
        "VarnBitType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "baseVar=$internalVarn, " +
            "bits=$bits, " +
            "supposedHash=$supposedHash" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HashedVarnBitType) return false
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

public data class UnpackedVarnBitType(
    public val varnId: Int,
    override var internalVarn: VarnType?,
    override var internalLsb: Int?,
    override var internalMsb: Int?,
    override var internalId: Int?,
    override var internalName: String?,
) : VarnBitType() {
    override fun toString(): String =
        "UnpackedVarnBitType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "baseVar=$internalVarn, " +
            "bits=$bits" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UnpackedVarnBitType) return false
        if (varnId != other.varnId) return false
        if (internalLsb != other.internalLsb) return false
        if (internalMsb != other.internalMsb) return false
        if (internalId != other.internalId) return false
        return true
    }

    override fun hashCode(): Int {
        var result = internalVarn?.hashCode() ?: 0
        result = 31 * result + (internalLsb ?: 0)
        result = 31 * result + (internalMsb ?: 0)
        result = 31 * result + (internalId ?: 0)
        return result
    }
}
