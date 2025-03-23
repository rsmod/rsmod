package org.rsmod.game.type.hitmark

import org.rsmod.game.type.CacheType
import org.rsmod.game.type.HashedCacheType

public sealed class HitmarkType : CacheType()

public data class HashedHitmarkType(
    override var startHash: Long?,
    override var internalName: String?,
    override var internalId: Int? = null,
) : HashedCacheType, HitmarkType() {
    public val autoResolve: Boolean = startHash == null

    override fun toString(): String =
        "HitmarkType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "supposedHash=$supposedHash" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HashedHitmarkType) return false
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

public data class UnpackedHitmarkType(
    public val damageFont: Int,
    public val damageColour: Int,
    public val classGraphic: Int,
    public val leftGraphic: Int,
    public val middleGraphic: Int,
    public val rightGraphic: Int,
    public val scrollToOffsetX: Int,
    public val damageFormat: String,
    public val stickTime: Int,
    public val scrollToOffsetY: Int,
    public val fadeout: Int,
    public val replaceMode: Int,
    public val damageYOf: Int,
    public val multiVarp: Int?,
    public val multiVarBit: Int?,
    public val multiMarkDefault: Int?,
    public val multiMark: ShortArray?,
    override var internalId: Int?,
    override var internalName: String?,
) : HitmarkType() {
    private val identityHash by lazy { computeIdentityHash() }

    public fun toHashedType(): HashedHitmarkType =
        HashedHitmarkType(
            startHash = identityHash,
            internalName = internalName,
            internalId = internalId,
        )

    public fun computeIdentityHash(): Long {
        var result = middleGraphic.toLong()
        result = 61 * result + damageColour
        result = 61 * result + classGraphic
        result = 61 * result + (multiVarp?.hashCode() ?: 0)
        result = 61 * result + (multiVarBit?.hashCode() ?: 0)
        result = 61 * result + (multiMark?.contentHashCode() ?: 0)
        result = 61 * result + (internalId ?: 0)
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UnpackedHitmarkType) return false
        if (internalId != other.internalId) return false
        if (damageFont != other.damageFont) return false
        if (damageColour != other.damageColour) return false
        if (classGraphic != other.classGraphic) return false
        if (leftGraphic != other.leftGraphic) return false
        if (middleGraphic != other.middleGraphic) return false
        if (rightGraphic != other.rightGraphic) return false
        if (scrollToOffsetX != other.scrollToOffsetX) return false
        if (stickTime != other.stickTime) return false
        if (scrollToOffsetY != other.scrollToOffsetY) return false
        if (fadeout != other.fadeout) return false
        if (replaceMode != other.replaceMode) return false
        if (damageYOf != other.damageYOf) return false
        if (damageFormat != other.damageFormat) return false
        if (multiMarkDefault != other.multiMarkDefault) return false
        if (multiVarp != other.multiVarp) return false
        if (multiVarBit != other.multiVarBit) return false
        if (!multiMark.contentEquals(other.multiMark)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = damageFont
        result = 31 * result + damageColour
        result = 31 * result + classGraphic
        result = 31 * result + leftGraphic
        result = 31 * result + middleGraphic
        result = 31 * result + rightGraphic
        result = 31 * result + scrollToOffsetX
        result = 31 * result + stickTime
        result = 31 * result + scrollToOffsetY
        result = 31 * result + fadeout
        result = 31 * result + replaceMode
        result = 31 * result + damageYOf
        result = 31 * result + (internalId ?: 0)
        result = 31 * result + multiMarkDefault.hashCode()
        result = 31 * result + damageFormat.hashCode()
        result = 31 * result + (multiVarp?.hashCode() ?: 0)
        result = 31 * result + (multiVarBit?.hashCode() ?: 0)
        result = 31 * result + (multiMark?.contentHashCode() ?: 0)
        return result
    }
}
