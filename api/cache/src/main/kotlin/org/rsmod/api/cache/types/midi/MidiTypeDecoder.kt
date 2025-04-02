package org.rsmod.api.cache.types.midi

import org.rsmod.api.type.symbols.name.NameMapping
import org.rsmod.game.type.midi.MidiType
import org.rsmod.game.type.midi.MidiTypeList

public object MidiTypeDecoder {
    public fun decodeAll(nameMapping: NameMapping): MidiTypeList =
        MidiTypeList(nameMapping.toTypeMap())

    private fun NameMapping.toTypeMap(): Map<Int, MidiType> =
        midis.entries.associate { it.value to MidiType(it.value, it.key) }
}
