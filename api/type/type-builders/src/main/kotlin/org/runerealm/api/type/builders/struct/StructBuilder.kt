package org.runerealm.api.type.builders.struct

import org.runerealm.api.type.builders.HashTypeBuilder
import org.runerealm.game.type.struct.StructTypeBuilder
import org.runerealm.game.type.struct.UnpackedStructType

public abstract class StructBuilder : HashTypeBuilder<StructTypeBuilder, UnpackedStructType>() {
    override fun build(internal: String, init: StructTypeBuilder.() -> Unit) {
        val type = StructTypeBuilder(internal).apply(init).build(id = -1)
        cache += type
    }
}
