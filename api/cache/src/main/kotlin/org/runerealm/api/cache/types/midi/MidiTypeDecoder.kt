package org.runerealm.api.cache.types.midi

import org.runerealm.api.type.symbols.name.NameMapping
import org.runerealm.game.type.midi.MidiType
import org.runerealm.game.type.midi.MidiTypeList

public object MidiTypeDecoder {
    public fun decodeAll(nameMapping: NameMapping): MidiTypeList =
        MidiTypeList(nameMapping.toTypeMap())

    private fun NameMapping.toTypeMap(): Map<Int, MidiType> =
        midis.entries.associate { it.value to MidiType(it.value, it.key) }
}
