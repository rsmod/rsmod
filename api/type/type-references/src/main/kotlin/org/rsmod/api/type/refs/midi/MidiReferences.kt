package org.rsmod.api.type.refs.midi

import org.rsmod.api.type.refs.NameTypeReferences
import org.rsmod.game.type.midi.MidiType
import org.rsmod.game.type.midi.MidiTypeBuilder

public abstract class MidiReferences : NameTypeReferences<MidiType>(MidiType::class.java) {
    override fun find(internal: String): MidiType {
        val type = MidiTypeBuilder(internal).build(id = -1)
        cache += type
        return type
    }
}
