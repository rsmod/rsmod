package org.runerealm.api.type.builders.varbit

import org.runerealm.api.type.builders.HashTypeBuilder
import org.runerealm.api.type.script.dsl.VarBitPluginBuilder
import org.runerealm.game.type.varbit.UnpackedVarBitType

public abstract class VarBitBuilder : HashTypeBuilder<VarBitPluginBuilder, UnpackedVarBitType>() {
    override fun build(internal: String, init: VarBitPluginBuilder.() -> Unit) {
        val type = VarBitPluginBuilder(internal).apply(init).build(id = -1)
        cache += type
    }
}
