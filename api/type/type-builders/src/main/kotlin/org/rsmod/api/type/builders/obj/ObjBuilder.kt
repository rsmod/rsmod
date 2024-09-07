package org.rsmod.api.type.builders.obj

import org.rsmod.api.type.builders.HashTypeBuilder
import org.rsmod.game.type.obj.ObjTypeBuilder
import org.rsmod.game.type.obj.UnpackedObjType

public abstract class ObjBuilder : HashTypeBuilder<ObjTypeBuilder, UnpackedObjType>() {
    override fun build(internal: String, init: ObjTypeBuilder.() -> Unit) {
        val type = ObjTypeBuilder(internal).apply(init).build(id = -1)
        cache += type
    }
}
