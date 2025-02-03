package org.rsmod.api.type.builders.obj

import org.rsmod.api.type.builders.HashTypeBuilder
import org.rsmod.api.type.script.dsl.ObjPluginBuilder
import org.rsmod.game.type.obj.UnpackedObjType

public abstract class ObjBuilder : HashTypeBuilder<ObjPluginBuilder, UnpackedObjType>() {
    override fun build(internal: String, init: ObjPluginBuilder.() -> Unit) {
        val type = ObjPluginBuilder(internal).apply(init).build(id = -1)
        cache += type
    }
}
