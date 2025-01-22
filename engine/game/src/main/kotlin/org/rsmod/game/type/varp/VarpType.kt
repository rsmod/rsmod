package org.rsmod.game.type.varp

public sealed class VarpType(
    internal var internalId: Int?,
    internal var internalName: String?,
    internal var internalTransmit: Boolean = true,
    internal var internalProtect: Boolean = false,
) {
    public val internalNameGet: String?
        get() = internalName

    public val id: Int
        get() = internalId ?: error("`internalId` must not be null.")

    public val transmit: Boolean
        get() = internalTransmit

    public val protect: Boolean
        get() = internalProtect
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
            "protect=$internalProtect" +
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
    transmit: Boolean,
    protect: Boolean,
    internalId: Int,
    internalName: String,
) : VarpType(internalId, internalName, transmit, protect) {
    public fun computeIdentityHash(): Long {
        var result = clientCode.hashCode().toLong()
        result = 61 * result + internalTransmit.hashCode()
        result = 61 * result + internalProtect.hashCode()
        result = 61 * result + (internalId?.hashCode()?.toLong() ?: 0)
        return result and 0x7FFFFFFFFFFFFFFF
    }

    override fun toString(): String =
        "UnpackedVarpType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "bitProtect=$bitProtect, " +
            "clientCode=$clientCode, " +
            "transmit=$internalTransmit, " +
            "protect=$internalProtect" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UnpackedVarpType) return false

        if (bitProtect != other.bitProtect) return false
        if (clientCode != other.clientCode) return false
        if (internalTransmit != other.internalTransmit) return false
        if (internalProtect != other.internalProtect) return false
        if (internalId != other.internalId) return false

        return true
    }

    override fun hashCode(): Int = computeIdentityHash().toInt()
}
