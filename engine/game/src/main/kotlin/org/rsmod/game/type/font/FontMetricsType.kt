package org.rsmod.game.type.font

public sealed class FontMetricsType(
    internal var internalId: Int?,
    internal var internalName: String?,
) {
    public val id: Int
        get() = internalId ?: error("`internalId` must not be null.")

    public val internalNameGet: String?
        get() = internalName
}

public class HashedFontMetricsType(
    internal var startHash: Long? = null,
    internalId: Int? = null,
    internalName: String? = null,
) : FontMetricsType(internalId, internalName) {
    public val supposedHash: Long?
        get() = startHash

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
        if (internalName != other.internalName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = internalId?.hashCode() ?: 0
        result = 31 * result + (startHash?.hashCode() ?: 0)
        return result
    }
}

public class UnpackedFontMetricsType(
    public val glyphAdvances: IntArray,
    public val ascent: Int,
    public val kerning: ByteArray,
    internalId: Int,
    internalName: String,
) : FontMetricsType(internalId, internalName) {
    public fun computeIdentityHash(): Long {
        var result = glyphAdvances.contentHashCode().toLong()
        result = 61 * result + kerning.contentHashCode()
        result = 61 * result + ascent
        result = 61 * result + (internalId?.hashCode() ?: 0)
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
        if (javaClass != other?.javaClass) return false

        other as UnpackedFontMetricsType

        if (glyphAdvances != other.glyphAdvances) return false
        if (kerning != other.kerning) return false
        if (ascent != other.ascent) return false

        return true
    }

    override fun hashCode(): Int {
        var result = glyphAdvances.contentHashCode()
        result = 31 * result + kerning.contentHashCode()
        result = 31 * result + ascent
        return result
    }
}
