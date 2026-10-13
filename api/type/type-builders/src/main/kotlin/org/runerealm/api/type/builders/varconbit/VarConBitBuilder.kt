package org.runerealm.api.type.builders.varconbit

import org.runerealm.api.type.builders.NameTypeBuilder
import org.runerealm.api.type.script.dsl.VarConBitPluginBuilder
import org.runerealm.game.type.varconbit.UnpackedVarConBitType

public abstract class VarConBitBuilder :
    NameTypeBuilder<VarConBitPluginBuilder, UnpackedVarConBitType>() {
    override fun build(
        internal: String,
        init: VarConBitPluginBuilder.() -> Unit,
    ): UnpackedVarConBitType {
        val type = VarConBitPluginBuilder(internal).apply(init).build(id = -1)
        cache += type
        return type
    }
}
