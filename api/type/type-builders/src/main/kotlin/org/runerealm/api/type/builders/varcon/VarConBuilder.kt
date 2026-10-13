package org.runerealm.api.type.builders.varcon

import org.runerealm.api.type.builders.NameTypeBuilder
import org.runerealm.game.type.varcon.UnpackedVarConType
import org.runerealm.game.type.varcon.VarConTypeBuilder

public abstract class VarConBuilder : NameTypeBuilder<VarConTypeBuilder, UnpackedVarConType>() {
    override fun build(internal: String, init: VarConTypeBuilder.() -> Unit): UnpackedVarConType {
        val type = VarConTypeBuilder(internal).apply(init).build(id = -1)
        cache += type
        return type
    }
}
