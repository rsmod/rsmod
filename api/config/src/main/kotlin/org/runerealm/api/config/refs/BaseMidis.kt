package org.runerealm.api.config.refs

import org.runerealm.api.type.refs.midi.MidiReferences

typealias midis = BaseMidis

object BaseMidis : MidiReferences() {
    val stop_music = find("stop_music")
}
