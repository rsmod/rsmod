package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.font.FontMetricsReferences
import org.rsmod.game.type.font.FontMetricsType

public typealias fontmetrics = BaseFontMetrics

public object BaseFontMetrics : FontMetricsReferences() {
    public val p11_full: FontMetricsType = find("p11_full")
    public val p12_full: FontMetricsType = find("p12_full")
    public val b12_full: FontMetricsType = find("b12_full")
    public val q8_full: FontMetricsType = find("q8_full")
    public val quill_oblique_large: FontMetricsType = find("quill_oblique_large")
    public val quill_caps_large: FontMetricsType = find("quill_caps_large")
    public val lunar_alphabet: FontMetricsType = find("lunar_alphabet")
    public val lunar_alphabet_lrg: FontMetricsType = find("lunar_alphabet_lrg")
    public val barbassault_font: FontMetricsType = find("barbassault_font")
    public val surok_font: FontMetricsType = find("surok_font")
    public val verdana_11pt_regular: FontMetricsType = find("verdana_11pt_regular")
    public val verdana_11pt_bold: FontMetricsType = find("verdana_11pt_bold")
    public val verdana_13pt_regular: FontMetricsType = find("verdana_13pt_regular")
    public val verdana_13pt_bold: FontMetricsType = find("verdana_13pt_bold")
    public val verdana_15pt_regular: FontMetricsType = find("verdana_15pt_regular")
}
