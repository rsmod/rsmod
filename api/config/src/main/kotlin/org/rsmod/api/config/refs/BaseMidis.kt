package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.midi.MidiReferences

typealias midis = BaseMidis

object BaseMidis : MidiReferences() {
    val stop_music = find("stop_music")
}
