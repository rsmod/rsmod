package org.rsmod.api.cache.types.synth

import org.rsmod.api.type.symbols.name.NameMapping
import org.rsmod.game.type.synth.SynthType
import org.rsmod.game.type.synth.SynthTypeList

public object SynthTypeDecoder {
    public fun decodeAll(nameMapping: NameMapping): SynthTypeList =
        SynthTypeList(nameMapping.toTypeMap())

    private fun NameMapping.toTypeMap(): Map<Int, SynthType> =
        synths.entries.associate { it.value to SynthType(it.value, it.key) }
}
