package org.rsmod.game.type.headbar

import org.rsmod.game.type.CacheType
import org.rsmod.game.type.HashedCacheType

public sealed class HeadbarType : CacheType()

public data class HashedHeadbarType(
    override var startHash: Long?,
    override var internalName: String?,
    override var internalId: Int? = null,
) : HashedCacheType, HeadbarType() {
    public val autoResolve: Boolean = startHash == null

    override fun toString(): String =
        "HeadbarType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "supposedHash=$supposedHash" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HashedHeadbarType) return false
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

public data class UnpackedHeadbarType(
    public val unknown1: Int,
    public val showPriority: Int,
    public val hidePriority: Int,
    public val fadeout: Int,
    public val stickTime: Int,
    public val unknown6: Int,
    public val full: Int?,
    public val empty: Int?,
    public val segments: Int,
    public val padding: Int,
    override var internalId: Int?,
    override var internalName: String?,
) : HeadbarType() {
    private val identityHash by lazy { computeIdentityHash() }

    public fun toHashedType(): HashedHeadbarType =
        HashedHeadbarType(
            startHash = identityHash,
            internalName = internalName,
            internalId = internalId,
        )

    public fun computeIdentityHash(): Long {
        var result = showPriority.toLong()
        result = 61 * result + hidePriority
        result = 61 * result + fadeout
        result = 61 * result + stickTime
        result = 61 * result + (full ?: 0)
        result = 61 * result + (empty ?: 0)
        result = 61 * result + segments
        result = 61 * result + (internalId ?: 0)
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UnpackedHeadbarType) return false
        if (internalId != other.internalId) return false
        if (unknown1 != other.unknown1) return false
        if (showPriority != other.showPriority) return false
        if (hidePriority != other.hidePriority) return false
        if (fadeout != other.fadeout) return false
        if (stickTime != other.stickTime) return false
        if (unknown6 != other.unknown6) return false
        if (full != other.full) return false
        if (empty != other.empty) return false
        if (segments != other.segments) return false
        if (padding != other.padding) return false
        return true
    }

    override fun hashCode(): Int {
        var result = unknown1
        result = 31 * result + showPriority
        result = 31 * result + hidePriority
        result = 31 * result + fadeout
        result = 31 * result + stickTime
        result = 31 * result + unknown6
        result = 31 * result + (full ?: 0)
        result = 31 * result + (empty ?: 0)
        result = 31 * result + segments
        result = 31 * result + padding
        result = 31 * result + (internalId ?: 0)
        return result
    }
}
