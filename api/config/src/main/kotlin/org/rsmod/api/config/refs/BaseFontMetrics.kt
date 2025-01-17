@file:Suppress("SpellCheckingInspection", "unused")

package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.font.FontMetricsReferences

typealias fontmetrics = BaseFontMetrics

object BaseFontMetrics : FontMetricsReferences() {
    val p11_full = find("p11_full")
    val p12_full = find("p12_full")
    val b12_full = find("b12_full")
    val q8_full = find("q8_full")
    val quill_oblique_large = find("quill_oblique_large")
    val quill_caps_large = find("quill_caps_large")
    val lunar_alphabet = find("lunar_alphabet")
    val lunar_alphabet_lrg = find("lunar_alphabet_lrg")
    val barbassault_font = find("barbassault_font")
    val surok_font = find("surok_font")
    val verdana_11pt_regular = find("verdana_11pt_regular")
    val verdana_11pt_bold = find("verdana_11pt_bold")
    val verdana_13pt_regular = find("verdana_13pt_regular")
    val verdana_13pt_bold = find("verdana_13pt_bold")
    val verdana_15pt_regular = find("verdana_15pt_regular")
}
