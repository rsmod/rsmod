package org.rsmod.api.type.editors.obj

import org.rsmod.api.type.editors.TypeEditor
import org.rsmod.api.type.script.dsl.ObjPluginBuilder
import org.rsmod.game.type.obj.UnpackedObjType

public abstract class ObjEditor : TypeEditor<ObjPluginBuilder, UnpackedObjType>() {
    override fun edit(internal: String, init: ObjPluginBuilder.() -> Unit) {
        val type = ObjPluginBuilder(internal).apply(init).build(id = -1)
        cache += type
    }
}
