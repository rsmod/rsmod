package org.rsmod.content.interfaces.prayer.tab.configs

import org.rsmod.api.type.refs.comp.ComponentReferences
import org.rsmod.api.type.refs.interf.InterfaceReferences

internal typealias prayer_components = PrayerTabComponents

internal typealias prayer_interfaces = PrayerTabInterfaces

object PrayerTabComponents : ComponentReferences() {
    val quick_prayers_orb = find("orbs:prayerbutton", 6559444023822024569)
    val quick_prayers_close = find("quick_prayers:close", 665436969762071494)
    val quick_prayers_setup = find("quick_prayers:buttons", 3047365150244200707)
    val filters = find("prayer_tab:filtermenu", 1348928103225883074)
}

object PrayerTabInterfaces : InterfaceReferences() {
    val quick_prayers = find("quick_prayers", 9223372036122417871)
}
