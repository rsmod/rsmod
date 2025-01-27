package org.rsmod.content.interfaces.prayer.tab.configs

import org.rsmod.api.type.refs.varbit.VarBitReferences

internal typealias prayer_varbits = PrayerTabVarBits

object PrayerTabVarBits : VarBitReferences() {
    val filter_show_lower_tiers = find("prayer_filter_show_lower_tiers", 1492222260)
    val filter_show_tiered_prayers = find("prayer_filter_show_tiered_prayers", 1492453023)
    val filter_show_rapid_healing = find("prayer_filter_show_rapid_healing", 1492683786)
    val filter_show_prayers_fail_lvl = find("prayer_filter_show_prayers_without_level", 1492914549)
    val filter_show_prayers_fail_req =
        find("prayer_filter_show_prayers_without_requirements", 1493145312)
    val enabled_prayers = find("prayer_enabled_full", 930850789)
    val selected_quick_prayers = find("quickprayer_enabled_full", 931077770)
    val using_quick_prayers = find("using_quick_prayers", 931303043)
}
