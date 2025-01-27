package org.rsmod.content.interfaces.prayer.tab.configs

import org.rsmod.api.type.refs.varbit.VarBitReferences

internal typealias prayer_varbits = PrayerTabVarBits

object PrayerTabVarBits : VarBitReferences() {
    val filter_show_lower_tiers = find("prayer_filter_show_lower_tiers")
    val filter_show_tiered_prayers = find("prayer_filter_show_tiered_prayers")
    val filter_show_rapid_healing = find("prayer_filter_show_rapid_healing")
    val filter_show_prayers_fail_lvl = find("prayer_filter_show_prayers_without_level")
    val filter_show_prayers_fail_req = find("prayer_filter_show_prayers_without_requirements")
    val enabled_prayers = find("prayer_enabled_full")
    val selected_quick_prayers = find("quickprayer_enabled_full")
    val using_quick_prayers = find("using_quick_prayers")
}
