package org.rsmod.api.type.refs.synth

import org.rsmod.api.type.refs.NameTypeReferences
import org.rsmod.game.type.synth.SynthType
import org.rsmod.game.type.synth.SynthTypeBuilder

public abstract class SynthReferences : NameTypeReferences<SynthType>(SynthType::class.java) {
    override fun find(internal: String): SynthType {
        val type = SynthTypeBuilder(internal).build(id = -1)
        cache += type
        return type
    }
}
