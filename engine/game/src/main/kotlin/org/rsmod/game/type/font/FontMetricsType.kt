package org.rsmod.game.type.font

import org.rsmod.game.type.CacheType
import org.rsmod.game.type.HashedCacheType

public sealed class FontMetricsType : CacheType()

public data class HashedFontMetricsType(
    override var startHash: Long?,
    override var internalName: String?,
    override var internalId: Int? = null,
) : HashedCacheType, FontMetricsType() {
    public val autoResolve: Boolean = startHash == null

    override fun toString(): String =
        "FontMetricsType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "supposedHash=$supposedHash" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HashedFontMetricsType) return false
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

public data class UnpackedFontMetricsType(
    public val glyphAdvances: IntArray,
    public val ascent: Int,
    public val kerning: ByteArray,
    override var internalId: Int?,
    override var internalName: String?,
) : FontMetricsType() {
    public fun computeIdentityHash(): Long {
        var result = (internalId?.hashCode()?.toLong() ?: 0)
        result = 61 * result + glyphAdvances.contentHashCode().toLong()
        result = 61 * result + kerning.contentHashCode()
        result = 61 * result + ascent
        return result and 0x7FFFFFFFFFFFFFFF
    }

    override fun toString(): String =
        "UnpackedFontMetricsType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "glyphAdvances=$glyphAdvances, " +
            "ascent=$ascent" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UnpackedFontMetricsType) return false
        if (ascent != other.ascent) return false
        if (!glyphAdvances.contentEquals(other.glyphAdvances)) return false
        if (!kerning.contentEquals(other.kerning)) return false
        if (internalId != other.internalId) return false
        return true
    }

    override fun hashCode(): Int {
        var result = (internalId ?: 0)
        result = 31 * result + ascent
        result = 31 * result + glyphAdvances.contentHashCode()
        result = 31 * result + kerning.contentHashCode()
        return result
    }
}
