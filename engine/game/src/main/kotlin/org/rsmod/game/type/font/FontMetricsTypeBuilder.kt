package org.rsmod.game.type.font

@DslMarker private annotation class FontMetricBuilderDsl

@FontMetricBuilderDsl
public class FontMetricsTypeBuilder(public var internal: String? = null) {
    public var charWidths: IntArray? = null
    public var fontWidths: IntArray? = null
    public var fontHeights: IntArray? = null
    public var kerning: ByteArray? = null
    public var ascent: Int? = null
    public var maxAscent: Int? = null
    public var maxDescent: Int? = null

    public fun build(id: Int): UnpackedFontMetricsType {
        val internal = internal ?: error("`internal` must be set.")
        val charWidths = charWidths ?: error("`charWidths` must be set.")
        val kerning = kerning ?: byteArrayOf()
        val ascent = ascent ?: error("`ascent` must be set.")
        return UnpackedFontMetricsType(
            charWidths = charWidths,
            kerning = kerning,
            ascent = ascent,
            internalId = id,
            internalName = internal,
        )
    }
}
