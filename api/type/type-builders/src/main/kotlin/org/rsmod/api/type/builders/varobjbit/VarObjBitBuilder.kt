package org.rsmod.api.type.builders.varobjbit

import org.rsmod.api.type.builders.NameTypeBuilder
import org.rsmod.api.type.script.dsl.VarObjBitPluginBuilder
import org.rsmod.game.type.varobjbit.UnpackedVarObjBitType

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
