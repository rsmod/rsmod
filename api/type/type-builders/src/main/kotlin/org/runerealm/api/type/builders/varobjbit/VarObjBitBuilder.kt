package org.runerealm.api.type.builders.varobjbit

import org.runerealm.api.type.builders.NameTypeBuilder
import org.runerealm.api.type.script.dsl.VarObjBitPluginBuilder
import org.runerealm.game.type.varobjbit.UnpackedVarObjBitType

public abstract class VarObjBitBuilder :
    NameTypeBuilder<VarObjBitPluginBuilder, UnpackedVarObjBitType>() {
    override fun build(
        internal: String,
        init: VarObjBitPluginBuilder.() -> Unit,
    ): UnpackedVarObjBitType {
        val type = VarObjBitPluginBuilder(internal).apply(init).build(id = -1)
        cache += type
        return type
    }
}
