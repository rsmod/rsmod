package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.font.FontMetricsReferences
import org.rsmod.game.type.font.FontMetricsType

public typealias fontmetrics = BaseFontMetrics

public object BaseFontMetrics : FontMetricsReferences() {
    public val p11_full: FontMetricsType = find(9223004667748331596)
    public val p12_full: FontMetricsType = find(423563882132785)
    public val b12_full: FontMetricsType = find(70832286009131)
    public val q8_full: FontMetricsType = find(9223249086231356720)
    public val quill_oblique_large: FontMetricsType = find(9223013548038351318)
    public val quill_caps_large: FontMetricsType = find(163612596505236)
    public val lunar_alphabet: FontMetricsType = find(212540834679631)
    public val lunar_alphabet_lrg: FontMetricsType = find(9223136279847326893)
    public val barbassault_font: FontMetricsType = find(365775886867391)
    public val surok_font: FontMetricsType = find(385299711149352)
    public val verdana_11pt_regular: FontMetricsType = find(9223273769553035047)
    public val verdana_11pt_bold: FontMetricsType = find(258953961539320)
    public val verdana_13pt_regular: FontMetricsType = find(421521454437546)
    public val verdana_13pt_bold: FontMetricsType = find(9223192018622715708)
    public val verdana_15pt_regular: FontMetricsType = find(9222885501338976955)
}
