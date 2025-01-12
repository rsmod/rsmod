package org.rsmod.api.type.builders.struct

import org.rsmod.api.type.builders.HashTypeBuilder
import org.rsmod.game.type.struct.StructTypeBuilder
import org.rsmod.game.type.struct.UnpackedStructType

public abstract class StructBuilder : HashTypeBuilder<StructTypeBuilder, UnpackedStructType>() {
    override fun build(internal: String, init: StructTypeBuilder.() -> Unit) {
        val type = StructTypeBuilder(internal).apply(init).build(id = -1)
        cache += type
    }
}
