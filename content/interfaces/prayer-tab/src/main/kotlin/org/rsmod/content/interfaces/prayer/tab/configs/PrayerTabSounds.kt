package org.rsmod.content.interfaces.prayer.tab.configs

import org.rsmod.api.type.refs.synth.SynthReferences

internal typealias prayer_sounds = PrayerTabSounds

object PrayerTabSounds : SynthReferences() {
    val disable = find("prayer_disable")
    val drain = find("prayer_drain")
}
