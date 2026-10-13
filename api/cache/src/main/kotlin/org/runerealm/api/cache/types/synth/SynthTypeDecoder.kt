package org.runerealm.api.cache.types.synth

import org.runerealm.api.type.symbols.name.NameMapping
import org.runerealm.game.type.synth.SynthType
import org.runerealm.game.type.synth.SynthTypeList

public object SynthTypeDecoder {
    public fun decodeAll(nameMapping: NameMapping): SynthTypeList =
        SynthTypeList(nameMapping.toTypeMap())

    private fun NameMapping.toTypeMap(): Map<Int, SynthType> =
        synths.entries.associate { it.value to SynthType(it.value, it.key) }
}
