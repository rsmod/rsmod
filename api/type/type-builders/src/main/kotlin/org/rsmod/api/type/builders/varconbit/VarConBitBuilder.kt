package org.rsmod.api.type.builders.varconbit

import org.rsmod.api.type.builders.NameTypeBuilder
import org.rsmod.api.type.script.dsl.VarConBitPluginBuilder
import org.rsmod.game.type.varconbit.UnpackedVarConBitType

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
