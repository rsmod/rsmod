package org.rsmod.api.type.builders.varbit

import org.rsmod.api.type.builders.HashTypeBuilder
import org.rsmod.api.type.script.dsl.VarBitPluginBuilder
import org.rsmod.game.type.varbit.UnpackedVarBitType

public abstract class VarBitBuilder : HashTypeBuilder<VarBitPluginBuilder, UnpackedVarBitType>() {
    override fun build(internal: String, init: VarBitPluginBuilder.() -> Unit) {
        val type = VarBitPluginBuilder(internal).apply(init).build(id = -1)
        cache += type
    }
}
