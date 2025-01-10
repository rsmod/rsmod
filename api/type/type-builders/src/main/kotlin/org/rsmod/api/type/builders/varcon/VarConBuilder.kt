package org.rsmod.api.type.builders.varcon

import org.rsmod.api.type.builders.NameTypeBuilder
import org.rsmod.game.type.varcon.UnpackedVarConType
import org.rsmod.game.type.varcon.VarConTypeBuilder

public abstract class VarConBuilder : NameTypeBuilder<VarConTypeBuilder, UnpackedVarConType>() {
    override fun build(internal: String, init: VarConTypeBuilder.() -> Unit): UnpackedVarConType {
        val type = VarConTypeBuilder(internal).apply(init).build(id = -1)
        cache += type
        return type
    }
}
