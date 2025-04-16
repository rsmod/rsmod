package org.rsmod.content.interfaces.prayer.tab.configs

import org.rsmod.api.type.refs.varbit.VarBitReferences

internal typealias prayer_varbits = PrayerTabVarBits

object PrayerTabVarBits : VarBitReferences() {
    val filter_show_lower_tiers = find("prayer_filter_blocklowtier", 153119424717634)
    val filter_show_tiered_prayers = find("prayer_filter_allowcombinedtier", 153119424721417)
    val filter_show_rapid_healing = find("prayer_filter_blockhealing", 153119424725200)
    val filter_show_prayers_fail_lvl = find("prayer_filter_blocklacklevel", 153119424728983)
    val filter_show_prayers_fail_req = find("prayer_filter_blocklocked", 153119424732766)
    val quickprayer_selected = find("quickprayer_selected", 4328583513136)
}
