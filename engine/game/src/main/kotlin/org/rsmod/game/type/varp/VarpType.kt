package org.rsmod.game.type.varp

public sealed class VarpType(
    internal var internalId: Int?,
    internal var internalName: String?,
    internal var internalScope: VarpLifetime = VarpLifetime.Perm,
    internal var internalTransmit: VarpTransmitLevel = VarpTransmitLevel.OnSetAlways,
) {
    public val internalNameGet: String?
        get() = internalName

    public val id: Int
        get() = internalId ?: error("`internalId` must not be null.")

    public val transmit: VarpTransmitLevel
        get() = internalTransmit

    public val scope: VarpLifetime
        get() = internalScope
}

public class HashedVarpType(
    internal var startHash: Long? = null,
    internalId: Int? = null,
    internalName: String? = null,
    public val autoResolve: Boolean = startHash == null,
) : VarpType(internalId, internalName) {
    public val supposedHash: Long?
        get() = startHash

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

public class UnpackedVarpType(
    public val bitProtect: Boolean,
    public val clientCode: Int,
    scope: VarpLifetime,
    transmit: VarpTransmitLevel,
    internalId: Int,
    internalName: String,
) : VarpType(internalId, internalName, scope, transmit) {
    public fun computeIdentityHash(): Long {
        var result = internalId?.hashCode()?.toLong() ?: 0L
        result = 61 * result + scope.id.hashCode()
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
