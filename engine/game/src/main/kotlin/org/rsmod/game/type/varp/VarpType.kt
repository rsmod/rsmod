package org.rsmod.game.type.varp

import org.rsmod.game.type.CacheType
import org.rsmod.game.type.HashedCacheType

public sealed class VarpType : CacheType() {
    internal abstract var internalScope: VarpLifetime
    internal abstract var internalTransmit: VarpTransmitLevel

    public val transmit: VarpTransmitLevel
        get() = internalTransmit

    public val scope: VarpLifetime
        get() = internalScope
}

public data class HashedVarpType(
    override var startHash: Long?,
    override var internalName: String?,
    override var internalId: Int? = null,
    override var internalScope: VarpLifetime = VarpTypeBuilder.DEFAULT_SCOPE,
    override var internalTransmit: VarpTransmitLevel = VarpTypeBuilder.DEFAULT_TRANSMIT,
) : HashedCacheType, VarpType() {
    public val autoResolve: Boolean = startHash == null

    override fun toString(): String =
        "VarpType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "supposedHash=$supposedHash, " +
            "transmit=$internalTransmit, " +
            "scope=$internalScope" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HashedVarpType) return false
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

public data class UnpackedVarpType(
    public val bitProtect: Boolean,
    public val clientCode: Int,
    override var internalScope: VarpLifetime,
    override var internalTransmit: VarpTransmitLevel,
    override var internalId: Int?,
    override var internalName: String?,
) : VarpType() {
    public fun computeIdentityHash(): Long {
        var result = internalId?.hashCode()?.toLong() ?: 0L
        result = 61 * result + internalScope.id.hashCode()
        result = 61 * result + internalTransmit.id.hashCode()
        result = 61 * result + clientCode.hashCode()
        return result and 0x7FFFFFFFFFFFFFFF
    }

    override fun toString(): String =
        "UnpackedVarpType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "bitProtect=$bitProtect, " +
            "clientCode=$clientCode, " +
            "transmit=$internalTransmit, " +
            "scope=$internalScope" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UnpackedVarpType) return false
        if (bitProtect != other.bitProtect) return false
        if (clientCode != other.clientCode) return false
        if (internalTransmit != other.internalTransmit) return false
        if (internalScope != other.internalScope) return false
        if (internalId != other.internalId) return false
        return true
    }

    override fun hashCode(): Int = computeIdentityHash().toInt()
}
