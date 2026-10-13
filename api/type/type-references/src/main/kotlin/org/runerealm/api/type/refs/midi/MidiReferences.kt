package org.runerealm.api.type.refs.midi

import org.runerealm.api.type.refs.NameTypeReferences
import org.runerealm.game.type.midi.MidiType
import org.runerealm.game.type.midi.MidiTypeBuilder

public abstract class MidiReferences : NameTypeReferences<MidiType>(MidiType::class.java) {
    override fun find(internal: String): MidiType {
        val type = MidiTypeBuilder(internal).build(id = -1)
        cache += type
        return type
    }
}
