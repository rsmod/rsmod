package org.rsmod.game.type.varbit

import org.rsmod.game.type.varp.VarpType

public sealed class VarBitType(
    internal var internalId: Int?,
    internal var internalName: String?,
    internal var internalVarp: VarpType? = null,
    internal var internalLsb: Int? = null,
    internal var internalMsb: Int? = null,
) {
    public val internalNameGet: String?
        get() = internalName

    public val id: Int
        get() = internalId ?: error("`internalId` must not be null.")

    public val baseVar: VarpType
        get() = internalVarp ?: error("`varp` must not be null.")

    public val bits: IntRange
        get() = bitRange()

    private fun bitRange(): IntRange {
        val lsb = internalLsb ?: error("`lsb` must not be null.")
        val msb = internalMsb ?: error("`msb` must not be null.")
        return lsb..msb
    }
}

public class HashedVarBitType(
    internal var startHash: Long? = null,
    internalId: Int? = null,
    internalName: String? = null,
    public val autoResolve: Boolean = startHash == null,
) : VarBitType(internalId, internalName) {
    public val supposedHash: Long?
        get() = startHash

    override fun toString(): String =
        "VarBitType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "baseVar=$internalVarp, " +
            "lsb=$internalLsb, " +
            "msb=$internalMsb, " +
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

public class UnpackedVarBitType(
    public val varpId: Int,
    public val lsb: Int,
    public val msb: Int,
    public val varp: VarpType?,
    internalId: Int,
    internalName: String,
) : VarBitType(internalId, internalName, varp, lsb, msb) {
    public fun computeIdentityHash(): Long {
        var result = varp?.internalId?.hashCode()?.toLong() ?: 0L
        result = 61 * result + lsb.hashCode()
        result = 61 * result + msb.hashCode()
        result = 61 * result + (internalId?.hashCode()?.toLong() ?: 0)
        return result and 0x7FFFFFFFFFFFFFFF
    }

    override fun toString(): String =
        "UnpackedVarBitType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "baseVar=$internalVarp, " +
            "lsb=$internalLsb, " +
            "msb=$internalMsb" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UnpackedVarBitType

        if (varpId != other.varpId) return false
        if (lsb != other.lsb) return false
        if (msb != other.msb) return false
        if (internalId != other.internalId) return false

        return true
    }

    override fun hashCode(): Int = computeIdentityHash().toInt()
}
