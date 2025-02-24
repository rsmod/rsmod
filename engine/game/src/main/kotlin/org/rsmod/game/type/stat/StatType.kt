package org.rsmod.game.type.stat

public sealed class StatType(
    internal var internalId: Int?,
    internal var internalName: String?,
    internal var internalMaxLevel: Int? = null,
    internal var internalDisplayName: String? = null,
) {
    public val id: Int
        get() = internalId ?: error("`internalId` must not be null.")

    public val internalNameGet: String?
        get() = internalName

    public val maxLevel: Int
        get() = internalMaxLevel ?: error("`internalMaxLevel` must not be null.")

    public val displayName: String
        get() = internalDisplayName ?: error("`internalDisplayName` must not be null.")
}

public class HashedStatType(
    internal var startHash: Long? = null,
    internalId: Int? = null,
    internalName: String? = null,
    internalMaxLevel: Int? = null,
    internalDisplayName: String? = null,
    public val autoResolve: Boolean = startHash == null,
) : StatType(internalId, internalName, internalMaxLevel, internalDisplayName) {
    public val supposedHash: Long?
        get() = startHash

    override fun toString(): String =
        "StatType(internalName='$internalName', internalId=$internalId, supposedHash=$supposedHash)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HashedStatType) return false

        if (startHash != other.startHash) return false
        if (internalId != other.internalId) return false
        if (internalName != other.internalName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = internalId?.hashCode() ?: 0
        result = 31 * result + (startHash?.hashCode() ?: 0)
        return result
    }
}

public class UnpackedStatType(
    public val unreleased: Boolean,
    maxLevel: Int,
    displayName: String,
    internalId: Int,
    internalName: String,
) : StatType(internalId, internalName, maxLevel, displayName) {
    public fun toHashedType(): HashedStatType =
        HashedStatType(
            internalId = internalId,
            internalName = internalName,
            startHash = computeIdentityHash(),
            internalMaxLevel = internalMaxLevel,
            internalDisplayName = displayName,
        )

    public fun computeIdentityHash(): Long {
        val result = (internalId?.hashCode()?.toLong() ?: 0)
        return result and 0x7FFFFFFFFFFFFFFF
    }

    override fun toString(): String =
        "UnpackedStatType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "internalHash=${computeIdentityHash()}, " +
            "displayName='$displayName', " +
            "maxLevel=$maxLevel, " +
            "unreleased=$unreleased" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UnpackedStatType

        if (unreleased != other.unreleased) return false
        if (maxLevel != other.maxLevel) return false
        if (displayName != other.displayName) return false
        if (internalId != other.internalId) return false

        return true
    }

    override fun hashCode(): Int = computeIdentityHash().toInt()
}
