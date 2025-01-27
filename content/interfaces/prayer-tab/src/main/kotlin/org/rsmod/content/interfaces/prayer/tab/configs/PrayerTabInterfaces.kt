package org.rsmod.content.interfaces.prayer.tab.configs

import org.rsmod.api.type.refs.comp.ComponentReferences
import org.rsmod.api.type.refs.interf.InterfaceReferences

internal typealias prayer_components = PrayerTabComponents

internal typealias prayer_interfaces = PrayerTabInterfaces

object PrayerTabComponents : ComponentReferences() {
    val quick_prayers_orb = find("orbs_com19")
    val quick_prayers_close = find("quick_prayers_com5")
    val quick_prayers_setup = find("quick_prayers_com4")
    val filters = find("prayer_tab_com42")
}

object PrayerTabInterfaces : InterfaceReferences() {
    val quick_prayers = find("quick_prayers")
}
