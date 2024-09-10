package org.rsmod.game.type.font

@DslMarker private annotation class FontMetricBuilderDsl

@FontMetricBuilderDsl
public class FontMetricsTypeBuilder(public var internal: String? = null) {
    public var glyphAdvances: IntArray? = null
    public var ascent: Int? = null
    public var kerning: ByteArray? = null

    public fun build(id: Int): UnpackedFontMetricsType {
        val internal = internal ?: error("`internal` must be set.")
        val glyphAdvances = glyphAdvances ?: error("`glyphAdvances` must be set.")
        val ascent = ascent ?: error("`ascent` must be set.")
        val kerning = kerning ?: byteArrayOf()
        return UnpackedFontMetricsType(
            glyphAdvances = glyphAdvances,
            kerning = kerning,
            ascent = ascent,
            internalId = id,
            internalName = internal,
        )
    }
}
