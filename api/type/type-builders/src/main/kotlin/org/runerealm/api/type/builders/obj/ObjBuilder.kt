package org.runerealm.api.type.builders.obj

import org.runerealm.api.type.builders.HashTypeBuilder
import org.runerealm.api.type.script.dsl.ObjPluginBuilder
import org.runerealm.game.type.obj.UnpackedObjType

public abstract class ObjBuilder : HashTypeBuilder<ObjPluginBuilder, UnpackedObjType>() {
    override fun build(internal: String, init: ObjPluginBuilder.() -> Unit) {
        val type = ObjPluginBuilder(internal).apply(init).build(id = -1)
        cache += type
    }
}
